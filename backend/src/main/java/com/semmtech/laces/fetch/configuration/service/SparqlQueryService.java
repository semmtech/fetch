package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.dtos.common.ColumnDto;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ColumnEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.exceptions.QueryUpdateException;
import com.semmtech.laces.fetch.configuration.repository.FindByIdInRepository;
import com.semmtech.laces.fetch.configuration.repository.SparqlQueryRepository;
import com.semmtech.laces.fetch.sparql.SparqlQueryUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QueryException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SparqlQueryService extends GenericService<SparqlQueryEntity> {
    private final AddOnConfigurationService configurationService;
    private final SparqlQueryUtils sparqlQueryUtils;

    public SparqlQueryService(
            FindByIdInRepository<SparqlQueryEntity, String> repository,
            SparqlQueryUtils sparqlQueryUtils,
            AddOnConfigurationService configurationService) {

        super(repository);
        this.sparqlQueryUtils = sparqlQueryUtils;
        this.configurationService = configurationService;
    }

    private void throwOnSyntaxErrorOnNewQuery(Throwable error) {
        log.error(error.getMessage(), error);
        throw new QueryUpdateException(error.getMessage());
    }

    public List<String> extractVariables(String query, Consumer<Throwable> exceptionHandler) {
        List<String> result = List.of();
        if (StringUtils.isNotEmpty(query)) {
            try {
                result = sparqlQueryUtils.extractBindings(query);
            }
            catch (QueryException qe) {
                exceptionHandler.accept(qe);
            }
        }
        return result;
    }

    public List<SparqlQueryEntity> getQueriesOfType(String type) {
        return ((SparqlQueryRepository) repository).findByType(type);
    }

    public Optional<SparqlQueryEntity> update(SparqlQueryEntity queryEntity) {
        final var queryId = queryEntity.getId();
        List<String> oldColumns = getColumnsByQueryId(queryId);

        Optional<SparqlQueryEntity> savedEntity = super.update(queryEntity);

        updateVariablesInConfigurations(queryEntity, queryId, oldColumns);
        refreshConfigurationCaches(queryId);

        return savedEntity;
    }

    private void refreshConfigurationCaches(String queryId) {
        log.debug("Refreshing Configurations which contain SparqlQuery {}...", queryId);
        configurationService
                .findConfigurationsByQuery(queryId)
                .forEach(configuration -> configurationService.refresh(configuration.getId()));
    }

    private void updateVariablesInConfigurations(SparqlQueryEntity queryEntity, String queryId, List<String> oldColumns) {
        final var newColumns = extractVariables(queryEntity.getQuery(), this::throwOnSyntaxErrorOnNewQuery);

        configurationService
                .findConfigurationsUsingRootsQuery(queryId)
                .forEach(configuration ->
                        applyAndStoreNewColumnEffects(configuration, oldColumns, newColumns));
    }

    public LinkedHashMap<String, ColumnDto> calculateUpdatedColumnsForNewQuery(
            String oldQueryId,
            String newQueryId,
            LinkedHashMap<String, ColumnDto> previouslySelectedColumns) {
        // When selecting a new query, we don't really care about errors in the old query.
        List<String> oldQueryColumns = getColumnsByQueryId(oldQueryId);

        // Errors in the new query do matter. We don't update the columns if the query contains errors.
        List<String> newQueryColumns = extractNewColumnsOnSelection(newQueryId);

        if (previouslySelectedColumns == null) {
            previouslySelectedColumns = new LinkedHashMap<>();
        }

        return applyNewColumnEffects(oldQueryColumns, newQueryColumns, previouslySelectedColumns, ColumnDto::new);
    }

    private List<String> extractNewColumnsOnSelection(String newQueryId) {
        return getColumnsByQueryId(newQueryId, this::throwOnSyntaxErrorOnNewQuery);
    }

    private List<String> getColumnsByQueryId(String queryId, Consumer<Throwable> errorHandler) {
        List<String> columns = new ArrayList<>();

        if (StringUtils.isNotEmpty(queryId)) {
            columns = repository.findById(queryId)
                    .map(SparqlQueryEntity::getQuery)
                    .map(query -> extractVariables(query, errorHandler))
                    .orElse(Collections.emptyList());
        }

        return columns;
    }

    private List<String> getColumnsByQueryId(String queryId) {
        return getColumnsByQueryId(
                queryId,
                throwable -> log.warn(throwable.getMessage(), throwable));
    }

    private void applyAndStoreNewColumnEffects(
            AddOnEntity configuration,
            Collection<String> oldColumns,
            Collection<String> newColumns) {

        LinkedHashMap<String, ColumnEntity> columns =
                configuration.getVisualization().getColumns();
        if (CollectionUtils.isNotEmpty(newColumns)) {
            if (columns == null) {
                columns = new LinkedHashMap<>();
            }
            LinkedHashMap<String, ColumnEntity> updatedColumns =
                    applyNewColumnEffects(oldColumns, newColumns, columns, ColumnEntity::new);

            configuration.getVisualization().setColumns(updatedColumns);

            configurationService.update(configuration);
        }
    }

    private <T> LinkedHashMap<String, T> applyNewColumnEffects(
            Collection<String> oldColumns,
            Collection<String> newColumns,
            LinkedHashMap<String, T> columns,
            Function<String, T> columnProvider) {

        Collection<String> addedColumns = CollectionUtils.subtract(newColumns, oldColumns);
        Collection<String> removedColumns = CollectionUtils.subtract(oldColumns, newColumns);

        removedColumns.forEach(columns::remove);
        addedColumns.forEach(name -> addColumn(columns, name, columnProvider));

        // Cleanup in case we are processing a correction of a previously unparseable query.
        // When the old query cannot be parsed, remove the columns that may be left-overs from a previous query.
        if (CollectionUtils.isEmpty(oldColumns)) {
            Collection<String> missingColumns = CollectionUtils.subtract(columns.keySet(), newColumns);
            missingColumns.forEach(columns::remove);
        }

        return columns;
    }

    private <T> void addColumn(LinkedHashMap<String, T> columns, String name, Function<String, T> columnProvider) {
        columns.put(name, columnProvider.apply(name));
    }

    public List<String> delete(Collection<? extends SparqlQueryEntity> objectsToDelete) {
        List<String> objectIds = objectsToDelete.stream().map(SparqlQueryEntity::getId).collect(Collectors.toList());

        List<String> results = super.delete(objectsToDelete);
        objectIds.forEach(this::refreshConfigurationCaches);

        return results;
    }
}

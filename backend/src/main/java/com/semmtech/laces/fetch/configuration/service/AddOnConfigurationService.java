package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryWithDefaultGraphs;
import com.semmtech.laces.fetch.configuration.repository.AddOnConfigurationRepository;
import com.semmtech.laces.fetch.configuration.repository.SparqlEndpointConfigurationRepository;
import com.semmtech.laces.fetch.configuration.repository.SparqlQueryRepository;
import com.semmtech.laces.fetch.configuration.repository.TargetDatasystemRepository;
import com.semmtech.laces.fetch.configuration.repository.WorkspaceConfigurationRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class AddOnConfigurationService extends GenericService<AddOnEntity> {

    private final SparqlEndpointConfigurationRepository sparqlEndpointRepository;
    private final WorkspaceConfigurationRepository workspaceRepository;
    private final SparqlQueryRepository sparqlQueryRepository;
    private final TargetDatasystemRepository targetDataSystemRepository;

    public AddOnConfigurationService(
            AddOnConfigurationRepository repository,
            SparqlEndpointConfigurationRepository sparqlEndpointRepository,
            WorkspaceConfigurationRepository workspaceRepository,
            TargetDatasystemRepository targetDataSystemRepository,
            SparqlQueryRepository sparqlQueryRepository) {

        super(repository);
        this.sparqlEndpointRepository = sparqlEndpointRepository;
        this.workspaceRepository = workspaceRepository;
        this.targetDataSystemRepository = targetDataSystemRepository;
        this.sparqlQueryRepository = sparqlQueryRepository;
    }

    public List<AddOnEntity> findConfigurationsUsingRootsQuery(String queryId) {
        return ((AddOnConfigurationRepository) repository).findByVisualizationRootsQueryQueryId(queryId);
    }

    public List<AddOnEntity> findConfigurationsByWorkspace(String workspaceId) {
        return ((AddOnConfigurationRepository) repository).findByDataTarget(workspaceId);
    }

    public List<AddOnEntity> findConfigurationsByQuery(String queryId) {
        return getAll()
                .stream()
                .filter(config -> config.hasQuery(queryId))
                .collect(Collectors.toList());
    }

    public List<AddOnEntity> findConfigurationsByTarget(String targetId) {
        return getAll()
                .stream()
                .filter(config -> config.hasTarget(targetId))
                .collect(Collectors.toList());
    }

    public List<AddOnEntity> findConfigurationsByEndpoint(String endpointId) {
        return getAll()
                .stream()
                .filter(config -> {
                    SparqlEndpointEntity endpoint = config.getSparqlEndpoint();
                    if (endpoint == null)
                        return false;
                    return endpointId.equals(endpoint.getId());
                })
                .collect(Collectors.toList());
    }


    @Override
    protected Function<AddOnEntity, AddOnEntity> save() {
        return configuration -> {
            saveParts(configuration);
            return repository.save(configuration);
        };
    }

    private void saveParts(AddOnEntity configuration) {
        updatePart(configuration::getSparqlEndpoint, configuration::setSparqlEndpoint, sparqlEndpointRepository);

        if (configuration.getVisualization() != null) {
            updatePart(getSparqlQuerySupplier(() -> configuration.getVisualization().getRootsQuery()), query -> configuration.getVisualization().getRootsQuery().setQuery(query), sparqlQueryRepository);
            updatePart(getSparqlQuerySupplier(() -> configuration.getVisualization().getChildrenQuery()), query -> configuration.getVisualization().getChildrenQuery().setQuery(query), sparqlQueryRepository);
            updatePart(configuration.getVisualization()::getTitleQuery, query -> configuration.getVisualization().setTitleQuery(query), sparqlQueryRepository);
        }

        if (configuration.getImportConfiguration() != null) {
            if (configuration.getImportConfiguration().getSteps() != null) {
                configuration.getImportConfiguration()
                        .getSteps()
                        .forEach(step -> updatePart(
                                getSparqlQuerySupplier(step::getSparqlQuery),
                                sparqlQuery -> step.getSparqlQuery().setQuery(sparqlQuery),
                                sparqlQueryRepository));
            }
        }
    }

    private Supplier<SparqlQueryEntity> getSparqlQuerySupplier(Supplier<SparqlQueryWithDefaultGraphs> sparqlQueryWithDefaultGraphsSupplier) {
        return () -> Optional.ofNullable(sparqlQueryWithDefaultGraphsSupplier.get()).map(SparqlQueryWithDefaultGraphs::getQuery).orElse(null);
    }

    private <T extends Identifiable> void updatePart(Supplier<T> partSupplier, Consumer<T> updater, MongoRepository<T, String> repository) {
        if (partSupplier.get() != null) {
            T part = partSupplier.get();
            if (part.getId() != null && !repository.existsById(part.getId()) || part.getId() == null) {
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<T>> violations = validator.validate(part);

                if (violations.isEmpty()) {
                    T saved = repository.save(part);
                    updater.accept(saved);
                } else {
                    throw new ValidationException(violations.toString());
                }
            }
        }
    }
}

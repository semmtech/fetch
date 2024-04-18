package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.dtos.common.ColumnDto;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ColumnEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import com.semmtech.laces.fetch.configuration.exceptions.QueryUpdateException;
import com.semmtech.laces.fetch.configuration.repository.FindByIdInRepository;
import com.semmtech.laces.fetch.configuration.repository.SparqlQueryRepository;
import com.semmtech.laces.fetch.sparql.SparqlQueryUtils;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SparqlQueryServiceTest {

    private final static String OLD_ID = "old";
    private final static String NEW_ID = "new";

    private final static String OLD_QUERY = "SELECT ?uri ?name {}";

    @Test
    public void whenSelectingNewQuery_oneVariableRemoved_correspondingColumnIsRemovedToo() {
        FindByIdInRepository<SparqlQueryEntity, String> repository = mock(SparqlQueryRepository.class);
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), null
                );


        when(repository.findById(OLD_ID)).thenReturn(Optional.of(SparqlQueryEntity.builder().query(OLD_QUERY).build()));
        when(repository.findById(NEW_ID)).thenReturn(Optional.of(SparqlQueryEntity.builder().query("SELECT ?uri {}").build()));

        Map<String, ColumnDto> updatedColumns = sparqlQueryService.calculateUpdatedColumnsForNewQuery(OLD_ID, NEW_ID, getOldConfiguration());

        assertThat(updatedColumns,
                hasValidEntry("uri", "Uri", false));
    }

    @Test
    public void whenSelectingNewQuery_oneVariableAdded_correspondingColumnIsAppended() {
        FindByIdInRepository<SparqlQueryEntity, String> repository = mock(SparqlQueryRepository.class);
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), null
                );


        when(repository.findById(OLD_ID)).thenReturn(Optional.of(SparqlQueryEntity.builder().query(OLD_QUERY).build()));
        when(repository.findById(NEW_ID)).thenReturn(Optional.of(SparqlQueryEntity.builder().query("SELECT ?uri ?name ?extra {}").build()));

        Map<String, ColumnDto> updatedColumns = sparqlQueryService.calculateUpdatedColumnsForNewQuery(OLD_ID, NEW_ID, getOldConfiguration());

        assertThat(updatedColumns,
                allOf(
                        hasValidEntry("uri", "Uri", false),
                        hasValidEntry("name", "Name", true),
                        hasValidEntry("extra", "extra", true)
                )
        );
    }

    @Test
    public void whenSelectingNewQuery_noVariablesChanged_columnConfigurationIsNotModified() {
        FindByIdInRepository<SparqlQueryEntity, String> repository = mock(SparqlQueryRepository.class);
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), null
                );


        when(repository.findById(OLD_ID)).thenReturn(Optional.of(SparqlQueryEntity.builder().query(OLD_QUERY).build()));
        when(repository.findById(NEW_ID)).thenReturn(Optional.of(SparqlQueryEntity.builder().query("SELECT ?uri ?name {}").build()));

        Map<String, ColumnDto> updatedColumns = sparqlQueryService.calculateUpdatedColumnsForNewQuery(OLD_ID, NEW_ID, getOldConfiguration());

        assertThat(updatedColumns,
                allOf(
                        hasValidEntry("uri", "Uri", false),
                        hasValidEntry("name", "Name", true)
                )
        );
    }

    @Test
    public void whenSelectingNewQuery_noQuerySelectedBefore_allVariablesAddedAsVisibleColumns() {
        FindByIdInRepository<SparqlQueryEntity, String> repository = mock(SparqlQueryRepository.class);
        //AddOnConfigurationService addOnConfigurationService = mock(AddOnConfigurationService.class);
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), null
                );

        when(repository.findById(NEW_ID)).thenReturn(Optional.of(SparqlQueryEntity.builder().query("SELECT ?uri ?name {}").build()));

        Map<String, ColumnDto> updatedColumns = sparqlQueryService.calculateUpdatedColumnsForNewQuery(null, NEW_ID, null);

        assertThat(updatedColumns,
                allOf(
                        hasValidEntry("uri", "uri", true),
                        hasValidEntry("name", "name", true)
                )
        );
    }

    @Test
    public void whenUpdatingAQuery_byAddingAReturnVariable_updateColumnsOfConfigurationsThatUseIt() {
        // Prepare input sparqlquery for update
        SparqlQueryEntity updatedEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?uri ?additional WHERE {}")
                        .build();

        // Prepare SparqlQuery before update
        SparqlQueryEntity originalEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?uri WHERE {}")
                        .build();
        var repository = mock(SparqlQueryRepository.class);
        when(repository.findById("id")).thenReturn(Optional.of(originalEntity));

        // Prepare the configuration of columns before update
        var columns = new LinkedHashMap<String, ColumnEntity>();
        columns.put("uri", ColumnEntity.builder().bindingName("uri").displayName("Uri").visible(false).build());
        var configuration =
                AddOnEntity.builder()
                        .visualization(VisualizationEntity.builder().columns(columns).build())
                        .build();
        AddOnConfigurationService addOnConfigurationService = mock(AddOnConfigurationService.class);
        when(addOnConfigurationService.findConfigurationsUsingRootsQuery("id")).thenReturn(List.of(configuration));

        // Prepare the service to test and perform operation to test
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), addOnConfigurationService
                );
        sparqlQueryService.update(updatedEntity);

        // Validate that the right data is saved
        verify(repository, times(1)).save(updatedEntity);

        ArgumentCaptor<AddOnEntity> updatedAddOn = ArgumentCaptor.forClass(AddOnEntity.class);
        verify(addOnConfigurationService).update(updatedAddOn.capture());

        final var value = updatedAddOn.getValue();
        assertThat(value.getVisualization(),
                hasProperty("columns",
                        allOf(
                                hasValidEntry("uri","Uri",false),
                                hasValidEntry("additional","additional",true)
                        )));
    }

    @Test
    public void whenUpdatingAnUnusedQuery_byAddingAReturnVariable_queryIsSavedNoConfigurationsAffected_noErrors() {
        // Prepare input sparqlquery for update
        SparqlQueryEntity updatedEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?uri ?additional WHERE {}")
                        .build();

        // Prepare SparqlQuery before update
        SparqlQueryEntity originalEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?uri WHERE {}")
                        .build();
        var repository = mock(SparqlQueryRepository.class);
        when(repository.findById("id")).thenReturn(Optional.of(originalEntity));

        // No configurations use this query, Spring-data's findBy methods guarantee to return empty collections and not null
        AddOnConfigurationService addOnConfigurationService = mock(AddOnConfigurationService.class);
        when(addOnConfigurationService.findConfigurationsUsingRootsQuery("id")).thenReturn(Collections.emptyList());

        // Prepare the service to test and perform operation to test
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), addOnConfigurationService
                );
        sparqlQueryService.update(updatedEntity);

        verify(repository, times(1)).save(updatedEntity);

        // Validate that no changes to configurations are saved
        verify(addOnConfigurationService, never()).update(any(AddOnEntity.class));
    }

    @Test
    public void whenUpdatingExisting_withEmptyQuery_saveExecutedWithoutErrors() {
        // Prepare input sparqlquery for update
        SparqlQueryEntity updatedEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query(null)
                        .build();

        // Prepare SparqlQuery before update
        SparqlQueryEntity originalEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?uri WHERE {}")
                        .build();
        var repository = mock(SparqlQueryRepository.class);
        when(repository.findById("id")).thenReturn(Optional.of(originalEntity));


        // Prepare the configuration of columns before update
        var columns = new LinkedHashMap<String, ColumnEntity>();
        columns.put("uri", ColumnEntity.builder().bindingName("uri").displayName("Uri").visible(false).build());
        var configuration =
                AddOnEntity.builder()
                        .visualization(VisualizationEntity.builder().columns(columns).build())
                        .build();
        AddOnConfigurationService addOnConfigurationService = mock(AddOnConfigurationService.class);
        when(addOnConfigurationService.findConfigurationsUsingRootsQuery("id")).thenReturn(List.of(configuration));

        // Prepare the service to test and perform operation to test
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), addOnConfigurationService
                );
        sparqlQueryService.update(updatedEntity);

        verify(repository, times(1)).save(updatedEntity);


        verify(addOnConfigurationService, never()).update(any());
    }

    @Test
    public void whenUpdatingExistingEmptyQuery_withNonNullQuery_saveExecutedWithoutErrors() {
        // Prepare input sparqlquery for update
        SparqlQueryEntity updatedEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?uri WHERE {}")
                        .build();

        // Prepare SparqlQuery before update
        SparqlQueryEntity originalEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query(null)
                        .build();
        var repository = mock(SparqlQueryRepository.class);
        when(repository.findById("id")).thenReturn(Optional.of(originalEntity));

        // Prepare the configuration of columns before update
        var columns = new LinkedHashMap<String, ColumnEntity>();
        var configuration =
                AddOnEntity.builder()
                        .visualization(VisualizationEntity.builder().columns(columns).build())
                        .build();
        AddOnConfigurationService addOnConfigurationService = mock(AddOnConfigurationService.class);
        when(addOnConfigurationService.findConfigurationsUsingRootsQuery("id")).thenReturn(List.of(configuration));

        // Prepare the service to test and perform operation to test
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), addOnConfigurationService
                );
        sparqlQueryService.update(updatedEntity);

        verify(repository, times(1)).save(updatedEntity);

        ArgumentCaptor<AddOnEntity> updatedAddOn = ArgumentCaptor.forClass(AddOnEntity.class);
        verify(addOnConfigurationService).update(updatedAddOn.capture());

        final var value = updatedAddOn.getValue();
        assertThat(value.getVisualization(),
                hasProperty("columns",
                        allOf(
                                hasValidEntry("uri","uri",true)
                        )));
    }

    @Test
    public void whenUpdatingExistingIncorrectQuery_withCorrectQuery_querySavedColumnsUpdated() {
        // Prepare input sparqlquery for update
        SparqlQueryEntity updatedEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?other WHERE {}")
                        .build();

        // Prepare SparqlQuery before update
        SparqlQueryEntity originalEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT")
                        .build();
        var repository = mock(SparqlQueryRepository.class);
        when(repository.findById("id")).thenReturn(Optional.of(originalEntity));

        // Prepare the configuration of columns before update
        var columns = new LinkedHashMap<String, ColumnEntity>();
        columns.put("uri", ColumnEntity.builder().bindingName("uri").displayName("Uri").visible(false).build());
        var configuration =
                AddOnEntity.builder()
                        .visualization(VisualizationEntity.builder().columns(columns).build())
                        .build();
        AddOnConfigurationService addOnConfigurationService = mock(AddOnConfigurationService.class);
        when(addOnConfigurationService.findConfigurationsUsingRootsQuery("id")).thenReturn(List.of(configuration));

        // Prepare the service to test and perform operation to test
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), addOnConfigurationService
                );
        sparqlQueryService.update(updatedEntity);

        // Validate that the right data is saved
        verify(repository, times(1)).save(updatedEntity);

        ArgumentCaptor<AddOnEntity> updatedAddOn = ArgumentCaptor.forClass(AddOnEntity.class);
        verify(addOnConfigurationService).update(updatedAddOn.capture());

        final var value = updatedAddOn.getValue();
        assertThat(value.getVisualization(),
                hasProperty("columns",
                        allOf(
                                hasValidEntry("other","other",true)
                        )));
    }

    @Test
    public void whenUpdatingExistingQuery_withIncorrectQuery_querySavedColumnsNotUpdatedAndExceptionThrown() {
        // Prepare input sparqlquery for update
        SparqlQueryEntity updatedEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT")
                        .build();

        // Prepare SparqlQuery before update
        SparqlQueryEntity originalEntity =
                SparqlQueryEntity.builder()
                        .id("id")
                        .query("SELECT ?uri WHERE {}")
                        .build();
        var repository = mock(SparqlQueryRepository.class);
        when(repository.findById("id")).thenReturn(Optional.of(originalEntity));

        // Prepare the configuration of columns before update
        var columns = new LinkedHashMap<String, ColumnEntity>();
        columns.put("uri", ColumnEntity.builder().bindingName("uri").displayName("Uri").visible(false).build());
        var configuration =
                AddOnEntity.builder()
                        .visualization(VisualizationEntity.builder().columns(columns).build())
                        .build();
        AddOnConfigurationService addOnConfigurationService = mock(AddOnConfigurationService.class);
        when(addOnConfigurationService.findConfigurationsUsingRootsQuery("id")).thenReturn(List.of(configuration));

        // Prepare the service to test and perform operation to test
        SparqlQueryService sparqlQueryService =
                new SparqlQueryService(
                        repository, new SparqlQueryUtils(), addOnConfigurationService
                );

        // Catch expected exception, instead of declaring it as expected,
        // so we can still verify that the save has occurred.
        boolean exceptionThrown = false;
        try {
            sparqlQueryService.update(updatedEntity);
        } catch (QueryUpdateException que) {
            exceptionThrown = true;
        }
        assertThat(exceptionThrown, is(true));

        // Validate that the right data is saved
        verify(repository, times(1)).save(updatedEntity);
    }

    private Matcher<Map<? extends String, ? extends ColumnDto>> hasValidEntry(String bindingName, String displayName, boolean visible) {
        return hasEntry(
                equalTo(bindingName),
                allOf(
                        hasProperty("bindingName", equalTo(bindingName)),
                        hasProperty("displayName", equalTo(displayName)),
                        hasProperty("visible", equalTo(visible))
                )
        );
    }

    private LinkedHashMap<String, ColumnDto> getOldConfiguration() {
        LinkedHashMap<String, ColumnDto> columns = new LinkedHashMap<>();
        columns.put("uri", ColumnDto.builder().bindingName("uri").displayName("Uri").visible(false).build());
        columns.put("name", ColumnDto.builder().bindingName("name").displayName("Name").visible(true).build());
        return columns;
    }
}


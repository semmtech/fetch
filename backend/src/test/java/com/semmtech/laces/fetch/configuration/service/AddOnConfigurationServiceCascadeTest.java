package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddOnConfigurationServiceCascadeTest {

    @InjectMocks
    private AddOnConfigurationService serviceUnderTest;

    @Mock
    private AddOnConfigurationRepository addOnConfigurationRepository;
    @Mock
    private SparqlEndpointConfigurationRepository sparqlEndpointRepository;
    @Mock
    private WorkspaceConfigurationRepository relaticsWorkspaceRepository;
    @Mock
    private SparqlQueryRepository sparqlQueryRepository;
    @Mock
    private TargetDatasystemRepository targetDataSystemRepository;

    @Test
    public void givenAddOnConfigurationWithExistingSparqlEndpoint_whenCreatingANewConfiguration_noNewSparqlEndpointIsCreated() {
        var configuration =
                AddOnEntity
                        .builder()
                        .sparqlEndpoint(
                                SparqlEndpointEntity
                                        .builder()
                                        .id("existing id")
                                        .build()
                        )
                        .build();
        when(sparqlEndpointRepository.existsById("existing id")).thenReturn(true);

        serviceUnderTest.create(configuration);

        verify(sparqlEndpointRepository, never()).save(any());
        verify(addOnConfigurationRepository, times(1)).save(configuration);
    }

    @Test
    public void givenAddOnConfigurationWithExistingSparqlEndpoint_whenUpdatingAConfiguration_noNewSparqlEndpointIsCreated() {
        var configuration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .sparqlEndpoint(
                                SparqlEndpointEntity
                                        .builder()
                                        .id("existing id")
                                        .build()
                        )
                        .build();

        var existingConfiguration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .build();

        when(addOnConfigurationRepository.findById("existing addon id")).thenReturn(Optional.of(existingConfiguration));
        when(sparqlEndpointRepository.existsById("existing id")).thenReturn(true);

        serviceUnderTest.update(configuration);

        verify(sparqlEndpointRepository, never()).save(any());
        verify(addOnConfigurationRepository, times(1)).save(configuration);
    }

    @Test
    public void givenAddOnConfigurationWithNewVisualization_whenUpdatingAConfiguration_newVisualizationIsCreated() {
        final var webservice = TargetDataSystemEntity
                .builder()
                .id("existing data system")
                .entryCode("code")
                .operationName("name")
                .xPathExpression("//")
                .build();
        var visualization =
                VisualizationEntity
                        .builder()
                        .additionalInputsConfiguration("webservice")
                        .build();

        var configuration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .visualization(
                                visualization
                        )
                        .name("name")
                        .build();

        var existingConfiguration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .build();

        when(addOnConfigurationRepository.findById("existing addon id")).thenReturn(Optional.of(existingConfiguration));

        when(addOnConfigurationRepository.save(configuration))
                .thenAnswer(answerWithNewId("new configuration id"));

        serviceUnderTest.update(configuration);

        verify(targetDataSystemRepository, never()).save(any());
        verify(addOnConfigurationRepository, times(1)).save(configuration);

        assertThat(configuration, hasProperty("id", equalTo("new configuration id")));
    }

    @Test
    public void givenAddOnConfigurationWithNewVisualizationAndNewQueries_whenUpdatingAConfiguration_newVisualizationAndQueriesAreCreated() {
        var rootsQuery = SparqlQueryEntity
                .builder()
                .query("sparqlquery")
                .build();

        var childrenQuery = SparqlQueryEntity
                .builder()
                .query("sparqlquery")
                .build();

        var titleQuery = SparqlQueryEntity
                .builder()
                .query("titleQuery")
                .build();

        var visualization =
                VisualizationEntity
                        .builder()
                        .additionalInputsConfiguration("webservice")
                        .rootsQuery(
                                SparqlQueryWithDefaultGraphs
                                        .builder()
                                        .query(
                                                rootsQuery
                                        )
                                        .build()
                        )
                        .childrenQuery(
                                SparqlQueryWithDefaultGraphs
                                        .builder()
                                        .query(
                                                childrenQuery
                                        )
                                        .build()
                        )
                        .titleQuery(titleQuery)
                        .build();

        var configuration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .visualization(
                                visualization
                        )
                        .name("name")
                        .build();

        var existingConfiguration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .build();

        when(addOnConfigurationRepository.findById("existing addon id")).thenReturn(Optional.of(existingConfiguration));

        when(sparqlQueryRepository.save(rootsQuery))
                .thenAnswer(answerWithNewId("roots id"));
        when(sparqlQueryRepository.save(childrenQuery))
                .thenAnswer(answerWithNewId("children id"));
        when(sparqlQueryRepository.save(titleQuery))
                .thenAnswer(answerWithNewId("title id"));

        when(addOnConfigurationRepository.save(configuration))
                .thenAnswer(answerWithNewId("new configuration id"));

        serviceUnderTest.update(configuration);

        verify(targetDataSystemRepository, never()).save(any());
        verify(sparqlQueryRepository, times(1)).save(rootsQuery);
        verify(sparqlQueryRepository, times(1)).save(childrenQuery);
        verify(sparqlQueryRepository, times(1)).save(titleQuery);
        verify(addOnConfigurationRepository, times(1)).save(configuration);

        assertThat(configuration, hasProperty("id", equalTo("new configuration id")));
        // Verify that newly created visualization is saved as part of the configuration.
        assertThat(configuration,
                hasProperty("visualization",
                        allOf(
                                hasProperty("rootsQuery",
                                        hasProperty("query",
                                                hasProperty("id", equalTo("roots id")))),
                                hasProperty("childrenQuery",
                                        hasProperty("query",
                                                hasProperty("id", equalTo("children id")))),
                                hasProperty("titleQuery",
                                        hasProperty("id", equalTo("title id"))),
                                hasProperty("additionalInputsConfiguration", equalTo("webservice"))
                        )
                )
        );
    }

    @Test
    public void givenAddOnConfigurationWithNewImportStep_whenUpdatingAConfiguration_newImportStepsAreCreated() {

        var step1 = ImportStepEntity
                .builder()
                .importTarget("")
                .build();

        var existingTarget = TargetDataSystemEntity
                .builder()
                .id("existing target")
                .build();

        var step2 = ImportStepEntity
                .builder()
                .importTarget("existing target")
                .build();

        var importConfiguration =
                ImportEntity
                        .builder()
                        .steps(List.of(step1, step2))
                        .build();

        var configuration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .importConfiguration(importConfiguration)
                        .name("name")
                        .build();

        var existingConfiguration =
                AddOnEntity
                        .builder()
                        .id("existing addon id")
                        .build();

        when(addOnConfigurationRepository.findById("existing addon id")).thenReturn(Optional.of(existingConfiguration));

        when(addOnConfigurationRepository.save(configuration))
                .thenAnswer(answerWithNewId("new configuration id"));

        serviceUnderTest.update(configuration);
        verify(addOnConfigurationRepository, times(1)).save(configuration);

        assertThat(configuration, hasProperty("id", equalTo("new configuration id")));
    }

    public Answer<Object> answerWithNewId(String newInstanceId) {
        return invocationOnMock -> {
            var saveResponseObject = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(saveResponseObject, "id", newInstanceId);
            return saveResponseObject;
        };
    }
}

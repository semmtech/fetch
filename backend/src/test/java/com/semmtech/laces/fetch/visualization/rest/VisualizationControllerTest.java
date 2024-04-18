package com.semmtech.laces.fetch.visualization.rest;

import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.configuration.service.SparqlQueryService;
import com.semmtech.laces.fetch.visualization.model.Column;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import com.semmtech.laces.fetch.visualization.model.RootsQueryResponse;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import com.semmtech.laces.fetch.visualization.service.VisualizationService;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class VisualizationControllerTest extends RestControllerTest {

    public static final String API_VISUALIZATION_ROOTS = "/api/visualization/roots";
    public static final String API_VISUALIZATION_FILTERVALUES = "/api/visualization/filtervalues";
    private static final String API_VISUALIZATION_CHILDREN = "/api/visualization/children";
    @MockBean
    private VisualizationService visualizationService;

    @MockBean
    private SparqlQueryService sparqlQueryService;

    @MockBean
    private AddOnConfigurationService addOnConfigurationService;

    @Test
    public void get_roots_invalidConfiguration_notFoundReturned() throws Exception {
        when(addOnConfigurationService.get("id")).thenReturn(Optional.empty());

        this.mockMvc.perform(
                post(API_VISUALIZATION_ROOTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .param("configurationId", "id")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void get_roots_inactiveConfiguration_notFoundReturned() throws Exception {
        String configurationId = "id";

        AddOnEntity configuration =
                AddOnEntity.builder()
                        .visualization(
                                VisualizationEntity.builder()
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphs.builder()
                                                        .query(
                                                                SparqlQueryEntity.builder()
                                                                        .query("query")
                                                                        .filterField(
                                                                                FilterFieldEntity.builder()
                                                                                        .name("Name")
                                                                                        .type("Text")
                                                                                        .query("queryId")
                                                                                        .build()
                                                                        )
                                                                        .filterField(
                                                                                FilterFieldEntity.builder()
                                                                                        .name("URI")
                                                                                        .type("SparqlQuery")
                                                                                        .query("queryId")
                                                                                        .build()
                                                                        )
                                                                        .build()
                                                        )
                                                        .build()

                                        )
                                        .build()
                        )
                        .active(false)
                        .build();

        when(addOnConfigurationService.get(configurationId)).thenReturn(Optional.of(configuration));

        this.mockMvc.perform(
                post(API_VISUALIZATION_ROOTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .param("configurationId", "id")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void get_roots_validConfiguration_succesReturned() throws Exception {
        String configurationId = "id";

        AddOnEntity configuration =
                AddOnEntity.builder()
                        .active(true)
                        .visualization(
                                VisualizationEntity.builder()
                                        .rootsQuery(
                                                SparqlQueryWithDefaultGraphs.builder()
                                                        .query(
                                                                SparqlQueryEntity.builder()
                                                                        .query("query")
                                                                        .filterField(
                                                                                FilterFieldEntity.builder()
                                                                                        .name("Name")
                                                                                        .type("Text")
                                                                                        .query("queryId")
                                                                                        .build()
                                                                        )
                                                                        .filterField(
                                                                                FilterFieldEntity.builder()
                                                                                        .name("URI")
                                                                                        .type("SparqlQuery")
                                                                                        .query("queryId")
                                                                                        .build()
                                                                        )
                                                                        .build()
                                                        )
                                                        .build()

                                        )
                                        .build()
                        )
                        .build();
        when(addOnConfigurationService.get(configurationId)).thenReturn(Optional.of(configuration));

        QueryExecutionRequest request = QueryExecutionRequest.builder().build();
        RootsQueryResponse treeNode = RootsQueryResponse.rootsResponseBuilder().build();
        when(visualizationService.executeRootQuery(configuration, request)).thenReturn(treeNode);

        this.mockMvc.perform(
                post(API_VISUALIZATION_ROOTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .param("configurationId", "id")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(not(containsString("visualizationMetadata"))))
                .andExpect(content().string(containsString("filters")));

        verify(addOnConfigurationService, times(1)).get(configurationId);
        verify(visualizationService, times(1)).executeRootQuery(configuration, request);
    }

    @Test
    public void get_children_validURIInvalidConfiguration_notFoundReturned() throws Exception {
        when(addOnConfigurationService.get("IDontExist"))
                .thenReturn(Optional.empty());

        this.mockMvc.perform(
                post(API_VISUALIZATION_CHILDREN)
                        .param("configurationId", "IDontExist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\":[]}")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void get_children_validURIAndConfiguration_queryExecuted() throws Exception {
        AddOnEntity configuration = AddOnEntity.builder().active(true).build();
        String configurationId = "IExist";
        String parentUri = "http://valid.uri.com";

        when(addOnConfigurationService.get(configurationId))
                .thenReturn(Optional.of(configuration));

        QueryResult queryResult = createTreeNode();

        Map<String, String> requestRecord = new HashMap<>();
        requestRecord.put("uri", "http://parent.uri.com");
        requestRecord.put("uuid", "parentUuid");
        QueryExecutionRequest request = QueryExecutionRequest.builder().values(Collections.singletonList(requestRecord)).build();

        when(visualizationService.executeChildQuery(configuration, request))
                .thenReturn(queryResult);

        this.mockMvc.perform(
                post(API_VISUALIZATION_CHILDREN)
                        .param("configurationId", configurationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\" : [{ \"uri\":\"http://parent.uri.com\",\"uuid\":\"parentUuid\" }]}")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.columns[0].name").value("uri"))
                .andExpect(jsonPath("$.columns[0].display").value("URI"))
                .andExpect(jsonPath("$.columns[0].show").value(true))
                .andExpect(jsonPath("$.values[0].uri").value("http://some.uri.com"));

        verify(addOnConfigurationService, times(1)).get(configurationId);
        verify(visualizationService, times(1)).executeChildQuery(configuration, request);


    }

    @Test
    public void whenGettingFilterValues_returnQueryResult() throws Exception {

        final var queryId = "queryId";
        SparqlQueryEntity query = SparqlQueryEntity.builder().id(queryId).build();
        when(sparqlQueryService.get(queryId)).thenReturn(Optional.of(query));

        final var configurationId = "configId";
        AddOnEntity addOnEntity =
                AddOnEntity.builder()
                        .active(true)
                        .sparqlEndpoint(
                                SparqlEndpointEntity.builder().build()
                        )
                        .build();
        when(addOnConfigurationService.get(configurationId)).thenReturn(Optional.of(addOnEntity));

        QueryResult result =
                QueryResult.builder()
                        .values(
                                List.of(
                                        Map.of("field1", "value1", "field2", "value2"),
                                        Map.of("field3", "value3", "field4", "value4")
                                )
                        )
                        .build();
        when(visualizationService.executeParameterLessQuery(addOnEntity, query))
                .thenReturn(result);

        this.mockMvc.perform(
                get(API_VISUALIZATION_FILTERVALUES)
                        .param("configurationId", configurationId)
                        .param("queryId", queryId)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.[0].field1").value("value1"))
                .andExpect(jsonPath("$.[0].field2").value("value2"))
                .andExpect(jsonPath("$.[1].field3").value("value3"))
                .andExpect(jsonPath("$.[1].field4").value("value4"));

    }

    @Test
    public void whenGettingFilterValues_missingQueryId_return400() throws Exception {
        String errorMessage =
                this.mockMvc.perform(
                        get(API_VISUALIZATION_FILTERVALUES)
                                .param("configurationId", "blabla")
                )
                        .andExpect(status().is4xxClientError())
                        .andReturn().getResolvedException().getMessage();
        assertThat(errorMessage, equalTo("Required String parameter 'queryId' is not present"));
    }

    @Test
    public void whenGettingFilterValues_missingConfigurationId_return400() throws Exception {
        String errorMessage =
                this.mockMvc.perform(
                        get(API_VISUALIZATION_FILTERVALUES)
                                .param("queryId", "blabla")
                )
                        .andExpect(status().is4xxClientError())
                        .andReturn().getResolvedException().getMessage();

        assertThat(errorMessage, equalTo("Required String parameter 'configurationId' is not present"));
    }

    private QueryResult createTreeNode() {
        Map<String, String> record = new HashMap<>();
        record.put("uri", "http://some.uri.com");
        record.put("uuid", "some uuid");
        List<Map<String, String>> records = Collections.singletonList(record);
        List<Column> columns =
                Collections.singletonList(
                        Column.builder()
                                .name("uri")
                                .display("URI")
                                .show(true)
                                .build()
                );

        return QueryResult.builder()
                .columns(columns)
                .values(records)
                .build();
    }
}

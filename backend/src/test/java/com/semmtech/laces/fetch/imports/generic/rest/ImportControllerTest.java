package com.semmtech.laces.fetch.imports.generic.rest;

import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.imports.generic.service.ImportService;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import com.semmtech.laces.fetch.visualization.rest.RestControllerTest;
import org.junit.Test;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.semmtech.laces.fetch.imports.generic.rest.ImportController.IMPORT_FAILED_MESSAGE;
import static com.semmtech.laces.fetch.imports.generic.rest.ImportController.IMPORT_SUCCESSFUL_MESSAGE;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ImportControllerTest extends RestControllerTest {

    @MockBean
    private AddOnConfigurationService addOnConfigurationService;

    @MockBean
    private ImportService importService;

    @Test
    public void whenConfigurationNotFound_returnsErrorMessage() throws Exception {
        when(addOnConfigurationService.get("id"))
                .thenReturn(Optional.empty());

        this.mockMvc.perform(
                post("/api/import/")
                        .param("configurationId", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\":[{\"uri\":\"test\"}]}")
        ).andExpect(jsonPath("$.[0].success").value("false"));
    }

    @Test
    public void whenConfigurationFound_returnsSuccessMessage() throws Exception {
        AddOnEntity configuration = constructAddonConfiguration(false);

        when(importService.sendData(any(QueryExecutionRequest.class), any(ImportStepEntity.class), eq(configuration), anyMap()))
                .thenAnswer(this::constructSuccessResponse);

        when(addOnConfigurationService.get("id"))
                .thenReturn(Optional.of(configuration));

        this.mockMvc.perform(
                        post("/api/import/")
                                .param("configurationId", "id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"values\":[{\"uri\":\"test\",\"treeNodeId\":\"1\"}]}")
                )
                .andExpect(jsonPath("$.[0].success").value("true"))
                .andExpect(jsonPath("$.[1].success").value("true"));

        verify(addOnConfigurationService, VerificationModeFactory.times(1)).get("id");
        verify(importService, VerificationModeFactory.times(2)).sendData(any(QueryExecutionRequest.class), any(ImportStepEntity.class), eq(configuration), anyMap());
    }

    @Test
    public void whenConfigurationFound_returnsSuccessMessage_simpleFeedback() throws Exception {

        AddOnEntity configuration = constructAddonConfiguration(true);

        when(importService.sendData(any(QueryExecutionRequest.class), any(ImportStepEntity.class), eq(configuration), anyMap()))
                .thenAnswer(this::constructSuccessResponse);

        when(addOnConfigurationService.get("id"))
                .thenReturn(Optional.of(configuration));

        this.mockMvc.perform(
                        post("/api/import/")
                                .param("configurationId", "id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"values\":[{\"uri\":\"test\",\"treeNodeId\":\"1\"}]}")
                )
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].success").value("true"))
                .andExpect(jsonPath("$.[0].successMessage").value(IMPORT_SUCCESSFUL_MESSAGE));

        verify(addOnConfigurationService, VerificationModeFactory.times(1)).get("id");
        verify(importService, VerificationModeFactory.times(2)).sendData(any(QueryExecutionRequest.class), any(ImportStepEntity.class), eq(configuration), anyMap());
    }

    @Test
    public void whenConfigurationFound_returnsFailureMessage() throws Exception {
        AddOnEntity configuration = constructAddonConfiguration(true);

        when(importService.sendData(any(QueryExecutionRequest.class), any(ImportStepEntity.class), eq(configuration), anyMap()))
                .thenAnswer(this::constructFailureResponse);

        when(addOnConfigurationService.get("id"))
                .thenReturn(Optional.of(configuration));

        this.mockMvc.perform(
                        post("/api/import/")
                                .param("configurationId", "id")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"values\":[{\"uri\":\"test\",\"treeNodeId\":\"1\"}]}")
                )
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].success").value("false"))
                .andExpect(jsonPath("$.[0].errors", hasSize(1)))
                .andExpect(jsonPath("$.[0].errors[0]").value(IMPORT_FAILED_MESSAGE));

        verify(addOnConfigurationService, VerificationModeFactory.times(1)).get("id");
        verify(importService, VerificationModeFactory.times(2)).sendData(any(QueryExecutionRequest.class), any(ImportStepEntity.class), eq(configuration), anyMap());
    }

    private AddOnEntity constructAddonConfiguration(boolean simpleFeedback) {
        final var webservice = TargetDataSystemEntity
                .builder()
                .operationName("Step1")
                .entryCode("entrycode1")
                .build();
        final var webservice2 = TargetDataSystemEntity
                .builder()
                .operationName("Step2")
                .entryCode("entrycode2")
                .build();
        return
                AddOnEntity.builder()
                        .active(true)
                        .simpleFeedback(simpleFeedback)
                        .importConfiguration(
                                ImportEntity
                                        .builder()
                                        .steps(
                                                Arrays.asList(
                                                        ImportStepEntity
                                                                .builder()
                                                                .name("name1")
                                                                .importTarget("webservice1")
                                                                .sparqlQuery(
                                                                        SparqlQueryWithDefaultGraphs
                                                                                .builder()
                                                                                .query(
                                                                                        SparqlQueryEntity
                                                                                                .builder()
                                                                                                .query("query1")
                                                                                                .build())
                                                                                .build())
                                                                .build(),
                                                        ImportStepEntity
                                                                .builder()
                                                                .name("name2")
                                                                .importTarget("webservice2")
                                                                .sparqlQuery(
                                                                        SparqlQueryWithDefaultGraphs
                                                                                .builder()
                                                                                .query(
                                                                                        SparqlQueryEntity
                                                                                                .builder()
                                                                                                .query("query2")
                                                                                                .build())
                                                                                .build())
                                                                .build()
                                                )
                                        )
                                        .build())
                        .build();
    }

    private GenericImportResponse constructSuccessResponse(InvocationOnMock invocationOnMock) {
        ImportStepEntity step = invocationOnMock.getArgument(1);
        return GenericImportResponse
                .builder()
                .success(true)
                .importStep(step.getName())
                .successMessage("Jippie!")
                .build();
    }

    private GenericImportResponse constructFailureResponse(InvocationOnMock invocationOnMock) {
        ImportStepEntity step = invocationOnMock.getArgument(1);
        return GenericImportResponse
                .builder()
                .success(false)
                .importStep(step.getName())
                .errors(List.of("I am a very annoying error"))
                .build();
    }

}

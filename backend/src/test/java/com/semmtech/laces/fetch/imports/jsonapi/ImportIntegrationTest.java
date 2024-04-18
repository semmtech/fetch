package com.semmtech.laces.fetch.imports.jsonapi;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ImportStepEntity;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEntity;
import com.semmtech.laces.fetch.configuration.service.JsonApiEndpointService;
import com.semmtech.laces.fetch.configuration.service.JsonApiService;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.imports.generic.service.ImportService;
import com.semmtech.laces.fetch.imports.jsonapi.service.JSONAPIClient;
import com.semmtech.laces.fetch.restclient.OutboundRESTRequestLogger;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(components = {JSONAPIClient.class, ImportIntegrationTest.TestConfig.class})
public class ImportIntegrationTest {

    @Autowired
    private JSONAPIClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private JsonApiService jsonApiService;

    @Autowired
    private JsonApiEndpointService jsonApiEndpointService;

    @Test
    public void whenFailingImport_errorMessageConstructed() {
        server.expect(requestTo("/URL/operation"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Neanex-Project", "project"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).body("Error message"));

        AddOnEntity configuration =
                AddOnEntity
                        .builder()
                        .dataTarget("api")
                        .build();

        ImportStepEntity step =
                ImportStepEntity
                        .builder()
                        .name("Step")
                        .importTarget("webservice")
                        .build();

        QueryResult combinedResult = QueryResult.builder().build();
        List<CommonParameter> headerParameters =
                List.of(CommonParameter.builder().id("X-Neanex-Project").type("header").value("project").build());

        when(jsonApiService.get("api")).thenReturn(Optional.of(new JsonApiEntity("","","URL")));

        when(jsonApiEndpointService.get("webservice")).thenReturn(Optional.of(new JsonApiEndpointEntity("","","operation","","Receiving")));

        GenericImportResponse genericImportResponse = client.doImport(step, configuration, combinedResult, headerParameters.stream());

        assertThat(genericImportResponse,
                allOf(
                        hasProperty("errors", hasItem(equalTo("Status: 400-Bad Request: Error message"))),
                        hasProperty("success", equalTo(false)),
                        hasProperty("importStep", equalTo("Step"))
                ));
        server.verify();
    }


    @Test
    public void whenSuccessfulImport_responseMessageConstructed() {
        server.expect(requestTo("/URL/operation"))
                .andExpect(header("X-Neanex-Project", "project"))
                .andRespond(withStatus(HttpStatus.CREATED).body("{ \"result\": [\"id1\",\"id2\"]}").contentType(MediaType.APPLICATION_JSON));

        AddOnEntity configuration =
                AddOnEntity
                        .builder()
                        .dataTarget("api")
                        .build();

        ImportStepEntity step =
                ImportStepEntity
                        .builder()
                        .importTarget("webservice")
                        .name("Step")
                        .build();

        QueryResult combinedResult = QueryResult.builder().build();
        List<CommonParameter> headerParameters =
                List.of(
                        CommonParameter.builder().id("X-Neanex-Project").type("header").value("project").build()
                );

        when(jsonApiService.get("api")).thenReturn(Optional.of(new JsonApiEntity("","","URL")));

        when(jsonApiEndpointService.get("webservice")).thenReturn(Optional.of(new JsonApiEndpointEntity("","","operation","", "Receiving")));

        GenericImportResponse genericImportResponse = client.doImport(step, configuration, combinedResult, headerParameters.stream());

        assertThat(genericImportResponse,
                allOf(
                        hasProperty("errors", hasSize(0)),
                        hasProperty("importStep", equalTo("Step")),
                        hasProperty("success", equalTo(true)),
                        hasProperty("successMessage", equalTo("Successfully created 2 instances.")),
                        hasProperty("warnings", hasSize(0))
                ));
        server.verify();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ImportService importService() {
            return mock(ImportService.class);
        }

        @Bean
        public OutboundRESTRequestLogger logger() {
            return new OutboundRESTRequestLogger();
        }

        @Bean
        public JsonApiService jsonApiService() { return mock(JsonApiService.class); }

        @Bean
        public JsonApiEndpointService jsonApiEndpointService() { return mock(JsonApiEndpointService.class); }

    }
}
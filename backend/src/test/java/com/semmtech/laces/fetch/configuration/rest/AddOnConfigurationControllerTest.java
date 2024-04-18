package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.configuration.exceptions.ItemAlreadyExistsException;
import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.configuration.service.JsonApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser("admin")
public class AddOnConfigurationControllerTest {

    public static final String MINIMAL_CONFIGURATION = "{\n" +
            "        \"id\": \"1\",\n" +
            "       \"targetType\":\"JSON API\",\n" +
            "        \"name\": \"Initial test configuration - new\",\n" +
            "        \"sparqlEndpoint\": {\n" +
            "            \"url\": \"url\",\n" +
            "            \"authenticationMethod\": {\"type\":\"NONE\" }\n" +
            "        },\n" +
            "        \"jsonApi\": { \"id\":\"1\"}, " +
            "        \"visualize\": {\n" +
            "            \"rootsSparqlQuery\": \"roots\",\n" +
            "            \"childrenSparqlQuery\": \"children\",\n" +
            "            \"titleSparqlQuery\": \"title\"\n" +
            "        }\n" +
            "    }";

    public static final String MINIMAL_CONFIGURATION_NO_ID = "{\n" +
            "       \"targetType\":\"JSON API\",\n" +
            "        \"name\": \"Initial test configuration - new\",\n" +
            "        \"sparqlEndpoint\": {\n" +
            "            \"url\": \"url\",\n" +
            "            \"authenticationMethod\": {\"type\":\"NONE\"}\n" +
            "        },\n" +
            "        \"jsonApi\": { \"id\":\"1\"}, " +
            "        \"visualize\": {\n" +
            "            \"rootsSparqlQuery\": \"roots\",\n" +
            "            \"childrenSparqlQuery\": \"children\",\n" +
            "            \"titleSparqlQuery\": \"title\"\n" +
            "        }\n" +
            "    }";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddOnConfigurationService service;

    @MockBean
    private JsonApiService jsonApiService;

    @Test
    public void allConfigurations() throws Exception {
        when(service.getAll()).thenReturn(
                Arrays.asList(
                        AddOnEntity.builder().id("1").name("Configuration 1").targetType("JSON_API").build(),
                        AddOnEntity.builder().id("2").name("Configuration 2").targetType("JSON_API").build(),
                        AddOnEntity.builder().id("3").name("Configuration 3").targetType("JSON_API").build()
                )
        );

        this.mockMvc.perform(get("/api/configurations/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Configuration 1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Configuration 2"))
                .andExpect(jsonPath("$[2].id").value("3"))
                .andExpect(jsonPath("$[2].name").value("Configuration 3"));
    }

    @Test
    public void post_noId_createdWith200() throws Exception {
        AddOnEntity posted =
                createBasicConfiguration(AddOnEntity.builder());

        when(service.create(argThat(configuration -> configuration.getId() == null)))
                .thenReturn(AddOnEntity.builder().id("1").name("configuration 1").build());
        this.mockMvc.perform(
                post("/api/configurations/")
                        .content(MINIMAL_CONFIGURATION_NO_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

    }

    @Test
    public void post_existingId_rejectedWith422() throws Exception {
        AddOnEntity posted =
                AddOnEntity.builder()
                        .id("1")
                        .name("configuration 1")
                        .sparqlEndpoint(SparqlEndpointEntity.builder().authenticationMethod(new PublicAuthenticationEntity()).build())
                        .dataTarget("1")
                        .visualization(
                                VisualizationEntity
                                        .builder()
                                        .childrenQuery(
                                                SparqlQueryWithDefaultGraphs.builder().query(SparqlQueryEntity.builder().build()).build())
                                        .rootsQuery(SparqlQueryWithDefaultGraphs.builder().query(SparqlQueryEntity.builder().build()).build())
                                        .titleQuery(SparqlQueryEntity.builder().build())
                                        .build())
                        .build();

        when(service.create(any(AddOnEntity.class)))
                .thenThrow(new ItemAlreadyExistsException(posted));
        this.mockMvc.perform(
                post("/api/configurations/")
                        .content(MINIMAL_CONFIGURATION)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("[CONFIG ALREADY EXISTS]"))
                .andExpect(jsonPath("$.message").value("Create failed, configuration with id 1 already exists."));

    }

    @Test
    public void put_existingId_updatedWith200() throws Exception {
        AddOnEntity posted =
                createBasicConfiguration(AddOnEntity.builder()
                        .id("1"));

        when(service.update(argThat(arg -> "1".equals(arg.getId()))))
                .thenReturn(Optional.of(posted));
        this.mockMvc.perform(
                put("/api/configurations/")
                        .content(MINIMAL_CONFIGURATION)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Initial test configuration - new"));

    }

    private AddOnEntity createBasicConfiguration(AddOnEntity.AddOnEntityBuilder builder) {
        return builder
                .name("Initial test configuration - new")
                .sparqlEndpoint(SparqlEndpointEntity.builder().url("url").authenticationMethod(new PublicAuthenticationEntity()).build())
                .visualization(
                        VisualizationEntity
                                .builder()
                                .childrenQuery(
                                        SparqlQueryWithDefaultGraphs
                                                .builder()
                                                .query(
                                                        SparqlQueryEntity.builder().build())
                                                .build())
                                .rootsQuery(
                                        SparqlQueryWithDefaultGraphs
                                                .builder()
                                                .query(
                                                        SparqlQueryEntity.builder().build())
                                                .build())
                                .titleQuery(
                                        SparqlQueryEntity.builder().build())
                                .build())
                .build();
    }

    @Test
    public void put_nonExistingId_respondsWith404() throws Exception {
        AddOnEntity posted = createBasicConfiguration(AddOnEntity.builder().id("1"));

        when(service.update(posted))
                .thenReturn(Optional.empty());
        this.mockMvc.perform(
                put("/api/configurations/")
                        .content(MINIMAL_CONFIGURATION)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

    }

    @Test
    public void get_existingId_returnedWith200() throws Exception {
        AddOnEntity posted = AddOnEntity.builder().id("1").dataTarget("2").targetType("JSON_API").name("configuration 1").build();

        when(service.get("1"))
                .thenReturn(Optional.of(posted));
        this.mockMvc.perform(
                get("/api/configurations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("configuration 1"));

    }

    @Test
    public void get_nonExistingId_respondsWith404() throws Exception {


        when(service.get("1"))
                .thenReturn(Optional.empty());
        this.mockMvc.perform(
                get("/api/configurations/1"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void delete_existingId_deletedWith200() throws Exception {
        AddOnEntity posted = AddOnEntity.builder().id("1").build();

        when(service.delete(argThat(arg -> arg.contains(posted))))
                .thenReturn(List.of(posted.getId()));
        this.mockMvc.perform(
                delete("/api/configurations/")
                        .content("[{\"id\":\"1\"}]")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("1"));

    }

    @Test
    public void delete_nonExistingId_respondsWith200AndEmptyCollectionResponse() throws Exception {
        AddOnEntity posted = AddOnEntity.builder().id("1").build();

        when(service.delete(argThat(arg -> arg.contains(posted))))
                .thenReturn(List.of());
        this.mockMvc.perform(
                delete("/api/configurations/")
                        .content("[{\"id\":\"1\"}]")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));

    }

    @Test
    @WithAnonymousUser
    public void getConfigurations_notAuthenticated_redirectToAuthentication() throws Exception {
        this.mockMvc.perform(
                get("/api/configurations/")
        ).andExpect(status().is3xxRedirection());
    }

}

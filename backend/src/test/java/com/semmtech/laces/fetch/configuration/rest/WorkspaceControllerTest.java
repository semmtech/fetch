package com.semmtech.laces.fetch.configuration.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.relatics.TargetDataSystemDto;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ColumnEntity;
import com.semmtech.laces.fetch.configuration.entities.ImportEntity;
import com.semmtech.laces.fetch.configuration.entities.ImportStepEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryWithDefaultGraphs;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.dtos.relatics.WorkspaceDto;
import com.semmtech.laces.fetch.configuration.repository.AddOnConfigurationRepository;
import com.semmtech.laces.fetch.configuration.repository.SparqlEndpointConfigurationRepository;
import com.semmtech.laces.fetch.configuration.repository.SparqlQueryRepository;
import com.semmtech.laces.fetch.configuration.repository.TargetDatasystemRepository;
import com.semmtech.laces.fetch.configuration.repository.WorkspaceConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.exparity.hamcrest.date.ZonedDateTimeMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser("admin")
public class WorkspaceControllerTest {

    private static final String API_WORKSPACES = "/api/workspaces";
    private final Supplier<MockHttpServletRequestBuilder> POST = () -> post(API_WORKSPACES);
    private final Supplier<MockHttpServletRequestBuilder> PUT = () -> put(API_WORKSPACES);
    private final Supplier<MockHttpServletRequestBuilder> DELETE = () -> delete(API_WORKSPACES);
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WorkspaceConfigurationRepository workspaceRepository;
    @MockBean
    private TargetDatasystemRepository targetDatasystemRepository;
    @MockBean
    private OptionalToResponseEntityMapper responseEntityMapper;
    @MockBean
    private AddOnConfigurationRepository addOnConfigurationRepository;
    @MockBean
    private SparqlEndpointConfigurationRepository sparqlEndpointConfigurationRepository;

    @MockBean
    private SparqlQueryRepository sparqlQueryRepository;

    @Test
    public void workspaceWithTargetSystemsCloned_workspaceAndTargetSystemsCreated()
            throws Exception {

        String originalWorkspaceEntityId = "workspace-entity-id";
        String cloneWorkspaceEntityId = "clone-workspace-entity-id";
        String cloneWorkspaceId = "clone-workspace-id";

        prepareWorkspaceAndConfiguration(originalWorkspaceEntityId, cloneWorkspaceEntityId, cloneWorkspaceId);

        this.mockMvc
                .perform(
                        post("/api/workspaces/" + originalWorkspaceEntityId + "/clone")
                                .param("cloneWorkspaceId", cloneWorkspaceId)
                                .param("cloneWorkspaceName", "Clone Workspace name")
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cloneWorkspaceEntityId));

        ArgumentCaptor<AddOnEntity> saveCaptor =
                ArgumentCaptor.forClass(AddOnEntity.class);
        verify(addOnConfigurationRepository, times(1)).save(saveCaptor.capture());
        AddOnEntity created = saveCaptor.getValue();
        Assert.assertNull(created.getEndDate());
        Assert.assertThat(ZonedDateTime.ofInstant(created.getStartDate().toInstant(), ZoneId.systemDefault()), ZonedDateTimeMatchers.isToday());
        Assert.assertTrue(created.isSimpleFeedback());
        verify(workspaceRepository, times(1)).save(any(WorkspaceEntity.class));
        verify(targetDatasystemRepository, times(2)).save(any(TargetDataSystemEntity.class));
    }

    @Test
    public void
    workspaceWithTargetSystemsCloned_existingWorkspace_workspaceAndTargetSystemsError()
            throws Exception {

        String originalWorkspaceEntityId = "workspace-entity-id";
        String cloneWorkspaceId = "clone-workspace-id";

        final var workspaceEntity =
                WorkspaceEntity.builder()
                        .id(originalWorkspaceEntityId)
                        .environmentId("env-id")
                        .workspaceId("workspace-id")
                        .workspaceName("workspace name")
                        .build();

        when(workspaceRepository.findById(workspaceEntity.getId())).thenReturn(Optional.of(workspaceEntity));
        when(workspaceRepository.existsByWorkspaceId(cloneWorkspaceId)).thenReturn(true);

        this.mockMvc
                .perform(
                        post("/api/workspaces/" + originalWorkspaceEntityId + "/clone")
                                .param("cloneWorkspaceId", cloneWorkspaceId)
                                .param("cloneWorkspaceName", "Clone Workspace name")
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnprocessableEntity());

        verify(workspaceRepository, times(0)).save(any(WorkspaceEntity.class));
        verify(targetDatasystemRepository, times(0)).save(any(TargetDataSystemEntity.class));
        verify(addOnConfigurationRepository, times(0)).save(any(AddOnEntity.class));
    }

    @Test
    public void
    workspaceWithTargetSystemsCreated_nonExistingWorkspace_workspaceAndTargetSystemsCreated()
            throws Exception {
        final var json =
                "{\n"
                        + "  \"id\": null,\n"
                        + "  \"name\": null,\n"
                        + "  \"environmentId\": null,\n"
                        + "  \"workspaceId\": null,\n"
                        + "  \"targetDataSystems\": [\n"
                        + "    {\n"
                        + "      \"id\": null,\n"
                        + "      \"operationName\": \"test1\",\n"
                        + "      \"entryCode\": \"entrycode\",\n"
                        + "      \"workspaceId\": null,\n"
                        + "      \"type\": \"Sending\",\n"
                        + "      \"xpathExpression\": \"//any/xpath/expression\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"id\": null,\n"
                        + "      \"operationName\": \"test2\",\n"
                        + "      \"entryCode\": \"entrycode\",\n"
                        + "      \"workspaceId\": null,\n"
                        + "      \"type\": \"Sending\",\n"
                        + "      \"xpathExpression\": \"//any/xpath/expression\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";

        when(workspaceRepository.save(any(WorkspaceEntity.class)))
                .thenReturn(WorkspaceEntity.builder().id("newId").build());

        final var targetDataSystems =
                List.of(
                        prepareTargetDataSystemEntity(null, "test1", "newId"),
                        prepareTargetDataSystemEntity(null, "test2", "newId"));
        final var savedTargetDataSystems =
                List.of(
                        prepareTargetDataSystemEntity("1", "test1", "newId"),
                        prepareTargetDataSystemEntity("2", "test2", "newId"));
        when(targetDatasystemRepository.saveAll(targetDataSystems))
                .thenReturn(savedTargetDataSystems);

        performRequest(
                json, POST, ResultMatcher.matchAll(status().isOk(), jsonPath("$.id").isNotEmpty()));

        verify(workspaceRepository, times(1)).save(any(WorkspaceEntity.class));
        verify(targetDatasystemRepository, times(1)).saveAll(targetDataSystems);
    }

    @Test
    public void workspaceWithTargetSystemsCreated_existingWorkspace_nothingCreatedAnd422Returned()
            throws Exception {
        final var json = prepareRequestBodyNewTargetSystems(withExistingObject("id"));
        when(workspaceRepository.existsById("id")).thenReturn(true);
        performRequest(json, POST, status().isUnprocessableEntity());

        verify(workspaceRepository, never()).save(any(WorkspaceEntity.class));

        verify(targetDatasystemRepository, never()).save(any(TargetDataSystemEntity.class));
    }

    @Test
    public void workspaceWithNewTargetSystemsUpdated_workspaceUpdatedAndAllTargetSystemsCreated()
            throws Exception {
        final var json =
                "{\n"
                        + "  \"id\": \"id\",\n"
                        + "  \"name\": null,\n"
                        + "  \"environmentId\": null,\n"
                        + "  \"workspaceId\": null,\n"
                        + "  \"targetDataSystems\": [\n"
                        + "    {\n"
                        + "      \"id\": null,\n"
                        + "      \"operationName\": \"test1\",\n"
                        + "      \"entryCode\": \"entrycode\",\n"
                        + "      \"workspaceId\": null,\n"
                        + "      \"type\": \"Sending\",\n"
                        + "      \"xpathExpression\": \"//any/xpath/expression\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"id\": null,\n"
                        + "      \"operationName\": \"test2\",\n"
                        + "      \"entryCode\": \"entrycode\",\n"
                        + "      \"workspaceId\": null,\n"
                        + "      \"type\": \"Sending\",\n"
                        + "      \"xpathExpression\": \"//any/xpath/expression\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";

        final var workspace = WorkspaceEntity.builder().build();
        when(workspaceRepository.findById("id")).thenReturn(Optional.of(workspace));
        when(workspaceRepository.save(any(WorkspaceEntity.class)))
                .thenReturn(WorkspaceEntity.builder().id("id").build());

        when(targetDatasystemRepository.findByWorkspaceId("id"))
                .thenReturn(Collections.emptyList());

        performRequest(json, PUT, status().isOk());

        verify(workspaceRepository, times(1)).save(any(WorkspaceEntity.class));
        verify(targetDatasystemRepository, times(1))
                .saveAll(
                        List.of(
                                prepareTargetDataSystemEntity(null, "test1", "id"),
                                prepareTargetDataSystemEntity(null, "test2", "id")));
        verify(targetDatasystemRepository, never()).deleteAll(anyIterable());
    }

    @Test
    public void
    workspaceWithExistingUnmodifiedTargetSystemsUpdated_workspaceUpdatedAndNoTargetSystemsCreatedOrUpdated()
            throws Exception {
        final var json = prepareRequestBodyExistingTargetSystems(withExistingObject("id"));

        final var workspace = WorkspaceEntity.builder().build();
        final var targets =
                List.of(
                        prepareTargetDataSystemEntity("id1", "test1", "id"),
                        prepareTargetDataSystemEntity("id2", "test2", "id"));

        when(workspaceRepository.findById("id")).thenReturn(Optional.of(workspace));
        when(workspaceRepository.save(any(WorkspaceEntity.class)))
                .thenReturn(WorkspaceEntity.builder().id("id").build());

        when(targetDatasystemRepository.findByWorkspaceId("id")).thenReturn(targets);

        performRequest(json, PUT, status().isOk());

        verify(workspaceRepository, times(1)).save(any(WorkspaceEntity.class));

        verify(targetDatasystemRepository, never()).saveAll(anyIterable());
        verify(targetDatasystemRepository, never()).deleteAll(anyIterable());
    }

    @Test
    public void
    workspaceWithNewUnchangedAndRemovedTargetSystemsUpdated_workspaceUpdatedAndNewTargetSystemsCreatedAndMissingTargetSystemsDeleted()
            throws Exception {
        final var json =
                "{\n"
                        + "  \"id\": \"id\",\n"
                        + "  \"name\": null,\n"
                        + "  \"environmentId\": null,\n"
                        + "  \"workspaceId\": null,\n"
                        + "  \"targetDataSystems\": [\n"
                        + "    {\n"
                        + "      \"id\": \"id1\",\n"
                        + "      \"operationName\": \"test1\",\n"
                        + "      \"entryCode\": \"entrycode\",\n"
                        + "      \"workspaceId\": \"id\",\n"
                        + "      \"type\": \"Sending\",\n"
                        + "      \"xpathExpression\": \"//any/xpath/expression\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"id\": \"id2\",\n"
                        + "      \"operationName\": \"test2\",\n"
                        + "      \"entryCode\": \"entrycode\",\n"
                        + "      \"workspaceId\": \"id\",\n"
                        + "      \"type\": \"Sending\",\n"
                        + "      \"xpathExpression\": \"//any/xpath/expression\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";

        final var workspace = WorkspaceEntity.builder().build();
        final var targets =
                List.of(
                        prepareTargetDataSystemEntity("id1", "test1", "id"),
                        prepareTargetDataSystemEntity("id3", "test3", "id"));

        when(targetDatasystemRepository.existsById("id3")).thenReturn(true);
        when(workspaceRepository.findById("id")).thenReturn(Optional.of(workspace));
        when(workspaceRepository.save(any(WorkspaceEntity.class)))
                .thenReturn(WorkspaceEntity.builder().id("id").build());

        when(targetDatasystemRepository.findByWorkspaceId("id")).thenReturn(targets);

        performRequest(json, PUT, status().isOk());

        verify(workspaceRepository, times(1)).save(any(WorkspaceEntity.class));

        ArgumentCaptor<List<TargetDataSystemEntity>> updatedCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(targetDatasystemRepository, times(1)).saveAll(updatedCaptor.capture());

        List<TargetDataSystemEntity> updated = updatedCaptor.getValue();
        assertThat(
                updated,
                hasItems(
                        allOf(
                                hasProperty("id", equalTo("id2")),
                                hasProperty("operationName", equalTo("test2")))));

        ArgumentCaptor<TargetDataSystemEntity> deletedCaptor =
                ArgumentCaptor.forClass(TargetDataSystemEntity.class);
        verify(targetDatasystemRepository, times(1)).delete(deletedCaptor.capture());

        TargetDataSystemEntity deleted = deletedCaptor.getValue();
        assertThat(
                deleted,
                allOf(
                        hasProperty("id", equalTo("id3")),
                        hasProperty("operationName", equalTo("test3"))));
    }

    @Test
    public void whenDeletingWorkspace_attachedTargetSystemsAreDeletedToo() throws Exception {
        final var json = prepareRequestBodyExistingTargetSystemsAsList(withExistingObject("id"));

        when(workspaceRepository.existsById("id")).thenReturn(true);
        when(targetDatasystemRepository.findByWorkspaceIdIn(List.of("id")))
                .thenReturn(
                        List.of(
                                prepareTargetDataSystemEntity("id1", "test1", "id"),
                                prepareTargetDataSystemEntity("id2", "test1", "id")));
        when(targetDatasystemRepository.existsById("id1")).thenReturn(true);
        when(targetDatasystemRepository.existsById("id2")).thenReturn(true);

        performRequest(
                json, DELETE, ResultMatcher.matchAll(content().json("[\"id\"]"), status().isOk()));

        verify(workspaceRepository, times(1))
                .delete(argThat(object -> StringUtils.equals(object.getId(), "id")));
        verify(targetDatasystemRepository, times(2))
                .delete(argThat(object -> StringUtils.containsAny(object.getId(), "id1", "id2")));
    }

    private void performRequest(
            String json,
            Supplier<MockHttpServletRequestBuilder> requestBuilder,
            ResultMatcher expectedResult)
            throws Exception {
        this.mockMvc
                .perform(
                        requestBuilder
                                .get()
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                .andExpect(expectedResult);
    }

    private String prepareRequestBodyNewTargetSystems(
            WorkspaceDto.WorkspaceDtoBuilder workspaceConfigurationDTOBuilder)
            throws JsonProcessingException {
        final var configuration =
                prepareWorkspace(workspaceConfigurationDTOBuilder, null, null, null);

        return new ObjectMapper().writeValueAsString(configuration);
    }

    private String prepareRequestBodyExistingTargetSystems(
            WorkspaceDto.WorkspaceDtoBuilder workspaceConfigurationDTOBuilder)
            throws JsonProcessingException {
        final var configuration =
                prepareWorkspace(workspaceConfigurationDTOBuilder, "id1", "id2", "id");

        return new ObjectMapper().writeValueAsString(configuration);
    }

    private String prepareRequestBodyExistingTargetSystemsAsList(
            WorkspaceDto.WorkspaceDtoBuilder workspaceConfigurationDTOBuilder)
            throws JsonProcessingException {
        final var configurationList =
                List.of(prepareWorkspace(workspaceConfigurationDTOBuilder, "id1", "id2", "id"));

        return new ObjectMapper().writeValueAsString(configurationList);
    }

    private WorkspaceDto prepareWorkspace(
            WorkspaceDto.WorkspaceDtoBuilder workspaceConfigurationDTOBuilder,
            String id1,
            String id2,
            String workspaceId) {
        return workspaceConfigurationDTOBuilder
                .targetDataSystem(prepareTargetDataSystem(id1, "test1", workspaceId))
                .targetDataSystem(prepareTargetDataSystem(id2, "test2", workspaceId))
                .build();
    }

    private TargetDataSystemEntity prepareTargetDataSystemEntity(
            String id, String operationName, String wid) {
        return TargetDataSystemEntity.builder()
                .id(id)
                .operationName(operationName)
                .workspaceId(wid)
                .type("Sending")
                .xPathExpression("//any/xpath/expression")
                .entryCode("entrycode")
                .build();
    }

    private TargetDataSystemDto prepareTargetDataSystem(
            String id, String operationName, String wid) {
        return TargetDataSystemDto.builder()
                .id(id)
                .operationName(operationName)
                .workspaceId(wid)
                .type("Sending")
                .xPathExpression("//any/xpath/expression")
                .entryCode("entrycode")
                .build();
    }

    private WorkspaceDto.WorkspaceDtoBuilder withNewObject() {
        return WorkspaceDto.builder();
    }

    private WorkspaceDto.WorkspaceDtoBuilder withExistingObject(String id) {
        return WorkspaceDto.builder().id(id);
    }

    private void prepareWorkspaceAndConfiguration(String originalWorkspaceEntityId, String cloneWorkspaceEntityId, String cloneWorkspaceId) {
        final var workspaceEntity =
                WorkspaceEntity.builder()
                        .id(originalWorkspaceEntityId)
                        .environmentId("env-id")
                        .workspaceId("workspace-id")
                        .workspaceName("workspace name")
                        .build();
        final var clonedWorkspaceEntity =
                WorkspaceEntity.builder()
                        .id(cloneWorkspaceEntityId)
                        .workspaceId(cloneWorkspaceId)
                        .environmentId("env-id")
                        .workspaceName("Clone Workspace name")
                        .build();

        final var originalTDSEntities =
                List.of(
                        prepareTargetDataSystemEntity("tds-1", "test1", workspaceEntity.getId()),
                        prepareTargetDataSystemEntity("tds-2", "test2", workspaceEntity.getId()));
        final var clonedTargetDataSystems =
                List.of(
                        prepareTargetDataSystemEntity(
                                "cloned-tds-1", "test1", clonedWorkspaceEntity.getId()),
                        prepareTargetDataSystemEntity(
                                "clone-tds-2", "test2", clonedWorkspaceEntity.getId()));

        SparqlEndpointEntity originalEndpoint =
                SparqlEndpointEntity.builder()
                        .id("endpoint-id")
                        .name("endpoint name")
                        .url("http://laces.fetch/a")
                        .build();

        SparqlQueryEntity originalQueryEntity =
                SparqlQueryEntity.builder()
                        .id("query-1")
                        .name("Query 1")
                        .description("Query 1 desc")
                        .query("query-1")
                        .build();

        SparqlQueryWithDefaultGraphs originalSparqlQueryWithDefaultGraphs1 =
                SparqlQueryWithDefaultGraphs.builder()
                        .defaultGraphs(List.of("a"))
                        .query(originalQueryEntity)
                        .build();

        ImportStepEntity originalImportStepEntity1 =
                ImportStepEntity.builder()
                        .importTarget(originalTDSEntities.get(0).getId())
                        .name("Import step 1")
                        .sparqlQuery(originalSparqlQueryWithDefaultGraphs1)
                        .build();

        ImportEntity originalImportEntity =
                ImportEntity.builder().steps(List.of(originalImportStepEntity1)).build();

        LinkedHashMap<String, ColumnEntity> originalColumns = new LinkedHashMap<>();
        originalColumns.put(
                "p",
                ColumnEntity.builder()
                        .bindingName("p")
                        .displayName("property")
                        .visible(true)
                        .build());

        VisualizationEntity originalVisualizationEntity =
                VisualizationEntity.builder()
                        .additionalInputsConfiguration(originalTDSEntities.get(0).getId())
                        .childrenQuery(originalSparqlQueryWithDefaultGraphs1)
                        .rootsQuery(originalSparqlQueryWithDefaultGraphs1)
                        .titleQuery(originalQueryEntity)
                        .columns(originalColumns)
                        .enablePagination(true)
                        .build();

        AddOnEntity originalAddonEntity =
                AddOnEntity.builder()
                        .id("conf-1")
                        .active(true)
                        .simpleFeedback(true)
                        .dataTarget(workspaceEntity.getId())
                        .description("description")
                        .displayName("display name")
                        .endDate(new Date(0))
                        .importConfiguration(originalImportEntity)
                        .name("entity name")
                        .sparqlEndpoint(originalEndpoint)
                        .startDate(new Date(0))
                        .targetType("Relatics")
                        .visualization(originalVisualizationEntity)
                        .build();


        when(workspaceRepository.findById(workspaceEntity.getId())).thenReturn(Optional.of(workspaceEntity));
        when(workspaceRepository.existsByWorkspaceId(clonedWorkspaceEntity.getWorkspaceId())).thenReturn(false);
        when(targetDatasystemRepository.findByWorkspaceId(workspaceEntity.getId()))
                .thenReturn(originalTDSEntities);
        when(sparqlEndpointConfigurationRepository.existsById(originalEndpoint.getId())).thenReturn(true);
        when(sparqlQueryRepository.existsById(originalQueryEntity.getId())).thenReturn(true);
        when(addOnConfigurationRepository.findByDataTarget(workspaceEntity.getId()))
                .thenReturn(List.of(originalAddonEntity));

        when(workspaceRepository.save(any(WorkspaceEntity.class)))
                .thenReturn(clonedWorkspaceEntity);
        when(targetDatasystemRepository.save(any(TargetDataSystemEntity.class)))
                .thenReturn(clonedTargetDataSystems.get(0),
                        clonedTargetDataSystems.get(1));

        originalAddonEntity.setName("Clone Addon entity");
        originalAddonEntity.setId("clone-conf-1");
        when(addOnConfigurationRepository.save(any(AddOnEntity.class)))
                .thenReturn(originalAddonEntity);
    }
}

package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.repository.EnvironmentRepository;
import com.semmtech.laces.fetch.configuration.repository.WorkspaceConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.verification.VerificationMode;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser("admin")
public class EnvironmentControllerTest {

    private static final String DELETE_OBJECTS_REQUEST = "\n" +
            "[\n" +
            "\t{\n" +
            "\t\t\"id\":\"id1\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"id\":\"id2\"\n" +
            "\t}\n" +
            "]";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnvironmentRepository environmentRepository;

    @MockBean
    private WorkspaceConfigurationRepository workspaceRepository;

    @Test
    public void whenDeletingEnvironments_noLinkedWorkspacesExist_environmentIsDeletedWithOK() throws Exception {
        performDeleteTest(
                withoutExistingWorkspaces(),
                verifyDeletedCalledOncePerEnvironment(),
                responseIsOk()
        )
        .andExpect(MockMvcResultMatchers.content().string("[\"id1\",\"id2\"]"));
    }

    @Test
    public void whenDeletingEnvironments_linkedWorkspacesExist_environmentIsNotDeletedWithBadRequest() throws Exception {
        performDeleteTest(
                withExistingWorkspaces(),
                nothingIsDeleted(),
                responseIsBadRequest()
        )
        .andExpect(jsonPath("$.code", equalTo("The requested object cannot be deleted.")))
        .andExpect(jsonPath("$.message", equalTo("The environment is still linked to workspaces. Please, delete these workspaces first.")));
    }

    private ResultMatcher responseIsBadRequest() {
        return status().isBadRequest();
    }

    private VerificationMode nothingIsDeleted() {
        return never();
    }

    private List<WorkspaceEntity> withExistingWorkspaces() {
        return List.of(
                WorkspaceEntity.builder().environmentId("id1").build(),
                WorkspaceEntity.builder().environmentId("id2").build()
        );
    }

    private ResultMatcher responseIsOk() {
        return status().isOk();
    }

    private List<WorkspaceEntity> withoutExistingWorkspaces() {
        return Collections.emptyList();
    }

    private VerificationMode verifyDeletedCalledOncePerEnvironment() {
        return times(1);
    }

    private ResultActions performDeleteTest(List<WorkspaceEntity> existingWorkspaces, VerificationMode verificationMode, ResultMatcher status) throws Exception {
        when(workspaceRepository.findByEnvironmentIdIn(List.of("id1", "id2")))
                .thenReturn(existingWorkspaces);

        when(environmentRepository.existsById("id1")).thenReturn(true);
        when(environmentRepository.existsById("id2")).thenReturn(true);

        final var resultActions =
                this.mockMvc.perform(
                    delete("/api/environments")
                            .content(DELETE_OBJECTS_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status);

        verify(environmentRepository, verificationMode).delete(argThat(object -> StringUtils.equals(object.getId(), "id1")));
        verify(environmentRepository, verificationMode).delete(argThat(object -> StringUtils.equals(object.getId(), "id2")));

        return resultActions;
    }

}


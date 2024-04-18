package com.semmtech.security;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.EnvironmentEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.configuration.service.RelaticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser("admin")
public class SecurityConfigurationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddOnConfigurationService addOnConfigurationService;

    @MockBean
    private RelaticsService relaticsService;


    @Test
    public void noReferer_headerReturned_DENY() throws Exception {
        this.mockMvc.perform(get("/add-on/index.html"))
                .andExpect(header().string("X-Frame-Options", "DENY"));
    }

    @Test
    public void validReferer_headerReturned_ALLOW() throws Exception {
        prepareMockRequests("654321098765432109876543210987654321");

        this.mockMvc.perform(
                get("/add-on/index.html?configurationId=1")
                        .header("referer", "https://some.relaticsonline.com/?WID=654321098765432109876543210987654321&EID=123456789-123456789-123456789--23456"))
                .andExpect(header().string("X-Frame-Options", "ALLOW-FROM https://some.relaticsonline.com"));
    }

    @Test
    public void refererWithIncorrectWID_noHeaderReturned_errorMessage() throws Exception {
        prepareMockRequests("123456789-123456789-123456789--23456");
        this.mockMvc.perform(
                get("/add-on/index.html?configurationId=1")
                        .header("referer", "https://some.relaticsonline.com/?WID=654321098765432109876543210987654321&EID=123456789-123456789-123456789--23456"))
                .andExpect(content().string("Relatics environment, workspace or url does not match for configuration 1." + System.lineSeparator()));
    }

    private void prepareMockRequests(String workspaceId) {
        when(addOnConfigurationService.get("1")).thenReturn(
                Optional.of(AddOnEntity.builder().dataTarget("workspace1").build())
        );

        when(relaticsService.getWorkspaceForConfiguration(
                argThat(
                        configuration -> "workspace1".equals(configuration.getDataTarget())
                )
        ))
        .thenReturn(
                Optional.of(
                        WorkspaceEntity.builder()
                                .environmentId("envId")
                                .workspaceId(workspaceId)
                                .build()
                )
        );

        when(relaticsService.getEnvironmentForWorkspace(
                argThat(
                        workspace -> "envId".equals(workspace.getEnvironmentId())
                )
        ))
        .thenReturn(
                EnvironmentEntity.builder()
                        .serviceUrl("https://some.relaticsonline.com")
                        .environmentId("123456789-123456789-123456789--23456")
                        .build()
        );
    }
}

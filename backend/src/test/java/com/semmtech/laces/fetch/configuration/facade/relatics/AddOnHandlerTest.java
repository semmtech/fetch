package com.semmtech.laces.fetch.configuration.facade.relatics;

import com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto;
import com.semmtech.laces.fetch.configuration.dtos.common.ImportStepDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.TargetDataSystemDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.VisualizationDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.WorkspaceDto;
import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.facade.ServiceRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AddOnHandlerTest {

    ServiceRegistry<AddOnDto> addOnHandlerRegistry;
    GenericService<AddOnEntity> addOnConfigurationGenericService;
    GenericService<WorkspaceEntity> workspaceGenericService;
    GenericService<TargetDataSystemEntity> targetDataSystemGenericService;
    AddOnHandler handler;

    @Before
    public void setupServices() {
        addOnHandlerRegistry = new ServiceRegistry<>();
        addOnConfigurationGenericService = mock(GenericService.class);
        workspaceGenericService = mock(GenericService.class);
        targetDataSystemGenericService = mock(GenericService.class);
        handler = new AddOnHandler(addOnHandlerRegistry, addOnConfigurationGenericService, workspaceGenericService, targetDataSystemGenericService);
    }

    @Test
    public void whenHandlingCreateWithVisualization_addOnAndAspectsAreCreated() {
        AddOnEntity configuration = AddOnEntity.builder().id("0").build();
        WorkspaceEntity workspace = WorkspaceEntity.builder().id("1").build();
        TargetDataSystemEntity targetDataSystem = TargetDataSystemEntity.builder().id("2").build();

        com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.addOnDtoBuilder()
                        .description("Description")
                        .name("Name")
                        .workspace(
                                WorkspaceDto.builder()
                                        .build()
                        )
                        .visualization(
                                VisualizationDto.visualizationDtoBuilder()
                                        .additionalInputs(
                                                TargetDataSystemDto.builder()
                                                        .operationName("test ep")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.create(any(AddOnEntity.class))).thenReturn(configuration);
        when(workspaceGenericService.create(any(WorkspaceEntity.class))).thenReturn(workspace);
        when(targetDataSystemGenericService.create(any(TargetDataSystemEntity.class))).thenReturn(targetDataSystem);

        AddOnDto savedDto = addOnHandlerRegistry.create(addOnDto);
        assertThat(savedDto, instanceOf(com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.class));
        com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto savedAddOnDto = (com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto) savedDto;
        assertThat(savedAddOnDto, hasProperty("id", equalTo("0")));
        assertThat(savedAddOnDto.getVisualization(),
                hasProperty("additionalInputs",
                        hasProperty("id", equalTo("2"))));
        assertThat(savedAddOnDto, hasProperty("workspace",
                hasProperty("id", equalTo("1"))));

        verify(workspaceGenericService, times(1)).create(any(WorkspaceEntity.class));
        verify(targetDataSystemGenericService, times(1)).create(any(TargetDataSystemEntity.class));
        verify(addOnConfigurationGenericService, times(1)).create(any(AddOnEntity.class));
    }

    @Test
    public void whenHandlingCreateWithImportSteps_addOnAndAspectsAreCreated() {
        AddOnEntity configuration = AddOnEntity.builder().id("0").build();
        WorkspaceEntity workspace = WorkspaceEntity.builder().id("1").build();
        TargetDataSystemEntity targetDataSystem = TargetDataSystemEntity.builder().id("2").build();

        com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.addOnDtoBuilder()
                        .description("Description")
                        .name("Name")
                        .workspace(
                                WorkspaceDto.builder()
                                        .build()
                        )
                        .importStep(
                                ImportStepDto.<TargetDataSystemDto> builder()
                                        .importTarget(
                                                TargetDataSystemDto.builder()
                                                        .operationName("test ep")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.create(any(AddOnEntity.class))).thenReturn(configuration);
        when(workspaceGenericService.create(any(WorkspaceEntity.class))).thenReturn(workspace);
        when(targetDataSystemGenericService.create(any(TargetDataSystemEntity.class))).thenReturn(targetDataSystem);

        AddOnDto savedDto = addOnHandlerRegistry.create(addOnDto);
        assertThat(savedDto, instanceOf(com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.class));
        com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto savedAddOnDto = (com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto) savedDto;
        assertThat(savedAddOnDto, hasProperty("id", equalTo("0")));
        assertThat(savedAddOnDto,
                hasProperty("importSteps",
                        hasItem(
                                hasProperty("importTarget",
                                        hasProperty("id", equalTo("2"))))));
        assertThat(savedAddOnDto, hasProperty("workspace",
                hasProperty("id", equalTo("1"))));

        verify(workspaceGenericService, times(1)).create(any(WorkspaceEntity.class));
        verify(targetDataSystemGenericService, times(1)).create(any(TargetDataSystemEntity.class));
        verify(addOnConfigurationGenericService, times(1)).create(any(AddOnEntity.class));
    }

    @Test
    public void whenHandlingUpdateWithImportSteps_addOnAndAspectsAreUpdated() {
        AddOnEntity configuration =
                AddOnEntity.builder()
                        .id("0")
                        .importConfiguration(
                                ImportEntity.builder()
                                        .steps(
                                                List.of(
                                                        ImportStepEntity.builder()
                                                                .name("Test step")
                                                                .importTarget("targetId")
                                                                .build()
                                                )
                                        )
                                        .build()
                        ).build();

        WorkspaceEntity jsonApiEntity = WorkspaceEntity.builder().id("1").build();
        TargetDataSystemEntity jsonApiEndpointEntity = TargetDataSystemEntity.builder().id("targetId").build();

        com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.addOnDtoBuilder()
                        .id("addOnId")
                        .description("Description")
                        .name("Name")
                        .workspace(
                                WorkspaceDto.builder()
                                        .id("apiId")
                                        .build()
                        )
                        .importStep(
                                ImportStepDto.<TargetDataSystemDto> builder()
                                        .importTarget(
                                                TargetDataSystemDto.builder()
                                                        .id("apiEndpointId")
                                                        .operationName("test ep")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.update(any(AddOnEntity.class))).thenReturn(Optional.of(configuration));
        when(workspaceGenericService.update(any(WorkspaceEntity.class))).thenReturn(Optional.of(jsonApiEntity));
        when(targetDataSystemGenericService.update(any(TargetDataSystemEntity.class))).thenReturn(Optional.of(jsonApiEndpointEntity));

        when(targetDataSystemGenericService.get("targetId")).thenReturn(Optional.of(jsonApiEndpointEntity));

        Optional<AddOnDto> optionalSavedDto = addOnHandlerRegistry.update(addOnDto);
        assertThat(optionalSavedDto.isPresent(), equalTo(true));

        verify(workspaceGenericService, times(1)).update(any(WorkspaceEntity.class));
        verify(targetDataSystemGenericService, times(1)).update(any(TargetDataSystemEntity.class));
        ArgumentCaptor<AddOnEntity> captor = ArgumentCaptor.forClass(AddOnEntity.class);
        verify(addOnConfigurationGenericService, times(1)).update(captor.capture());

        AddOnEntity captured = captor.getValue();
        assertThat(captured,
                hasProperty("importConfiguration",
                        hasProperty("steps",
                                hasItem(
                                        hasProperty("importTarget", equalTo("apiEndpointId"))))));

    }

    @Test
    public void whenHandlingUpdateWithVisualization_addOnAndAspectsAreUpdated() {
        AddOnEntity configuration = AddOnEntity.builder().id("0").visualization(VisualizationEntity.builder().additionalInputsConfiguration("2").build()).build();
        WorkspaceEntity workspace = WorkspaceEntity.builder().id("1").build();
        TargetDataSystemEntity targetDataSystem = TargetDataSystemEntity.builder().id("2").build();

        com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.addOnDtoBuilder()
                        .id("addOnId")
                        .description("Description")
                        .name("Name")
                        .workspace(
                                WorkspaceDto.builder()
                                        .id("apiId")
                                        .build()
                        )
                        .visualization(
                                VisualizationDto.visualizationDtoBuilder()
                                        .additionalInputs(
                                                TargetDataSystemDto.builder()
                                                        .id("apiEndpointId")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.update(any(AddOnEntity.class))).thenReturn(Optional.of(configuration));
        when(workspaceGenericService.update(any(WorkspaceEntity.class))).thenReturn(Optional.of(workspace));
        when(targetDataSystemGenericService.update(any(TargetDataSystemEntity.class))).thenReturn(Optional.of(targetDataSystem));

        Optional<AddOnDto> optionalSavedDto = addOnHandlerRegistry.update(addOnDto);
        assertThat(optionalSavedDto.isPresent(), equalTo(true));

        verify(workspaceGenericService, times(1)).update(any(WorkspaceEntity.class));
        verify(targetDataSystemGenericService, times(1)).update(any(TargetDataSystemEntity.class));
        ArgumentCaptor<AddOnEntity> captor = ArgumentCaptor.forClass(AddOnEntity.class);
        verify(addOnConfigurationGenericService, times(1)).update(captor.capture());

        AddOnEntity captured = captor.getValue();
        assertThat(captured,
                hasProperty("visualization",
                        hasProperty("additionalInputsConfiguration", equalTo("apiEndpointId"))));

    }

}

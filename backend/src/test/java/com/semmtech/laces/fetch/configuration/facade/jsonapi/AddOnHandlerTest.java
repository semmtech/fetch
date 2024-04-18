package com.semmtech.laces.fetch.configuration.facade.jsonapi;

import com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto;
import com.semmtech.laces.fetch.configuration.dtos.common.ImportStepDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiEndpointDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.VisualizationDto;
import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.facade.ServiceRegistry;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AddOnHandlerTest {

    @Test
    public void whenHandlingCreateWithVisualization_addOnAndAspectsAreCreated() {
        ServiceRegistry<AddOnDto> addOnHandlerRegistry = new ServiceRegistry<>();
        GenericService<AddOnEntity> addOnConfigurationGenericService = mock(GenericService.class);
        GenericService<JsonApiEntity> jsonApiGenericService = mock(GenericService.class);
        GenericService<JsonApiEndpointEntity> jsonApiEndpointGenericService = mock(GenericService.class);
        AddOnHandler handler = new AddOnHandler(addOnHandlerRegistry, addOnConfigurationGenericService, jsonApiGenericService, jsonApiEndpointGenericService);

        AddOnEntity configuration = AddOnEntity.builder().id("0").build();
        JsonApiEntity jsonApiEntity = JsonApiEntity.builder().id("1").build();
        JsonApiEndpointEntity jsonApiEndpointEntity = JsonApiEndpointEntity.builder().id("2").build();

        com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.addOnDtoBuilder()
                        .description("Description")
                        .name("Name")
                        .jsonApi(
                                JsonApiDto.builder()
                                        .build()
                        )
                        .visualization(
                                VisualizationDto.visualizationDtoBuilder()
                                        .additionalInputs(
                                                JsonApiEndpointDto.builder()
                                                        .name("test ep")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.create(any(AddOnEntity.class))).thenReturn(configuration);
        when(jsonApiGenericService.create(any(JsonApiEntity.class))).thenReturn(jsonApiEntity);
        when(jsonApiEndpointGenericService.create(any(JsonApiEndpointEntity.class))).thenReturn(jsonApiEndpointEntity);

        AddOnDto savedDto = addOnHandlerRegistry.create(addOnDto);
        assertThat(savedDto, instanceOf(com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.class));
        com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto savedAddOnDto = (com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto) savedDto;
        assertThat(savedAddOnDto, hasProperty("id", equalTo("0")));
        assertThat(savedAddOnDto.getVisualization(),
                hasProperty("additionalInputs",
                        hasProperty("id", equalTo("2"))));
        assertThat(savedAddOnDto, hasProperty("jsonApi",
                hasProperty("id", equalTo("1"))));

        verify(jsonApiGenericService, times(1)).create(any(JsonApiEntity.class));
        verify(jsonApiEndpointGenericService, times(1)).create(any(JsonApiEndpointEntity.class));
        verify(addOnConfigurationGenericService, times(1)).create(any(AddOnEntity.class));
    }

    @Test
    public void whenHandlingCreateWithImportSteps_addOnAndAspectsAreCreated() {
        ServiceRegistry<AddOnDto> addOnHandlerRegistry = new ServiceRegistry<>();
        GenericService<AddOnEntity> addOnConfigurationGenericService = mock(GenericService.class);
        GenericService<JsonApiEntity> jsonApiGenericService = mock(GenericService.class);
        GenericService<JsonApiEndpointEntity> jsonApiEndpointGenericService = mock(GenericService.class);
        AddOnHandler handler = new AddOnHandler(addOnHandlerRegistry, addOnConfigurationGenericService, jsonApiGenericService, jsonApiEndpointGenericService);

        AddOnEntity configuration = AddOnEntity.builder().id("0").build();
        JsonApiEntity jsonApiEntity = JsonApiEntity.builder().id("1").build();
        JsonApiEndpointEntity jsonApiEndpointEntity = JsonApiEndpointEntity.builder().id("2").build();

        com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.addOnDtoBuilder()
                        .description("Description")
                        .name("Name")
                        .jsonApi(
                                JsonApiDto.builder()
                                        .build()
                        )
                        .importStep(
                                ImportStepDto.<JsonApiEndpointDto> builder()
                                        .importTarget(
                                                JsonApiEndpointDto.builder()
                                                        .name("test ep")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.create(any(AddOnEntity.class))).thenReturn(configuration);
        when(jsonApiGenericService.create(any(JsonApiEntity.class))).thenReturn(jsonApiEntity);
        when(jsonApiEndpointGenericService.create(any(JsonApiEndpointEntity.class))).thenReturn(jsonApiEndpointEntity);

        AddOnDto savedDto = addOnHandlerRegistry.create(addOnDto);
        assertThat(savedDto, instanceOf(com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.class));
        com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto savedAddOnDto = (com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto) savedDto;
        assertThat(savedAddOnDto, hasProperty("id", equalTo("0")));
        assertThat(savedAddOnDto,
                hasProperty("importSteps",
                        hasItem(
                                hasProperty("importTarget",
                                        hasProperty("id", equalTo("2"))))));
        assertThat(savedAddOnDto, hasProperty("jsonApi",
                hasProperty("id", equalTo("1"))));

        verify(jsonApiGenericService, times(1)).create(any(JsonApiEntity.class));
        verify(jsonApiEndpointGenericService, times(1)).create(any(JsonApiEndpointEntity.class));
        verify(addOnConfigurationGenericService, times(1)).create(any(AddOnEntity.class));
    }

    @Test
    public void whenHandlingUpdateWithImportSteps_addOnAndAspectsAreUpdated() {
        ServiceRegistry<AddOnDto> addOnHandlerRegistry = new ServiceRegistry<>();
        GenericService<AddOnEntity> addOnConfigurationGenericService = mock(GenericService.class);
        GenericService<JsonApiEntity> jsonApiGenericService = mock(GenericService.class);
        GenericService<JsonApiEndpointEntity> jsonApiEndpointGenericService = mock(GenericService.class);
        AddOnHandler handler = new AddOnHandler(addOnHandlerRegistry, addOnConfigurationGenericService, jsonApiGenericService, jsonApiEndpointGenericService);

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

        JsonApiEntity jsonApiEntity = JsonApiEntity.builder().id("1").build();
        JsonApiEndpointEntity jsonApiEndpointEntity = JsonApiEndpointEntity.builder().id("targetId").build();

        com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.addOnDtoBuilder()
                        .id("addOnId")
                        .description("Description")
                        .name("Name")
                        .jsonApi(
                                JsonApiDto.builder()
                                        .id("apiId")
                                        .build()
                        )
                        .importStep(
                                ImportStepDto.<JsonApiEndpointDto> builder()
                                        .importTarget(
                                                JsonApiEndpointDto.builder()
                                                        .id("apiEndpointId")
                                                        .name("test ep")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.update(any(AddOnEntity.class))).thenReturn(Optional.of(configuration));
        when(jsonApiGenericService.update(any(JsonApiEntity.class))).thenReturn(Optional.of(jsonApiEntity));
        when(jsonApiEndpointGenericService.update(any(JsonApiEndpointEntity.class))).thenReturn(Optional.of(jsonApiEndpointEntity));

        when(jsonApiEndpointGenericService.get("targetId")).thenReturn(Optional.of(jsonApiEndpointEntity));

        Optional<AddOnDto> optionalSavedDto = addOnHandlerRegistry.update(addOnDto);
        assertThat(optionalSavedDto.isPresent(), equalTo(true));

        verify(jsonApiGenericService, times(1)).update(any(JsonApiEntity.class));
        verify(jsonApiEndpointGenericService, times(1)).update(any(JsonApiEndpointEntity.class));
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
        ServiceRegistry<AddOnDto> addOnHandlerRegistry = new ServiceRegistry<>();
        GenericService<AddOnEntity> addOnConfigurationGenericService = mock(GenericService.class);
        GenericService<JsonApiEntity> jsonApiGenericService = mock(GenericService.class);
        GenericService<JsonApiEndpointEntity> jsonApiEndpointGenericService = mock(GenericService.class);
        AddOnHandler handler = new AddOnHandler(addOnHandlerRegistry, addOnConfigurationGenericService, jsonApiGenericService, jsonApiEndpointGenericService);

        AddOnEntity configuration = AddOnEntity.builder().id("0").visualization(VisualizationEntity.builder().additionalInputsConfiguration("2").build()).build();
        JsonApiEntity jsonApiEntity = JsonApiEntity.builder().id("1").build();
        JsonApiEndpointEntity jsonApiEndpointEntity = JsonApiEndpointEntity.builder().id("2").build();

        com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto addOnDto =
                com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.addOnDtoBuilder()
                        .id("addOnId")
                        .description("Description")
                        .name("Name")
                        .jsonApi(
                                JsonApiDto.builder()
                                        .id("apiId")
                                        .build()
                        )
                        .visualization(
                                VisualizationDto.visualizationDtoBuilder()
                                        .additionalInputs(
                                                JsonApiEndpointDto.builder()
                                                        .id("apiEndpointId")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        when(addOnConfigurationGenericService.update(any(AddOnEntity.class))).thenReturn(Optional.of(configuration));
        when(jsonApiGenericService.update(any(JsonApiEntity.class))).thenReturn(Optional.of(jsonApiEntity));
        when(jsonApiEndpointGenericService.update(any(JsonApiEndpointEntity.class))).thenReturn(Optional.of(jsonApiEndpointEntity));

        Optional<AddOnDto> optionalSavedDto = addOnHandlerRegistry.update(addOnDto);
        assertThat(optionalSavedDto.isPresent(), equalTo(true));

        verify(jsonApiGenericService, times(1)).update(any(JsonApiEntity.class));
        verify(jsonApiEndpointGenericService, times(1)).update(any(JsonApiEndpointEntity.class));
        ArgumentCaptor<AddOnEntity> captor = ArgumentCaptor.forClass(AddOnEntity.class);
        verify(addOnConfigurationGenericService, times(1)).update(captor.capture());

        AddOnEntity captured = captor.getValue();
        assertThat(captured,
                hasProperty("visualization",
                        hasProperty("additionalInputsConfiguration", equalTo("apiEndpointId"))));

    }

}

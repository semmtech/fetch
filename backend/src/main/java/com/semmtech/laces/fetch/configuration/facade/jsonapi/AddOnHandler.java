package com.semmtech.laces.fetch.configuration.facade.jsonapi;

import com.semmtech.laces.fetch.configuration.dtos.common.NullSafeEntityDtoMapper;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiEndpointDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.VisualizationDto;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEntity;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.facade.ServiceRegistry;
import com.semmtech.laces.fetch.configuration.facade.common.BaseAddOnHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component("jsonAddOnHandler")
public class AddOnHandler extends BaseAddOnHandler {
    private final GenericService<JsonApiEntity> jsonApiGenericService;
    private final GenericService<JsonApiEndpointEntity> jsonApiEndpointGenericService;

    public AddOnHandler(
            ServiceRegistry<com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto> addOnServiceRegistry,
            GenericService<AddOnEntity> addOnConfigurationGenericService,
            GenericService<JsonApiEntity> jsonApiGenericService,
            GenericService<JsonApiEndpointEntity> jsonApiEndpointGenericService) {
        super(addOnConfigurationGenericService);

        this.jsonApiGenericService = jsonApiGenericService;
        this.jsonApiEndpointGenericService = jsonApiEndpointGenericService;

        Class<AddOnDto> handledClass = AddOnDto.class;
        addOnServiceRegistry.registerCreate(handledClass, this::create);
        addOnServiceRegistry.registerUpdate(handledClass, this::update);
        addOnServiceRegistry.registerEntityToDtoMapper(handledClass, this::entityToDto);
    }

    public com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto create(com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto commonDto) {
        AddOnDto addOnDto = (AddOnDto) commonDto;

        saveVisualization(addOnDto);

        saveJsonApi(addOnDto);
        saveImportTargets(addOnDto);
        AddOnEntity saved = addOnConfigurationGenericService.create(NullSafeEntityDtoMapper.toEntity(addOnDto));
        addOnDto.setId(saved.getId());
        return addOnDto;
    }

    public Optional<com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto> update(com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto commonDto) {
        AddOnDto addOnDto = (AddOnDto) commonDto;

        saveVisualization(addOnDto);

        saveJsonApi(addOnDto);
        saveImportTargets(addOnDto);
        return addOnConfigurationGenericService.update(NullSafeEntityDtoMapper.toEntity(addOnDto)).map(this::entityToDto);
    }

    private void saveImportTargets(AddOnDto addOnDto) {
        if (CollectionUtils.isNotEmpty(addOnDto.getImportSteps())) {
            addOnDto.getImportSteps()
                    .forEach(step -> saveAspectAndUpdateDtoWithId(
                            jsonApiEndpointGenericService,
                            step::getImportTarget,
                            getToEntityFunction(addOnDto)));
        }
    }

    private Function<JsonApiEndpointDto, JsonApiEndpointEntity> getToEntityFunction(AddOnDto addOnDto) {
        Function<JsonApiEndpointDto, JsonApiEndpointEntity> function = NullSafeEntityDtoMapper::toEntity;
        if (addOnDto.getJsonApi() != null && addOnDto.getJsonApi().getId() != null) {
            function = jsonApiEndpointDto -> jsonApiEndpointDto.toEntity(addOnDto.getJsonApi().getId());
        }
        return function;
    }

    private void saveJsonApi(AddOnDto addOnDto) {
        saveAspectAndUpdateDtoWithId(jsonApiGenericService, addOnDto::getJsonApi, JsonApiDto::toEntity);
    }

    private void saveVisualization(AddOnDto addOnDto) {
        if (addOnDto.getVisualization() != null) {
            saveAspectAndUpdateDtoWithId(
                    jsonApiEndpointGenericService,
                    () -> ((VisualizationDto) addOnDto.getVisualization()).getAdditionalInputs(),
                    getToEntityFunction(addOnDto));
        }
    }

    public com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto entityToDto(AddOnEntity addOnEntity) {
        return new com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto(
                addOnEntity,
                getJsonApiForConfiguration(addOnEntity),
                getAdditionalInfoEndpoint(addOnEntity),
                id -> getEntityById(
                        addOnEntity,
                        entity -> id,
                        entity -> id != null,
                        jsonApiEndpointGenericService,
                        JsonApiEndpointDto::new
                ));
    }

    private JsonApiDto getJsonApiForConfiguration(AddOnEntity configurationEntity) {
        return getEntityById(
                configurationEntity,
                entity -> entity.getDataTarget(),
                entity -> entity.getDataTarget() != null,
                jsonApiGenericService,
                JsonApiDto::new);
    }

    private JsonApiEndpointDto getAdditionalInfoEndpoint(AddOnEntity configurationEntity) {
        return getEntityById(
                configurationEntity,
                entity -> entity.getVisualization().getAdditionalInputsConfiguration(),
                entity -> entity.getVisualization() != null && StringUtils.isNotEmpty(entity.getVisualization().getAdditionalInputsConfiguration()),
                jsonApiEndpointGenericService,
                JsonApiEndpointDto::new
        );
    }

}

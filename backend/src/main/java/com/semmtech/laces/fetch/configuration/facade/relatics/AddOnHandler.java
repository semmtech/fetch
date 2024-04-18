package com.semmtech.laces.fetch.configuration.facade.relatics;

import com.semmtech.laces.fetch.configuration.dtos.common.NullSafeEntityDtoMapper;
import com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.TargetDataSystemDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.VisualizationDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.WorkspaceDto;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.facade.ServiceRegistry;
import com.semmtech.laces.fetch.configuration.facade.common.BaseAddOnHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("relaticsAddOnHandler")
public class AddOnHandler extends BaseAddOnHandler {
    private final GenericService<WorkspaceEntity> workspaceGenericService;
    private final GenericService<TargetDataSystemEntity> targetDataSystemGenericService;

    public AddOnHandler(
            ServiceRegistry<com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto> addOnServiceRegistry,
            GenericService<AddOnEntity> addOnConfigurationGenericService,
            GenericService<WorkspaceEntity> workspaceGenericService,
            GenericService<TargetDataSystemEntity> targetDataSystemGenericService) {

        super(addOnConfigurationGenericService);
        this.workspaceGenericService = workspaceGenericService;
        this.targetDataSystemGenericService = targetDataSystemGenericService;

        Class<AddOnDto> handledClass = AddOnDto.class;
        addOnServiceRegistry.registerCreate(handledClass, this::create);
        addOnServiceRegistry.registerUpdate(handledClass, this::update);
        addOnServiceRegistry.registerEntityToDtoMapper(handledClass, this::entityToDto);
    }

    public com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto create(com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto commonDto) {
        AddOnDto addOnDto = (AddOnDto) commonDto;
        saveVisualization(addOnDto);
        saveAspectAndUpdateDtoWithId(workspaceGenericService, addOnDto::getWorkspace, WorkspaceDto::toEntity);
        saveImportTargets(addOnDto);
        AddOnEntity saved = addOnConfigurationGenericService.create(addOnDto.toEntity());
        addOnDto.setId(saved.getId());
        return addOnDto;
    }

    public Optional<com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto> update(com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto commonDto) {
        AddOnDto addOnDto = (AddOnDto) commonDto;
        saveVisualization(addOnDto);
        saveWorkspace(addOnDto);
        saveImportTargets(addOnDto);
        return addOnConfigurationGenericService.update(NullSafeEntityDtoMapper.toEntity(addOnDto)).map(this::entityToDto);
    }

    private void saveImportTargets(AddOnDto addOnDto) {
        if (CollectionUtils.isNotEmpty(addOnDto.getImportSteps())) {
            addOnDto.getImportSteps()
                    .forEach(step -> saveAspectAndUpdateDtoWithId(
                            targetDataSystemGenericService,
                            step::getImportTarget,
                            NullSafeEntityDtoMapper::toEntity));
        }
    }

    private void saveWorkspace(AddOnDto addOnDto) {
        saveAspectAndUpdateDtoWithId(workspaceGenericService, addOnDto::getWorkspace, WorkspaceDto::toEntity);
    }

    private void saveVisualization(AddOnDto addOnDto) {
        if (addOnDto.getVisualization() != null) {
            saveAspectAndUpdateDtoWithId(
                    targetDataSystemGenericService,
                    () -> ((VisualizationDto) addOnDto.getVisualization()).getAdditionalInputs(),
                    TargetDataSystemDto::toEntity);
        }
    }

    public com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto entityToDto(AddOnEntity addOnEntity) {
        return new AddOnDto(
                addOnEntity,
                getJsonApiForConfiguration(addOnEntity),
                getAdditionalInfoEndpoint(addOnEntity),
                id -> getEntityById(
                        addOnEntity,
                        entity -> id,
                        entity -> id != null,
                        targetDataSystemGenericService,
                        TargetDataSystemDto::new
                ));
    }

    private WorkspaceDto getJsonApiForConfiguration(AddOnEntity configurationEntity) {
        return getEntityById(
                configurationEntity,
                entity -> entity.getDataTarget(),
                entity -> entity.getDataTarget() != null,
                workspaceGenericService,
                WorkspaceDto::new);
    }

    private TargetDataSystemDto getAdditionalInfoEndpoint(AddOnEntity configurationEntity) {
        return getEntityById(
                configurationEntity,
                entity -> entity.getVisualization().getAdditionalInputsConfiguration(),
                entity -> entity.getVisualization() != null && StringUtils.isNotEmpty(entity.getVisualization().getAdditionalInputsConfiguration()),
                targetDataSystemGenericService,
                TargetDataSystemDto::new
        );
    }

}

package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.dtos.common.ColumnDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlEndpointDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlQueryDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlQueryWithDefaultGraphsDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.TargetDataSystemDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.VisualizationDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.WorkspaceDto;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ColumnEntity;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.exceptions.WorkspaceIdAlreadyExistException;
import com.semmtech.laces.fetch.configuration.facade.ServiceRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Yogesh Tiwari (yogehtiwari@semmtech.nl) 14-04-2021
 * <p>This service is responsible for cloning a workspace including it's configurations. This
 * single service approach is preferred instead of scattered events to keep related code in one
 * place and to easily accommodate all future customizations
 */
@Service
public class WorkspaceCloneService {

    private final TargetSystemService targetSystemService;
    private final WorkspaceService workspaceService;
    private final AddOnConfigurationService addOnEntityService;
    private final ServiceRegistry<com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto>
            addOnDtoServices;

    public WorkspaceCloneService(
            TargetSystemService targetSystemService,
            WorkspaceService workspaceService,
            AddOnConfigurationService addOnConfigurationService,
            ServiceRegistry<com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto>
                    addOnDtoServices) {
        this.targetSystemService = targetSystemService;
        this.workspaceService = workspaceService;
        this.addOnEntityService = addOnConfigurationService;
        this.addOnDtoServices = addOnDtoServices;
    }

    /**
     * This function clone a workspace including it's configurations
     *
     * @param originalWorkspaceEntityId The id of workspace entity to clone
     * @param cloneWorkspaceId          The workspace-id of new workspace
     * @return New clone workspace DTO object
     */
    public ResponseEntity<WorkspaceDto> cloneWorkspaceAndConfigurations(
            String originalWorkspaceEntityId, String cloneWorkspaceId, String cloneWorkspaceName) {

        // Get the original workspace
        Optional<WorkspaceEntity> workspaceEntityOptional =
                workspaceService.get(originalWorkspaceEntityId);
        if (workspaceEntityOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        WorkspaceEntity originalWorkspaceEntity = workspaceEntityOptional.get();

        if (workspaceService.existsByWorkspaceId(cloneWorkspaceId)) {
            throw new WorkspaceIdAlreadyExistException(cloneWorkspaceId);
        }

        // Clone workspace entity
        WorkspaceDto clonedWorkspaceDto =
                new WorkspaceDto(
                        cloneWorkspaceEntity(
                                originalWorkspaceEntity, cloneWorkspaceId, cloneWorkspaceName));

        // TargetDataSystems are cloned separately because we need to prepare a map of
        // originalEntityId and with new clone entity which will be used in cloning
        // VisualizationEntity and ImportStepEntities
        Map<String, TargetDataSystemEntity> oldIdWithNewTDSEntity =
                cloneTargetDataSystemEntities(
                        originalWorkspaceEntityId, clonedWorkspaceDto.getId());

        // Clone all AddOnEntities
        cloneAddOnEntities(originalWorkspaceEntityId, clonedWorkspaceDto, oldIdWithNewTDSEntity);

        return ResponseEntity.ok(clonedWorkspaceDto);
    }

    private WorkspaceEntity cloneWorkspaceEntity(
            WorkspaceEntity workspace, String cloneWorkspaceId, String cloneWorkspaceName) {
        WorkspaceDto workspaceDto =
                new WorkspaceDto(
                        workspace.getEnvironmentId(), cloneWorkspaceId, cloneWorkspaceName);
        return workspaceService.create(workspaceDto.toEntity());
    }

    private Map<String, TargetDataSystemEntity> cloneTargetDataSystemEntities(
            String originalWorkspaceEntityId, String cloneWorkspaceEntityId) {

        Map<String, TargetDataSystemEntity> oldIdWithNewTDSEntity = new HashMap<>();

        List<TargetDataSystemEntity> originalTDSEntities =
                targetSystemService.getTargetSystemsByWorkspace(originalWorkspaceEntityId);

        for (TargetDataSystemEntity originalTDSEntity : originalTDSEntities) {

            TargetDataSystemDto targetDataSystemDto =
                    new TargetDataSystemDto(
                            originalTDSEntity.getOperationName(),
                            originalTDSEntity.getEntryCode(),
                            originalTDSEntity.getXPathExpression(),
                            cloneWorkspaceEntityId,
                            originalTDSEntity.getType());
            TargetDataSystemEntity newEntity =
                    targetSystemService.create(targetDataSystemDto.toEntity());

            oldIdWithNewTDSEntity.put(originalTDSEntity.getId(), newEntity);
        }

        return oldIdWithNewTDSEntity;
    }

    private void cloneAddOnEntities(
            String originalWorkspaceEntityId,
            WorkspaceDto cloneWorkspaceDto,
            Map<String, TargetDataSystemEntity> oldIdWithNewTDSEntity) {

        List<AddOnEntity> originalAddOnEntities =
                addOnEntityService.findConfigurationsByWorkspace(originalWorkspaceEntityId);

        List<String> allAddOnEntityNames = originalAddOnEntities.stream().map(AddOnEntity::getName).collect(Collectors.toList());

        for (AddOnEntity originalAddOnEntity : originalAddOnEntities) {

            AddOnDto dto =
                    AddOnDto.addOnDtoBuilder()
                            .active(originalAddOnEntity.isActive())
                            .simpleFeedback(originalAddOnEntity.isSimpleFeedback())
                            .name(
                                    getClonedName(
                                            allAddOnEntityNames,
                                            originalAddOnEntity.getName()))
                            .description(originalAddOnEntity.getDescription())
                            .startDate(Date.from(new GregorianCalendar().toInstant()))
                            .displayName(originalAddOnEntity.getDisplayName())
                            .sparqlEndpoint(
                                    new SparqlEndpointDto(originalAddOnEntity.getSparqlEndpoint()))
                            .workspace(cloneWorkspaceDto)
                            .visualization(
                                    cloneVisualizationEntity(
                                            originalAddOnEntity.getVisualization(),
                                            oldIdWithNewTDSEntity))
                            .build();

            dto.setImportSteps(
                    AddOnDto.toImportStepDtos(
                            originalAddOnEntity.getImportConfiguration(),
                            oldId ->
                                    getNewTargetDataSystemDtoFromMap(
                                            oldIdWithNewTDSEntity, oldId)));

            addOnDtoServices.create(dto);

            allAddOnEntityNames.add(dto.getName());
        }
    }

    private VisualizationDto cloneVisualizationEntity(
            VisualizationEntity visualizationEntity,
            Map<String, TargetDataSystemEntity> oldIdWithNewTargetDataSystemEntity) {

        TargetDataSystemDto additionalInputDto =
                getNewTargetDataSystemDtoFromMap(
                        oldIdWithNewTargetDataSystemEntity,
                        visualizationEntity.getAdditionalInputsConfiguration());

        LinkedHashMap<String, ColumnDto> columnDtoLinkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, ColumnEntity> entry : visualizationEntity.getColumns().entrySet()) {
            columnDtoLinkedHashMap.put(entry.getKey(), new ColumnDto(entry.getValue()));
        }

        return VisualizationDto.visualizationDtoBuilder()
                .additionalInputs(additionalInputDto)
                .childrenQuery(
                        new SparqlQueryWithDefaultGraphsDto(visualizationEntity.getChildrenQuery()))
                .rootsQuery(
                        new SparqlQueryWithDefaultGraphsDto(visualizationEntity.getRootsQuery()))
                .titleQuery(new SparqlQueryDto(visualizationEntity.getTitleQuery()))
                .enablePagination(visualizationEntity.isEnablePagination())
                .visibleColumns(columnDtoLinkedHashMap)
                .build();
    }

    private TargetDataSystemDto getNewTargetDataSystemDtoFromMap(
            Map<String, TargetDataSystemEntity> oldIdWithNewTargetDataSystemEntity,
            String oldEntityId) {
        return new TargetDataSystemDto(oldIdWithNewTargetDataSystemEntity.get(oldEntityId));
    }

    /**
     * Will create clone names as: name -> name_clone_1 name_clone_1 -> name_clone_2 name clone 1 ->
     * name clone 1_clone_1 Workspace
     *
     * @param allNames
     * @param name
     * @return
     */
    private String getClonedName(
            List<String> allNames, String name) {

        String newName = addIncrementToName(name);
        while (allNames.contains(newName)) {
            newName = addIncrementToName(newName);
        }
        return newName;
    }

    private String addIncrementToName(String name) {
        String cloneSegment = "_clone_";
        String[] nameParts = name.split(cloneSegment);
        if (nameParts.length > 0) {
            String lastPart = nameParts[nameParts.length - 1];
            if (StringUtils.isNumeric(lastPart)) {
                int lastPartIncremented = Integer.parseInt(lastPart) + 1;
                String[] newNameParts = Arrays.copyOf(nameParts, nameParts.length - 1);
                return StringUtils.join(newNameParts, "_") + cloneSegment + lastPartIncremented;
            }
        }
        return name + cloneSegment + "1";
    }
}

package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.relatics.TargetDataSystemDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.WorkspaceDto;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.service.TargetSystemService;
import com.semmtech.laces.fetch.configuration.service.WorkspaceCloneService;
import com.semmtech.laces.fetch.configuration.service.WorkspaceService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    private final GenericDataTargetController<WorkspaceDto, TargetDataSystemDto, WorkspaceEntity, TargetDataSystemEntity> genericDataTargetController;
    private final TargetSystemService targetSystemService;
    private final WorkspaceService workspaceService;
    private final WorkspaceCloneService workspaceCloneService;
    private final OptionalToResponseEntityMapper responseEntityMapper;

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDto> getById(@PathVariable("workspaceId") String workspaceId) {
        return responseEntityMapper.buildEntity(
                workspaceService.get(workspaceId).map(WorkspaceDto::new));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceDto>> getAll() {
        return genericDataTargetController.findAll(workspaceService, WorkspaceDto::new);
    }

    @PostMapping
    public ResponseEntity<WorkspaceDto> create(@NotNull @RequestBody WorkspaceDto newObject) {

        return genericDataTargetController
                .create(
                        newObject,
                        workspaceService,
                        targetSystemService,
                        WorkspaceDto::getTargetDataSystems,
                        this::toLinkedTargetEntity,
                        WorkspaceDto::toEntity,
                        WorkspaceDto::new
                );
    }

    @PostMapping("/{workspaceId}/clone")
    public ResponseEntity<WorkspaceDto> clone(@PathVariable("workspaceId") String workspaceId, @NotEmpty @RequestParam String cloneWorkspaceId, @NotEmpty @RequestParam String cloneWorkspaceName) {
        return workspaceCloneService
                .cloneWorkspaceAndConfigurations(
                        workspaceId,
                        cloneWorkspaceId,
                        cloneWorkspaceName
                );
    }

    @PutMapping
    public ResponseEntity<WorkspaceDto> update(@NotNull @RequestBody WorkspaceDto updatedObject) {
        return genericDataTargetController.update(
                updatedObject,
                workspaceService,
                targetSystemService,
                targetSystemService::getTargetSystemsByWorkspace,
                WorkspaceDto::getTargetDataSystems,
                TargetDataSystemDto::haveConflictingProperties,
                dto -> toLinkedTargetEntity(dto, updatedObject.getId()),
                WorkspaceDto::toEntity,
                WorkspaceDto::new,
                TargetDataSystemDto::new,
                this::updateTargets
        );
    }

    @DeleteMapping
    public ResponseEntity<List<String>> delete(@NotNull @RequestBody List<WorkspaceDto> objectsToDelete) {
        return genericDataTargetController.delete(
                objectsToDelete,
                workspaceService,
                targetSystemService,
                WorkspaceDto::toEntity,
                targetSystemService::getTargetSystemsByWorkspaces
        );
    }

    /**
     * Convert the DTO into an entity linked to the right JsonAPI
     *
     * @param targetDto   the DTO to convert
     * @param workspaceId the id of the Workspace to link to
     * @return an entity to perform the update with.
     */
    private TargetDataSystemEntity toLinkedTargetEntity(TargetDataSystemDto targetDto, String workspaceId) {
        TargetDataSystemEntity updatedEntity = targetDto.toEntity();
        updatedEntity.setWorkspaceId(workspaceId);
        return updatedEntity;
    }

    private WorkspaceDto updateTargets(WorkspaceDto dto, List<TargetDataSystemDto> finalEndpointsAfterUpdate) {
        dto.setTargetDataSystems(finalEndpointsAfterUpdate);
        return dto;
    }

    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        workspaceService.clearCache();
        targetSystemService.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}

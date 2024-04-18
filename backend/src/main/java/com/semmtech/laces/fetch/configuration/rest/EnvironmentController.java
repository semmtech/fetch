package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.relatics.EnvironmentDto;
import com.semmtech.laces.fetch.configuration.exceptions.UnsupportedDeleteException;
import com.semmtech.laces.fetch.configuration.entities.EnvironmentEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.service.WorkspaceService;
import com.semmtech.laces.fetch.streams.StreamUtils;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/environments")
public class EnvironmentController extends GenericController<EnvironmentEntity, EnvironmentDto> {
    private final WorkspaceService workspaceService;

    public EnvironmentController(GenericService<EnvironmentEntity> service, WorkspaceService workspaceService, OptionalToResponseEntityMapper responseMapper) {
        super(service, responseMapper, EnvironmentDto::new);
        this.workspaceService = workspaceService;
    }

    @Override
    @DeleteMapping
    public ResponseEntity<List<String>> delete(@NotNull @RequestBody List<EnvironmentDto> objectsToDelete) {
        List<String> environmentIds =
                objectsToDelete.stream()
                        .map(EnvironmentDto::getId)
                        .collect(Collectors.toList());

        List<WorkspaceEntity> workspacesByEnvironment =
                workspaceService.getWorkspacesForEnvironments(environmentIds);

        if (CollectionUtils.isNotEmpty(workspacesByEnvironment)) {
            throw new UnsupportedDeleteException("The environment is still linked to workspaces. Please, delete these workspaces first.");
        }

        return ResponseEntity.ok(
                service.delete(
                        StreamUtils.transformList(objectsToDelete, EnvironmentDto::toEntity)));
    }

    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        workspaceService.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}

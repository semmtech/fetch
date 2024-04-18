package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.exceptions.NoEnvironmentConfiguredException;
import com.semmtech.laces.fetch.configuration.exceptions.NoRelaticsWebserviceConfiguredException;
import com.semmtech.laces.fetch.configuration.exceptions.WorkspaceNotFoundException;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.EnvironmentEntity;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelaticsService {
    private final WorkspaceService workspaceService;
    private final RelaticsEnvironmentService environmentService;
    private final TargetSystemService targetSystemService;

    public RelaticsService(WorkspaceService workspaceService, RelaticsEnvironmentService environmentService, TargetSystemService targetSystemService) {
        this.workspaceService = workspaceService;
        this.environmentService = environmentService;
        this.targetSystemService = targetSystemService;
    }

    public String getServiceUrl(AddOnEntity configuration) {
        return getWorkspaceForConfiguration(configuration)
                .map(this::getEnvironmentForWorkspace)
                .map(EnvironmentEntity::getServiceUrl)
                .orElseThrow(WorkspaceNotFoundException::new);
    }

    public WorkspaceEntity getRequiredWorkspaceForConfiguration(AddOnEntity addOnEntity) {
        return getWorkspaceForConfiguration(addOnEntity).orElseThrow(WorkspaceNotFoundException::new);
    }

    public Optional<WorkspaceEntity> getWorkspaceForConfiguration(AddOnEntity configuration) {
        return workspaceService.get(configuration.getDataTarget());
    }

    public EnvironmentEntity getEnvironmentForWorkspace(WorkspaceEntity workspaceEntity) {
        return environmentService.get(workspaceEntity.getEnvironmentId())
                .orElseThrow(NoEnvironmentConfiguredException::new);
    }

    public TargetDataSystemEntity getTargetDataSystem(String id) {
        return targetSystemService.get(id)
                .orElseThrow(NoRelaticsWebserviceConfiguredException::new);
    }
}

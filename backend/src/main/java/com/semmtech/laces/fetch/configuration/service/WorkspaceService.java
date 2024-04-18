package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.repository.WorkspaceConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkspaceService extends GenericService<WorkspaceEntity> {
    private final AddOnConfigurationService configurationService;

    public WorkspaceService(WorkspaceConfigurationRepository repository,
                            AddOnConfigurationService configurationService) {

        super(repository);
        this.configurationService = configurationService;
    }

    public List<WorkspaceEntity> getWorkspacesForEnvironments(List<String> environmentIds) {
        return ((WorkspaceConfigurationRepository) repository).findByEnvironmentIdIn(environmentIds);
    }

    public boolean existsByWorkspaceId(String workspaceId) {
        return ((WorkspaceConfigurationRepository) repository).existsByWorkspaceId(workspaceId);
    }

    public Optional<WorkspaceEntity> update(WorkspaceEntity workspaceEntity) {
        final var workspaceId = workspaceEntity.getId();

        Optional<WorkspaceEntity> savedEntity = super.update(workspaceEntity);

        refreshConfigurationCaches(workspaceId);

        return savedEntity;
    }

    private void refreshConfigurationCaches(String workspaceId) {
        log.debug("Refreshing Configurations which contain Workspace {}...", workspaceId);
        configurationService
                .findConfigurationsByWorkspace(workspaceId)
                .forEach(configuration -> configurationService.refresh(configuration.getId()));
    }

    public List<String> delete(Collection<? extends WorkspaceEntity> objectsToDelete) {
        List<String> objectIds = objectsToDelete.stream().map(WorkspaceEntity::getId).collect(Collectors.toList());

        List<String> results = super.delete(objectsToDelete);
        objectIds.forEach(this::refreshConfigurationCaches);

        return results;
    }
}

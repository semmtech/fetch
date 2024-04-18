package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.repository.TargetDatasystemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TargetSystemService extends GenericService<TargetDataSystemEntity> {
    protected TargetDatasystemRepository repository;
    private final AddOnConfigurationService configurationService;

    public TargetSystemService(TargetDatasystemRepository repository,
                               AddOnConfigurationService configurationService) {
        super(repository);
        this.repository = repository;
        this.configurationService = configurationService;
    }

    public List<TargetDataSystemEntity> getTargetSystemsByWorkspace(String workspaceId) {
        return repository.findByWorkspaceId(workspaceId);
    }

    public List<TargetDataSystemEntity> getTargetSystemsByWorkspaces(List<String> workspaceIds) {
        return repository.findByWorkspaceIdIn(workspaceIds);
    }

    public Optional<TargetDataSystemEntity> update(TargetDataSystemEntity targetEntity) {
        final var targetId = targetEntity.getId();

        Optional<TargetDataSystemEntity> savedEntity = super.update(targetEntity);
        refreshConfigurationCaches(targetId);

        return savedEntity;
    }

    private void refreshConfigurationCaches(String targetId) {
        log.debug("Refreshing Configurations which contain TargetDataSystem {}...", targetId);
        configurationService
                .findConfigurationsByTarget(targetId)
                .forEach(configuration -> configurationService.refresh(configuration.getId()));
    }

    public List<String> delete(Collection<? extends TargetDataSystemEntity> objectsToDelete) {
        List<String> objectIds = objectsToDelete.stream().map(TargetDataSystemEntity::getId).collect(Collectors.toList());

        List<String> results = super.delete(objectsToDelete);
        objectIds.forEach(this::refreshConfigurationCaches);

        return results;
    }
}

package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceConfigurationRepository extends FindByIdInRepository<WorkspaceEntity, String> {
    List<WorkspaceEntity> findByEnvironmentIdIn(List<String> environmentId);

    boolean existsByWorkspaceId(String workspaceId);
}

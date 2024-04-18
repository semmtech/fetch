package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetDatasystemRepository extends FindByIdInRepository<TargetDataSystemEntity, String> {
    List<TargetDataSystemEntity> findByWorkspaceId(String workspaceId);
    List<TargetDataSystemEntity> findByWorkspaceIdIn(List<String> workspaceIds);
}
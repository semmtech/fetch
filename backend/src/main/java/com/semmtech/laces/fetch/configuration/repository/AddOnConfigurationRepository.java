package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddOnConfigurationRepository extends FindByIdInRepository<AddOnEntity, String> {
    List<AddOnEntity> findByVisualizationRootsQueryQueryId(String queryId);
    List<AddOnEntity> findByDataTarget(String workspaceId);
}

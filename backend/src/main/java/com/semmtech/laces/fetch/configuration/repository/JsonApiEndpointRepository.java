package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JsonApiEndpointRepository extends FindByIdInRepository<JsonApiEndpointEntity, String> {
    List<JsonApiEndpointEntity> findByApiId(String apiId);
    List<JsonApiEndpointEntity> findByApiIdIn(List<String> apiIds);
}

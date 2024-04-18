package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import com.semmtech.laces.fetch.configuration.repository.JsonApiEndpointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JsonApiEndpointService extends GenericService<JsonApiEndpointEntity> {
    protected final JsonApiEndpointRepository repository;

    public JsonApiEndpointService(JsonApiEndpointRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<JsonApiEndpointEntity> getEndpointsByApiId(String apiId) {
        return repository.findByApiId(apiId);
    }

    public List<JsonApiEndpointEntity> getEndpointsByApiIdIn(List<String> apiIds) {
        return repository.findByApiIdIn(apiIds);
    }
}

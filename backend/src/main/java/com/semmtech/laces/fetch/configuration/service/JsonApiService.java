package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.JsonApiEntity;
import com.semmtech.laces.fetch.configuration.repository.FindByIdInRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JsonApiService extends GenericService<JsonApiEntity> {
    public JsonApiService(FindByIdInRepository<JsonApiEntity, String> repository) {
        super(repository);
    }

    @Override
    @Cacheable("jsonapis")
    public Optional<JsonApiEntity> get(String id) {
        return super.get(id);
    }
}

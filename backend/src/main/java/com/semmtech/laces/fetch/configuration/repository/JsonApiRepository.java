package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.JsonApiEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface JsonApiRepository extends FindByIdInRepository<JsonApiEntity, String> {
}

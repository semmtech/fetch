package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.EnvironmentEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentRepository extends FindByIdInRepository<EnvironmentEntity, String> {
}

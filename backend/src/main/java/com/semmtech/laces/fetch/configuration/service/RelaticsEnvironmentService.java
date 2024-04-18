package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.EnvironmentEntity;
import com.semmtech.laces.fetch.configuration.repository.FindByIdInRepository;
import org.springframework.stereotype.Service;

@Service
public class RelaticsEnvironmentService extends GenericService<EnvironmentEntity> {
    public RelaticsEnvironmentService(FindByIdInRepository<EnvironmentEntity, String> repository) {
        super(repository);
    }
}

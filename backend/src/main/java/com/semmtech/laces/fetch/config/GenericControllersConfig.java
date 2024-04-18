package com.semmtech.laces.fetch.config;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiEndpointDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.TargetDataSystemDto;
import com.semmtech.laces.fetch.configuration.dtos.relatics.WorkspaceDto;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEntity;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import com.semmtech.laces.fetch.configuration.rest.GenericDataTargetController;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericControllersConfig {
    @Bean
    public GenericDataTargetController<JsonApiDto, JsonApiEndpointDto, JsonApiEntity, JsonApiEndpointEntity> jsonApiGenericController(OptionalToResponseEntityMapper responseMapper) {
        return new GenericDataTargetController<>(
                GenericService::createMany,
                GenericService::updateMany,
                responseMapper
        );
    }

    @Bean
    public GenericDataTargetController<WorkspaceDto, TargetDataSystemDto, WorkspaceEntity, TargetDataSystemEntity> workspaceGenericController(OptionalToResponseEntityMapper responseMapper) {
        return new GenericDataTargetController<>(
                GenericService::createMany,
                GenericService::updateMany,
                responseMapper
        );
    }
}

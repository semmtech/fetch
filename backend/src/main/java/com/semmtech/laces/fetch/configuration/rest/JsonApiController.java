package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiDto;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiEndpointDto;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEntity;
import com.semmtech.laces.fetch.configuration.service.JsonApiEndpointService;
import com.semmtech.laces.fetch.configuration.service.JsonApiService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/jsonapis")
@RequiredArgsConstructor
public class JsonApiController {

    private final JsonApiService jsonApiService;
    private final JsonApiEndpointService jsonApiEndpointService;
    private final OptionalToResponseEntityMapper responseMapper;
    private final GenericDataTargetController<JsonApiDto, JsonApiEndpointDto, JsonApiEntity, JsonApiEndpointEntity> genericDataTargetController;

    @GetMapping("/")
    public ResponseEntity<List<JsonApiDto>> findAll() {
        return genericDataTargetController.findAll(jsonApiService, JsonApiDto::new);
    }

    @PostMapping("/")
    public ResponseEntity<JsonApiDto> createNew(@RequestBody JsonApiDto dto) {

        return genericDataTargetController
                .create(
                        dto,
                        jsonApiService,
                        jsonApiEndpointService,
                        JsonApiDto::getEndpoints,
                        this::toLinkedEndpointEntity,
                        JsonApiDto::toEntity,
                        JsonApiDto::new
                );
    }

    @PutMapping("/")
    public ResponseEntity<JsonApiDto> update(@RequestBody JsonApiDto apiDto) {

        return genericDataTargetController
                .update(
                        apiDto,
                        jsonApiService,
                        jsonApiEndpointService,
                        jsonApiEndpointService::getEndpointsByApiId,
                        JsonApiDto::getEndpoints,
                        this::haveConflictingProperties,
                        dto -> toLinkedEndpointEntity(dto, apiDto.getId()),
                        JsonApiDto::toEntity,
                        JsonApiDto::new,
                        JsonApiEndpointDto::new,
                        this::updateEndpoints
                );
    }

    @DeleteMapping("/")
    public ResponseEntity<List<String>> delete(@NotNull @RequestBody List<JsonApiDto> apisToDelete) {
        return genericDataTargetController.delete(
                apisToDelete,
                jsonApiService,
                jsonApiEndpointService,
                JsonApiDto::toEntity,
                jsonApiEndpointService::getEndpointsByApiIdIn
        );
    }

    private JsonApiDto updateEndpoints(JsonApiDto dto, List<JsonApiEndpointDto> finalEndpointsAfterUpdate) {
        dto.setEndpoints(finalEndpointsAfterUpdate);
        return dto;
    }

    /**
     * Convert the DTO into an entity linked to the right JsonAPI
     *
     * @param endpointDto the DTO to convert
     * @param apiId       the id of the JsonApi to link to
     * @return an entity to perform the update with.
     */
    private JsonApiEndpointEntity toLinkedEndpointEntity(JsonApiEndpointDto endpointDto, String apiId) {
        JsonApiEndpointEntity updatedEntity = endpointDto.toEntity();
        updatedEntity.setApiId(apiId);
        return updatedEntity;
    }

    private boolean haveConflictingProperties(JsonApiEndpointDto endpointDto, JsonApiEndpointEntity endpointEntity) {
        return !(StringUtils.equals(endpointDto.getName(), endpointEntity.getName())
                && StringUtils.equals(endpointDto.getPath(), endpointEntity.getPath())
                && StringUtils.equals(endpointDto.getType(), endpointEntity.getType()));
    }


    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        jsonApiService.clearCache();
        jsonApiEndpointService.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}

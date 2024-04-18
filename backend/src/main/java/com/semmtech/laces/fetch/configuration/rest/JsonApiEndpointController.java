package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.jsonapi.JsonApiEndpointDto;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import com.semmtech.laces.fetch.configuration.service.JsonApiEndpointService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/jsonapiendpoints")
@RequiredArgsConstructor
public class JsonApiEndpointController {

    private final JsonApiEndpointService jsonApiEndpointService;
    private final OptionalToResponseEntityMapper responseMapper;

    @GetMapping("/")
    public ResponseEntity<List<JsonApiEndpointDto>> findAll() {
        return ResponseEntity.ok(
                entitiesAsDtos(
                        jsonApiEndpointService.getAll()
                )
        );
    }

    @GetMapping(path = "/", params = "jsonApiId")
    public ResponseEntity<List<JsonApiEndpointDto>> endpointsByApi(@RequestParam("jsonApiId") String apiId) {
        return ResponseEntity.ok(
                entitiesAsDtos(
                        jsonApiEndpointService.getEndpointsByApiId(apiId)
                )
        );
    }

    private List<JsonApiEndpointDto> entitiesAsDtos(List<JsonApiEndpointEntity> entities) {
        return entities.stream()
                .map(JsonApiEndpointDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        jsonApiEndpointService.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}

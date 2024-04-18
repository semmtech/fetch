package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.common.NullSafeEntityDtoMapper;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlEndpointDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlEndpointTestRequest;
import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.service.SparqlEndpointService;
import com.semmtech.laces.fetch.configuration.service.SparqlQueryService;
import com.semmtech.laces.fetch.sparql.SparqlClient;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/sparqlendpoints")
public class SparqlEndpointController extends GenericController<SparqlEndpointEntity, SparqlEndpointDto> {
    private final SparqlClient sparqlClient;
    private final SparqlQueryService sparqlQueryService;
    private final SparqlEndpointService sparqlEndpointService;

    public SparqlEndpointController(GenericService<SparqlEndpointEntity> service, OptionalToResponseEntityMapper responseMapper, SparqlClient sparqlClient, SparqlQueryService sparqlQueryService, SparqlEndpointService sparqlEndpointService) {
        super(service, responseMapper, SparqlEndpointDto::new);
        this.sparqlClient = sparqlClient;
        this.sparqlQueryService = sparqlQueryService;
        this.sparqlEndpointService = sparqlEndpointService;
    }

    @PostMapping(value = "test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String testSparqlQuery(@RequestBody SparqlEndpointTestRequest testRequest) {
        return sparqlQueryService.get(testRequest.getSelectedQuery())
                .map(query ->
                        sparqlClient.executeQueryRaw(
                                query.getQuery(),
                                testRequest.getDefaultGraphs(),
                                NullSafeEntityDtoMapper.toEntity(testRequest.getSparqlEndpoint())))
                .map(optional -> optional.orElse("Query failed"))
                .orElse("No query with id " + testRequest.getSelectedQuery() + " exists.");
    }

    @PostMapping(value = "test-fixed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String testSparqlQueryFixed(@RequestBody SparqlEndpointEntity sparqlEndpoint) {
        return sparqlClient.executeQueryRaw("SELECT ?s ?p ?o { ?s ?p ?o . }", Collections.emptyList(), sparqlEndpoint)
                .orElse("Failed to execute query SELECT ?s ?p ?o { ?s ?p ?o . }");
    }

    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        sparqlEndpointService.clearCache();
        sparqlQueryService.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}

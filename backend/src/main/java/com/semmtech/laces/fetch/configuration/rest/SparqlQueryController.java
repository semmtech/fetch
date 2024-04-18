package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.common.ColumnDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlQueryDto;
import com.semmtech.laces.fetch.configuration.dtos.common.UpdateSelectedQueryRequest;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.service.SparqlEndpointService;
import com.semmtech.laces.fetch.configuration.service.SparqlQueryService;
import com.semmtech.laces.fetch.sparql.SparqlClient;
import com.semmtech.laces.fetch.streams.StreamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/sparqlqueries")
@Slf4j
public class SparqlQueryController extends GenericController<SparqlQueryEntity, SparqlQueryDto> {
    private final SparqlClient sparqlClient;
    private final SparqlEndpointService sparqlEndpointService;
    private final SparqlQueryService sparqlQueryService;

    public SparqlQueryController(GenericService<SparqlQueryEntity> service, OptionalToResponseEntityMapper responseMapper, SparqlClient sparqlClient, SparqlEndpointService sparqlEndpointService, SparqlQueryService sparqlQueryService) {
        super(service, responseMapper, SparqlQueryDto::new);
        this.sparqlClient = sparqlClient;
        this.sparqlEndpointService = sparqlEndpointService;
        this.sparqlQueryService = sparqlQueryService;
    }

    @PostMapping(value = "test", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String testSparqlQuery(@RequestHeader(name = "X-LACES-ENDPOINTID", required = true) String endpointId, @RequestHeader(name = "X-LACES-DEFAULTGRAPHS", required = false) List<String> defaultGraphs, @RequestParam("query") String query) {
        return sparqlEndpointService.get(endpointId)
                .map(endpoint -> sparqlClient.executeQueryRaw(query, defaultGraphs, endpoint))
                .map(optional -> optional.orElse("Query failed"))
                .orElse("No endpoint with id " + endpointId + " exists.");
    }

    @GetMapping(value = "{id}/variables")
    public ResponseEntity<List<String>> getDeclaredVariablesByQueryId(@PathVariable(name = "id") String id) {
        return responseMapper.buildEntity(
                service.get(id)
                        .map(SparqlQueryEntity::getQuery)
                        .map(this::extractVariables));
    }

    @PostMapping(value = "variables")
    public ResponseEntity<List<String>> getDeclaredVariablesFromBody(@RequestBody String queryString) {
        return ResponseEntity.ok(extractVariables(queryString));
    }

    private List<String> extractVariables(String queryString) {
        return ((SparqlQueryService) service).extractVariables(queryString, throwable -> log.warn(throwable.getMessage(), throwable));
    }

    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully updated the query and configurations using it."),
                    @ApiResponse(code = 409, message = "The query was saved, but due to syntax errors, the configurations weren't updated."),
                    @ApiResponse(code = 500, message = "Something went wrong during saving of the query. The query was not updated.")
            })
    @Override
    public ResponseEntity<SparqlQueryDto> update(@RequestBody @NotNull @Valid SparqlQueryDto updatedObject) {
        return responseMapper.buildEntity(service.update(updatedObject.toEntity()).map(SparqlQueryDto::new));

    }

    @GetMapping(value = "bytype/{type}")
    public ResponseEntity<Collection<SparqlQueryDto>> getByType(@PathVariable(name = "type") String type) {
        return ResponseEntity.ok(
                StreamUtils.transformList(
                    ((SparqlQueryService)service).getQueriesOfType(type),
                    SparqlQueryDto::new
                )
        );
    }

    @ApiOperation(value = "Update calculate the new set of columns after updating the selected query of a configuration, " +
                    "based on the previously selected query, the new query and the set of defined columns.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The list of new columns has successfully been calculated."),
            @ApiResponse(code = 409, message = "There were errors in the new query, don't update the new columns and show a warning message.")
    })
    @PutMapping(value = "selection")
    public ResponseEntity<Map<String, ColumnDto>> updateSelection(@RequestBody UpdateSelectedQueryRequest updateSelectedQueryRequest) {
        return ResponseEntity.ok(((SparqlQueryService)service).calculateUpdatedColumnsForNewQuery(
                updateSelectedQueryRequest.getPreviousSelectedRootsQueryId(),
                updateSelectedQueryRequest.getSelectedRootsQueryId(),
                updateSelectedQueryRequest.getConfiguredColumns()
        ));
    }

    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        sparqlEndpointService.clearCache();
        sparqlQueryService.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}

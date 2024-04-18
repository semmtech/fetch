package com.semmtech.laces.fetch.visualization.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.configuration.service.SparqlQueryService;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import com.semmtech.laces.fetch.visualization.model.RootsQueryResponse;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import com.semmtech.laces.fetch.visualization.service.VisualizationService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/visualization")
public class VisualizationController {

    private VisualizationService visualizationService;
    private AddOnConfigurationService addOnConfigurationService;
    private SparqlQueryService sparqlQueryService;
    private OptionalToResponseEntityMapper responseMapper;

    public VisualizationController(
            VisualizationService visualizationService,
            AddOnConfigurationService addOnConfigurationService,
            SparqlQueryService sparqlQueryService,
            OptionalToResponseEntityMapper responseMapper) {
        this.visualizationService = visualizationService;
        this.addOnConfigurationService = addOnConfigurationService;
        this.sparqlQueryService = sparqlQueryService;
        this.responseMapper = responseMapper;
    }

    @PostMapping("/roots")
    public ResponseEntity<RootsQueryResponse> getRootVisualization(
            @NotNull @RequestParam("configurationId") String configurationId, @NotNull @RequestBody QueryExecutionRequest request) {

        return responseMapper.buildEntity(
                addOnConfigurationService
                        .get(configurationId)
                        .filter(AddOnEntity::isActive)
                        .map(addOnConfiguration -> visualizationService.executeRootQuery(addOnConfiguration, request))
        );
    }

    @PostMapping("/children")
    public ResponseEntity<QueryResult> getChildrenVisualization(
            @NotNull @RequestParam("configurationId") String configurationId,
            @NotNull @RequestBody QueryExecutionRequest request) {

        return responseMapper.buildEntity(
                addOnConfigurationService
                        .get(configurationId)
                        .filter(AddOnEntity::isActive)
                        .map(configuration -> visualizationService.executeChildQuery(configuration, request))
        );
    }

    @GetMapping(value = "/filtervalues")
    public List<Map<String, String>> executeFilterQuery(@NotNull @RequestParam("configurationId") String configurationId, @NotNull @RequestParam("queryId") String queryId) {
        return sparqlQueryService.get(queryId)
            .map(query -> runQueryForConfiguration(query, configurationId))
                .orElse(List.of());
    }

    private List<Map<String, String>> runQueryForConfiguration(SparqlQueryEntity query, String configurationId) {
        return addOnConfigurationService
                .get(configurationId)
                .filter(AddOnEntity::isActive)
                .map(configuration -> visualizationService.executeParameterLessQuery(configuration, query))
                .map(QueryResult::getValues)
                .orElse(List.of());
    }
}

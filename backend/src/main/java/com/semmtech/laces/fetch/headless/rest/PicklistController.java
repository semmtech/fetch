package com.semmtech.laces.fetch.headless.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import com.semmtech.laces.fetch.visualization.service.VisualizationService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/picklists")
public class PicklistController {
    public static final String ASPECT_URI = "aspectUri";

    private VisualizationService visualizationService;
    private AddOnConfigurationService addOnConfigurationService;
    private OptionalToResponseEntityMapper responseMapper;

    public PicklistController(VisualizationService visualizationService, AddOnConfigurationService addOnConfigurationService, OptionalToResponseEntityMapper responseMapper) {
        this.visualizationService = visualizationService;
        this.addOnConfigurationService = addOnConfigurationService;
        this.responseMapper = responseMapper;
    }

    @GetMapping()
    public ResponseEntity<List<Map<String, String>>> getPicklist(@RequestParam("configurationId") String configurationId, @RequestParam(value = ASPECT_URI, required = false) String aspectUri) {
        final var requestBuilder = QueryExecutionRequest.builder();
        if (aspectUri != null) {
            requestBuilder
                    .commonParameter(
                            CommonParameter.builder()
                                    .value(aspectUri)
                                    .type("uri")
                                    .id(ASPECT_URI)
                                    .build());
        }
        QueryExecutionRequest request = requestBuilder.build();

        return responseMapper.buildEntity(
                addOnConfigurationService
                        .get(configurationId)
                        .filter(AddOnEntity::isActive)
                        .map(addOnConfiguration -> visualizationService.executeRootQuery(addOnConfiguration, request))
                        .map(QueryResult::getValues)
        );
    }
}

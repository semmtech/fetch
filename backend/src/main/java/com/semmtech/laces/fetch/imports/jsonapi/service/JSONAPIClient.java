package com.semmtech.laces.fetch.imports.jsonapi.service;

import com.semmtech.laces.fetch.restclient.OutboundRESTRequestLogger;
import com.semmtech.laces.fetch.configuration.exceptions.JsonApiEndpointNotFoundException;
import com.semmtech.laces.fetch.configuration.exceptions.JsonApiNotFoundException;
import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.service.JsonApiService;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.imports.generic.service.ImportService;
import com.semmtech.laces.fetch.imports.generic.service.TargetClient;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class JSONAPIClient extends TargetClient {

    private final RestTemplate restTemplate;
    private final JsonApiService jsonApiService;
    private final GenericService<JsonApiEndpointEntity> jsonApiEndpointService;

    public JSONAPIClient(ImportService importService, RestTemplateBuilder restTemplateBuilder,
                         OutboundRESTRequestLogger logger, JsonApiService jsonApiService,
                         GenericService<JsonApiEndpointEntity> jsonApiEndpointService) {
        super(importService, EnumSet.of(EnvironmentType.JSON_API));
        this.jsonApiEndpointService = jsonApiEndpointService;
        this.restTemplate = restTemplateBuilder.interceptors(logger).build();
        this.jsonApiService = jsonApiService;
    }

    @Override
    public GenericImportResponse doImport(ImportStepEntity step, AddOnEntity addOnEntity, QueryResult combinedResult, Stream<CommonParameter> headerParameters) {
        String url = getServiceUrl(addOnEntity) + "/" + getPath(step);
        HttpHeaders headers = new HttpHeaders();
        headerParameters.forEach(headerParameter -> headers.set(headerParameter.getId(), headerParameter.getValue()));

        HttpEntity<List<Map<String,String>>> entity = new HttpEntity<>(combinedResult.getValues(), headers);
        try {
            ResponseEntity<BIMPortalImportResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, BIMPortalImportResponse.class);
            return buildSuccessResponse(step, responseEntity);
        } catch (HttpClientErrorException e) {
            return buildErrorResponse(step, e);
        }
    }

    private String getPath(ImportStepEntity step) {
        var importTarget = step.getImportTarget();
        if (importTarget != null) {
               return jsonApiEndpointService.get(importTarget)
               .map(JsonApiEndpointEntity::getPath)
               .orElseThrow(JsonApiEndpointNotFoundException::new);
        }
        throw new JsonApiEndpointNotFoundException();
    }

    private String getServiceUrl(AddOnEntity configuration) {
        return jsonApiService.get(configuration.getDataTarget())
                .map(JsonApiEntity::getServiceUrl)
                .orElseThrow(JsonApiNotFoundException::new);
    }

    private GenericImportResponse buildErrorResponse(ImportStepEntity step, HttpClientErrorException exception) {
        return GenericImportResponse.builder()
                .importStep(step.getName())
                .errors(List.of("Status: " + exception.getRawStatusCode() + "-" +exception.getStatusText() + ": " + exception.getResponseBodyAsString()))
                .success(false)
                .warnings(Collections.emptyList())
                .build();
    }

    private GenericImportResponse buildSuccessResponse(ImportStepEntity step, ResponseEntity<BIMPortalImportResponse> responseEntity) {
        var numberOfRecords = CollectionUtils.isNotEmpty(responseEntity.getBody().getResult()) ? responseEntity.getBody().getResult().size() : 0;

        return GenericImportResponse.builder()
                .importStep(step.getName())
                .success(true)
                .successMessage("Successfully created " + numberOfRecords + " instances.")
                .warnings(StringUtils.isNotEmpty(responseEntity.getBody().getMessage()) ? List.of(responseEntity.getBody().getMessage()) : Collections.emptyList())
                .errors(Collections.emptyList())
                .build();
    }

    @Override
    public List<Map<String, String>> getAdditionalInputData(AddOnEntity addOnEntity) {
        return Collections.EMPTY_LIST;
    }

}

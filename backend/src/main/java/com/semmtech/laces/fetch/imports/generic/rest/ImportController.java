package com.semmtech.laces.fetch.imports.generic.rest;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ImportEntity;
import com.semmtech.laces.fetch.configuration.service.AddOnConfigurationService;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.imports.generic.service.ImportService;
import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Api("laces-fetch-api")
@Controller
public class ImportController {

    public static final String IMPORT_SUCCESSFUL_MESSAGE = "Import successful";
    public static final String IMPORT_FAILED_MESSAGE = "Error in import. Please contact your administrator";
    private AddOnConfigurationService configurationService;
    private ImportService importService;

    public ImportController(AddOnConfigurationService configurationService, ImportService importService) {
        this.configurationService = configurationService;
        this.importService = importService;
    }

    @ApiOperation(
            value = "Start importing selected objects into the target system for the given configuration."
    )
    @PostMapping("/api/import/")
    public @ResponseBody
    List<GenericImportResponse> doImport(@RequestBody QueryExecutionRequest request, @RequestParam("configurationId") String configurationId) {
        Optional<AddOnEntity> configuration = configurationService.get(configurationId);
        if (configuration.isEmpty()) {
            return noSuchConfiguration();
        }

        importService.prepareSelectionForImport(request);

        Map<String, List<Map<String, String>>> linkedUuidsAndUrisByUuid = new HashMap<>();

        // For each
        List<GenericImportResponse> genericImportResponses = configuration
                .filter(AddOnEntity::isActive)
                .map(AddOnEntity::getImportConfiguration)
                .map(ImportEntity::getSteps)
                .map(List::stream)
                .map(configurations ->
                        configurations.map(step -> importService.sendData(request, step, configuration.get(), linkedUuidsAndUrisByUuid))
                                .collect(Collectors.toList()))
                .orElseGet(this::noSuchConfiguration);

        if (configuration.get().isSimpleFeedback()) {
            return buildSimpleFeedback(genericImportResponses);
        }
        return genericImportResponses;
    }

    private List<GenericImportResponse> noSuchConfiguration() {
        return Collections.singletonList(
                GenericImportResponse
                        .builder()
                        .success(false)
                        .errors(Collections.singletonList("Configuration not found or inactive"))
                        .build()
        );
    }

    private List<GenericImportResponse> buildSimpleFeedback(List<GenericImportResponse> genericImportResponses) {
        if (genericImportResponses.stream().allMatch(r -> r.isSuccess() && (r.getErrors() == null || r.getErrors().isEmpty()))) {
            return Collections.singletonList(
                    GenericImportResponse
                            .builder()
                            .success(true)
                            .successMessage(IMPORT_SUCCESSFUL_MESSAGE)
                            .errors(List.of())
                            .warnings(List.of())
                            .build());
        } else {
            return Collections.singletonList(
                    GenericImportResponse
                            .builder()
                            .success(false)
                            .errors(Collections.singletonList(IMPORT_FAILED_MESSAGE))
                            .warnings(List.of())
                            .build());

        }
    }
}

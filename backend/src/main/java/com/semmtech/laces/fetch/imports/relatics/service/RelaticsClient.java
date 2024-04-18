package com.semmtech.laces.fetch.imports.relatics.service;

import com.semmtech.laces.fetch.configuration.entities.*;
import com.semmtech.laces.fetch.configuration.service.RelaticsService;
import com.semmtech.laces.fetch.imports.generic.service.TargetClient;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.imports.generic.service.ImportService;
import com.semmtech.laces.fetch.imports.relatics.model.ImportResponse;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryResult;
import javax.naming.NoPermissionException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RelaticsClient extends TargetClient {

    private final WebserviceClient webserviceClient;
    private final ImportDataXmlProvider xmlProvider;
    private final RelaticsService relaticsService;

    public RelaticsClient(
            ImportService importService,
            WebserviceClient webserviceClient,
            RelaticsService relaticsService,
            ImportDataXmlProvider xmlProvider) {
        super(importService, EnumSet.of(EnvironmentType.Relatics));
        this.webserviceClient = webserviceClient;
        this.xmlProvider = xmlProvider;
        this.relaticsService = relaticsService;
    }

    @Override
    public GenericImportResponse doImport(ImportStepEntity step, AddOnEntity addOnEntity, QueryResult combinedResult, Stream<CommonParameter> commonParameters) throws NoPermissionException {
        return extractGenericResponse(
                webserviceClient.sendData(
                        step,
                        relaticsService.getRequiredWorkspaceForConfiguration(addOnEntity),
                        xmlProvider.toImportXml(combinedResult),
                        relaticsService.getServiceUrl(addOnEntity),
                        relaticsService.getTargetDataSystem(step.getImportTarget())
                ),
                step
        );
    }

    @Override
    public List<Map<String, String>> getAdditionalInputData(AddOnEntity addOnEntity) {
        if (addOnEntity.getVisualization() != null && addOnEntity.getVisualization().getAdditionalInputsConfiguration() != null) {
            WorkspaceEntity workspaceEntity = relaticsService.getRequiredWorkspaceForConfiguration(addOnEntity);
            return webserviceClient.readData(
                    workspaceEntity,
                    relaticsService.getServiceUrl(addOnEntity),
                    relaticsService.getTargetDataSystem(addOnEntity.getVisualization().getAdditionalInputsConfiguration()));
        }
        return new ArrayList<>();
    }

    private GenericImportResponse extractGenericResponse(ImportResponse importResponse, ImportStepEntity step) throws NoPermissionException {
        //Check for authentication issues
        if (importResponse.getImportResult().getExport() != null) {
            String authError = importResponse.getImportResult().getExport().getError();
            if (StringUtils.contains(authError.toLowerCase(), "no permissions")) {
                throw new NoPermissionException(authError);
            }
        }


        List<String> warningMessages = extractMessages(importResponse, message -> "Warning".equals(message.getResult()));
        List<String> errorMessages = extractMessages(importResponse, message -> "Error".equals(message.getResult()));
        List<String> successMessages = extractMessages(importResponse, message -> "Progress".equals(message.getResult()) && message.getValue().contains("Total rows imported"));
        boolean success = (CollectionUtils.isNotEmpty(successMessages) && errorMessages.isEmpty());

        return GenericImportResponse.builder()
                .importStep(step.getName())
                .success(success)
                .successMessage(success ? successMessages.get(0) : "")
                .errors(errorMessages)
                .warnings(warningMessages)
                .build();
    }

    private List<String> extractMessages(ImportResponse importResponse, Predicate<ImportResponse.ImportResult.Import.Message> messagePredicate) {
        return importResponse.getImportResult()
                .getImport()
                .getMessage()
                .stream()
                .filter(messagePredicate)
                .map(ImportResponse.ImportResult.Import.Message::getValue)
                .distinct()
                .collect(Collectors.toList());
    }
}

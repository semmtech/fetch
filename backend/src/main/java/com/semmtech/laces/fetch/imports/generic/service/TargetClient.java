package com.semmtech.laces.fetch.imports.generic.service;

import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.EnvironmentType;
import com.semmtech.laces.fetch.configuration.entities.ImportStepEntity;
import com.semmtech.laces.fetch.imports.generic.model.GenericImportResponse;
import com.semmtech.laces.fetch.visualization.model.CommonParameter;
import com.semmtech.laces.fetch.visualization.model.QueryResult;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.naming.NoPermissionException;

public abstract class TargetClient {
    private final EnumSet<EnvironmentType> supportedTypes;

    public TargetClient(ImportService importService, EnumSet<EnvironmentType> supportedTypes) {
        this.supportedTypes = supportedTypes;
        importService.registerTargetClient(this);
    }

    public abstract GenericImportResponse doImport(ImportStepEntity step, AddOnEntity addOnEntity, QueryResult combinedResult, Stream<CommonParameter> commonParameters) throws NoPermissionException;

    public abstract List<Map<String, String>> getAdditionalInputData(AddOnEntity addOnEntity);

    /**
     * A TargetClient is a technology/platform specific implementation of a way to get data into an external data system.
     * To allow for dynamic selection of the right client to execute, each client needs to specify for which type of
     * environment it can handle requests. The EnvironmentType would be specified on the configured Workspace/Environment.
     * @return an EnumSet containing the supported types, this is used when registering the .
     */
    public final EnumSet<EnvironmentType> supportedTypes() {
        return supportedTypes;
    }
}

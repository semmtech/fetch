package com.semmtech.laces.fetch.configuration.dtos.jsonapi;

import com.semmtech.laces.fetch.configuration.dtos.common.*;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.ImportEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ApiModel
public class AddOnDto extends com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto implements EntityProvider<AddOnEntity> {

    private JsonApiDto jsonApi;
    @ApiModelProperty(required = true)
    private VisualizationDto visualization;
    private List<ImportStepDto<JsonApiEndpointDto>> importSteps = new ArrayList<>();

    @Builder(builderMethodName = "addOnDtoBuilder")
    public AddOnDto(String id, String name, String description, String displayName, boolean active, boolean simpleFeedback,
                    Date startDate, Date endDate, SparqlEndpointDto sparqlEndpoint,VisualizationDto visualization,
                    @Singular List<ImportStepDto<JsonApiEndpointDto>> importSteps, JsonApiDto jsonApi) {

        super(id, name, description, displayName, active, simpleFeedback, startDate, endDate, sparqlEndpoint);
        this.visualization = visualization;
        this.jsonApi = jsonApi;
        this.importSteps = importSteps;
    }

    public AddOnDto(AddOnEntity configuration, JsonApiDto jsonApi, JsonApiEndpointDto additionalInfoEndpoint, Function<String, JsonApiEndpointDto> endpointMapper) {
        this(
                configuration.getId(),
                configuration.getName(),
                configuration.getDescription(),
                configuration.getDisplayName(),
                configuration.isActive(),
                configuration.isSimpleFeedback(),
                configuration.getStartDate(),
                configuration.getEndDate(),
                NullSafeEntityDtoMapper.toDto(configuration.getSparqlEndpoint(), SparqlEndpointDto::new),
                NullSafeEntityDtoMapper.toDto(configuration.getVisualization(), entity -> new VisualizationDto(entity, additionalInfoEndpoint)),
                toImportStepDtos(configuration.getImportConfiguration(), endpointMapper),
                jsonApi
        );
    }

    protected static List<ImportStepDto<JsonApiEndpointDto>> toImportStepDtos(ImportEntity importEntity, Function<String, JsonApiEndpointDto> endpointMapper) {
        if (importEntity != null && CollectionUtils.isNotEmpty(importEntity.getSteps())) {
            return importEntity.getSteps()
                    .stream()
                    .map(step -> new ImportStepDto<>(step, endpointMapper))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public AddOnEntity toEntity() {
        return
                toEntity(TargetType.JSONApi, jsonApi);
    }

}

package com.semmtech.laces.fetch.configuration.dtos.relatics;

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

@ApiModel
@Data
@NoArgsConstructor
public class AddOnDto extends com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto implements EntityProvider<AddOnEntity> {
    private WorkspaceDto workspace;
    @ApiModelProperty(required = true)
    private VisualizationDto visualization;
    private List<ImportStepDto<TargetDataSystemDto>> importSteps = new ArrayList<>();

    public AddOnDto(AddOnEntity configuration, WorkspaceDto workspaceDto, TargetDataSystemDto additionalInfoEndpoint, Function<String, TargetDataSystemDto> endpointMapper) {
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
                workspaceDto
        );
    }

    @Builder(builderMethodName = "addOnDtoBuilder")
    public AddOnDto(String id, String name, String description, String displayName, boolean active, boolean simpleFeedback, Date startDate,
                    Date endDate, SparqlEndpointDto sparqlEndpoint, VisualizationDto visualization,
                    @Singular List<ImportStepDto<TargetDataSystemDto>> importSteps, WorkspaceDto workspace) {
        super(id, name, description, displayName, active, simpleFeedback, startDate, endDate, sparqlEndpoint);
        this.visualization = visualization;
        this.workspace = workspace;
        this.importSteps = importSteps;
    }

    public static List<ImportStepDto<TargetDataSystemDto>> toImportStepDtos(ImportEntity importEntity, Function<String, TargetDataSystemDto> endpointMapper) {
        if (importEntity != null && CollectionUtils.isNotEmpty(importEntity.getSteps())) {
            return importEntity.getSteps()
                    .stream()
                    .map(step -> new ImportStepDto<>(step, endpointMapper))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public AddOnEntity toEntity() {
        return toEntity(TargetType.Relatics, workspace);
    }

}

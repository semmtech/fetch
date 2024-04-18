package com.semmtech.laces.fetch.configuration.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.entities.ImportEntity;
import com.semmtech.laces.fetch.configuration.entities.ImportStepEntity;
import com.semmtech.laces.fetch.streams.StreamUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "targetType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.class, name = "JSON API"),
        @JsonSubTypes.Type(value = com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.class, name = "Relatics")
})
public abstract class AddOnDto implements Identifiable {
    private String id;
    private String name;
    private String description;
    private String displayName;
    @JsonProperty(value = "isActive")
    private boolean active;
    @JsonProperty(value = "isSimpleFeedback")
    private boolean simpleFeedback;
    @ApiModelProperty(notes = "The start date in the format dd-MM-yyyy", example = "25-07-2019")
    private Date startDate;
    @ApiModelProperty(notes = "The end date in the format dd-MM-yyyy", example = "25-07-2019")
    private Date endDate;
    @ApiModelProperty(required = true)
    private SparqlEndpointDto sparqlEndpoint;

    protected ImportEntity mapImportConfigurations() {
        List<ImportStepEntity> steps = new ArrayList<>();
        if (getImportSteps() != null) {
            steps = StreamUtils.transformList(getImportSteps(), ImportStepDto::toEntity);
        }
        return ImportEntity.builder().steps(steps).build();
    }

    protected abstract List<? extends ImportStepDto> getImportSteps();

    protected abstract BaseVisualizationDto getVisualization();

    protected AddOnEntity toEntity(TargetType targetType, Identifiable dataTarget) {
        return AddOnEntity.builder()
                .targetType(targetType.getValue())
                .dataTarget(NullSafeIdProvider.getId(dataTarget))
                .description(getDescription())
                .displayName(getDisplayName())
                .id(getId())
                .active(isActive())
                .simpleFeedback(isSimpleFeedback())
                .name(getName())
                .startDate(getStartDate())
                .endDate(getEndDate())
                .visualization(getVisualization() != null ? getVisualization().toEntity() : null)
                .sparqlEndpoint(getSparqlEndpoint() != null ? getSparqlEndpoint().toEntity() : null)
                .importConfiguration(mapImportConfigurations())
                .build();
    }
}

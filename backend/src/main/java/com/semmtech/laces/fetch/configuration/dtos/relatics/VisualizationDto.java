package com.semmtech.laces.fetch.configuration.dtos.relatics;

import com.semmtech.laces.fetch.configuration.dtos.common.BaseVisualizationDto;
import com.semmtech.laces.fetch.configuration.dtos.common.ColumnDto;
import com.semmtech.laces.fetch.configuration.dtos.common.NullSafeIdProvider;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlQueryDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlQueryWithDefaultGraphsDto;
import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import lombok.*;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VisualizationDto extends BaseVisualizationDto {
    private TargetDataSystemDto additionalInputs;

    public VisualizationDto(VisualizationEntity visualization, TargetDataSystemDto additionalInputs) {
        super(visualization);
        this.additionalInputs = additionalInputs;
    }

    @Builder(builderMethodName = "visualizationDtoBuilder")
    public VisualizationDto(SparqlQueryWithDefaultGraphsDto rootsQuery, SparqlQueryWithDefaultGraphsDto childrenQuery,
                            LinkedHashMap<String, ColumnDto> visibleColumns, boolean enablePagination,
                            TargetDataSystemDto additionalInputs, SparqlQueryDto titleQuery) {

        super(rootsQuery, childrenQuery, visibleColumns, enablePagination, titleQuery);
        this.additionalInputs = additionalInputs;
    }

    @Override
    protected String getAdditionaInputsId() {
        return NullSafeIdProvider.getId(additionalInputs);
    }
}

package com.semmtech.laces.fetch.configuration.dtos.jsonapi;

import com.semmtech.laces.fetch.configuration.dtos.common.BaseVisualizationDto;
import com.semmtech.laces.fetch.configuration.dtos.common.ColumnDto;
import com.semmtech.laces.fetch.configuration.dtos.common.NullSafeIdProvider;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlQueryDto;
import com.semmtech.laces.fetch.configuration.dtos.common.SparqlQueryWithDefaultGraphsDto;
import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class VisualizationDto extends BaseVisualizationDto {

    private JsonApiEndpointDto additionalInputs;

    @Builder(builderMethodName = "visualizationDtoBuilder")
    public VisualizationDto(SparqlQueryWithDefaultGraphsDto rootsQuery, SparqlQueryWithDefaultGraphsDto childrenQuery,
                            LinkedHashMap<String, ColumnDto> visibleColumns, boolean enablePagination,
                            JsonApiEndpointDto additionalInputs, SparqlQueryDto titleQuery) {

        super(rootsQuery, childrenQuery, visibleColumns, enablePagination, titleQuery);
        this.additionalInputs = additionalInputs;
    }

    public VisualizationDto(VisualizationEntity visualization, JsonApiEndpointDto additionalInputs) {
        super(visualization);
        this.additionalInputs = additionalInputs;
    }

    @Override
    protected String getAdditionaInputsId() {
        return NullSafeIdProvider.getId(additionalInputs);
    }
}

package com.semmtech.laces.fetch.visualization.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semmtech.laces.fetch.configuration.dtos.common.FilterFieldDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootsQueryResponse extends QueryResult {

    private VisualizationMetadata visualizationMetadata;
    private List<FilterFieldDto> filters = new ArrayList<>();

    public RootsQueryResponse(QueryResult queryResult, VisualizationMetadata visualizationMetadata, List<FilterFieldDto> filterFields) {
        this(queryResult.getColumns(), queryResult.getValues(), visualizationMetadata, filterFields);
    }

    @Builder(builderMethodName = "rootsResponseBuilder")
    public RootsQueryResponse(List<Column> columns, List<Map<String, String>> values, VisualizationMetadata visualizationMetadata, @Singular List<FilterFieldDto> filters) {
        super(columns, values);
        this.visualizationMetadata = visualizationMetadata;
        this.filters = filters;
    }
}

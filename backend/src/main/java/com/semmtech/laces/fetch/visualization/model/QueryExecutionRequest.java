package com.semmtech.laces.fetch.visualization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryExecutionRequest {
    @Singular
    private List<CommonParameter> commonParameters = new ArrayList<>();

    @Singular
    private List<CommonParameter> filterParameters = new ArrayList<>();

    @Builder.Default
    private List<Map<String, String>> values = new ArrayList<>();

    @JsonIgnore
    public Stream<CommonParameter> getHeaderParameters() {
        return applyFilterToParameters(CommonParameter::isHeader);
    }

    @JsonIgnore
    public Stream<CommonParameter> getSparqlParameters() {
        return applyFilterToParameters(CommonParameter::isNoHeader);
    }

    private Stream<CommonParameter> applyFilterToParameters(Predicate<CommonParameter> predicate) {
        return commonParameters
                .stream()
                .filter(predicate);
    }

    public Map<String, String> getSelectedRecord() {
        if (CollectionUtils.isNotEmpty(values)) {
            return values.get(0);
        }
        return new HashMap<>();
    }



}

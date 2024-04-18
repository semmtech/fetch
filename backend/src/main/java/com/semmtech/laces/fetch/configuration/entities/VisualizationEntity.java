package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisualizationEntity {

    private SparqlQueryWithDefaultGraphs rootsQuery;
    private SparqlQueryWithDefaultGraphs childrenQuery;

    @Builder.Default
    private LinkedHashMap<String, ColumnEntity> columns = new LinkedHashMap<>();

    @JsonProperty(value ="additionalInputs")
    private String additionalInputsConfiguration;

    private boolean enablePagination;

    @DBRef
    private SparqlQueryEntity titleQuery;

    @JsonIgnore
    public List<FilterFieldEntity> getFilterFields() {
        if (rootsQuery != null) {
            return rootsQuery.getFilterFields();
        }
        return new ArrayList<>();
    }

    @JsonIgnore
    public boolean hasQuery(String queryId) {
        if (rootsQuery != null && rootsQuery.hasQuery(queryId)) {
            return true;
        }
        if (childrenQuery != null && childrenQuery.hasQuery(queryId)) {
            return true;
        }
        if (titleQuery != null && queryId.equals(titleQuery.getId())) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public boolean hasTarget(String targetId) {
        if (targetId.equals(additionalInputsConfiguration)) {
            return true;
        }
        return false;
    }
}

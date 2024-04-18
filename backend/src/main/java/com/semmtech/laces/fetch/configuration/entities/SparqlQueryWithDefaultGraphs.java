package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SparqlQueryWithDefaultGraphs {
    @DBRef
    private SparqlQueryEntity query;

    @Builder.Default
    private List<String> defaultGraphs = new ArrayList<>();

    @JsonIgnore
    public List<FilterFieldEntity> getFilterFields() {
        if (query != null) {
            return query.getFilterFields();
        }
        return new ArrayList<>();
    }

    @JsonIgnore
    public boolean hasQuery(String queryId) {
        if (query != null && queryId.equals(query.getId())) {
            return true;
        }
        return false;
    }
}

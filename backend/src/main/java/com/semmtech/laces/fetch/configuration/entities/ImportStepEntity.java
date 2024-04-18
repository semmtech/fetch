package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportStepEntity {
    String name;
    SparqlQueryWithDefaultGraphs sparqlQuery;
    String importTarget;

    @JsonIgnore
    public boolean hasQuery(String queryId) {
        if (sparqlQuery != null && sparqlQuery.hasQuery(queryId)) {
            return true;
        }
        return false;
    }
}

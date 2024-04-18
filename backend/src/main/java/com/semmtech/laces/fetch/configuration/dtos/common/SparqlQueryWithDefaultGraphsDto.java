package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.SparqlQueryWithDefaultGraphs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparqlQueryWithDefaultGraphsDto implements EntityProvider<SparqlQueryWithDefaultGraphs> {
    private SparqlQueryDto query;
    private List<String> defaultGraphs = new ArrayList<>();

    public SparqlQueryWithDefaultGraphsDto(SparqlQueryWithDefaultGraphs sparqlQueryWithDefaultGraphs) {
        if (sparqlQueryWithDefaultGraphs != null) {
            query = NullSafeEntityDtoMapper.toDto(sparqlQueryWithDefaultGraphs.getQuery(), SparqlQueryDto::new);
            defaultGraphs = sparqlQueryWithDefaultGraphs.getDefaultGraphs();
        }
    }

    public SparqlQueryWithDefaultGraphs toEntity() {
        return SparqlQueryWithDefaultGraphs.builder()
                .query(NullSafeEntityDtoMapper.toEntity(query))
                .defaultGraphs(defaultGraphs)
                .build();
    }

    public void setDefaultGraphs(List<String> defaultGraphs) {
        this.defaultGraphs = new ArrayList<>();
        if (defaultGraphs != null) {
            this.defaultGraphs = defaultGraphs;
        }
    }
}

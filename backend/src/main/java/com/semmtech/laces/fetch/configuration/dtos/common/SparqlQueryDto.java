package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.FilterFieldEntity;
import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import com.semmtech.laces.fetch.streams.StreamUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparqlQueryDto implements EntityProvider<SparqlQueryEntity> {
    private String id;
    private String name;
    private String description;
    private String query;
    private String type;
    private List<FilterFieldDto> filterFields;


    public SparqlQueryDto(SparqlQueryEntity sparqlQuery) {
        if (sparqlQuery != null) {
            this.id = sparqlQuery.getId();
            this.name = sparqlQuery.getName();
            this.description = sparqlQuery.getDescription();
            this.query = sparqlQuery.getQuery();
            this.type = sparqlQuery.getType();
            this.filterFields = StreamUtils.transformList(sparqlQuery.getFilterFields(), FilterFieldDto::new);
        }
    }

    public SparqlQueryEntity toEntity() {
        return SparqlQueryEntity.builder()
                .query(query)
                .description(description)
                .id(id)
                .name(name)
                .type(type)
                .filterFields(filterFieldsToEntities())
                .build();
    }

    private List<FilterFieldEntity> filterFieldsToEntities() {
        if (CollectionUtils.isNotEmpty(filterFields)) {
            return StreamUtils.transformList(filterFields, FilterFieldDto::toEntity);
        }
        return List.of();
    }
}

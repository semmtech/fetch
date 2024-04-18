package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.entities.ImportStepEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ImportStepDto<T extends Identifiable> implements EntityProvider<ImportStepEntity> {

    private String name;
    private SparqlQueryWithDefaultGraphsDto sparqlQuery;
    private T importTarget;

    public ImportStepDto(ImportStepEntity importStepEntity, Function<String, T> importStepMapper) {
        if (importStepEntity != null) {
            this.name = importStepEntity.getName();
            this.sparqlQuery = new SparqlQueryWithDefaultGraphsDto(importStepEntity.getSparqlQuery());
            this.importTarget = importStepMapper.apply(importStepEntity.getImportTarget());
        }
    }

    public ImportStepEntity toEntity() {
        return ImportStepEntity.builder()
                .name(getName())
                .sparqlQuery(NullSafeEntityDtoMapper.toEntity(getSparqlQuery()))
                .importTarget(NullSafeIdProvider.getId(importTarget))
                .build();
    }

}







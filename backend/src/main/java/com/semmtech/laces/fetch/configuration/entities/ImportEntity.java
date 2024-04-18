package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportEntity {
    private List<ImportStepEntity> steps;

    @JsonIgnore
    public boolean hasQuery(String queryId) {
        for (ImportStepEntity step : steps) {
            if (step.hasQuery(queryId)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean hasTarget(String targetId) {
        for (ImportStepEntity step : steps) {
            if (targetId.equals(step.getImportTarget())) {
                return true;
            }
        }
        return false;
    }
}

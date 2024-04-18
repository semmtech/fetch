package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.FilterFieldEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterFieldDto implements EntityProvider<FilterFieldEntity> {
    @ApiModelProperty(value = "The name property defines what the label in the filter pop-up will be.")
    private String name;
    @ApiModelProperty(value = "The type determines how the filter is visualized and which options a user has to enter data. Type \"Text\" would result in an text input field, \"Sparql\" would result in a drop down.", allowableValues = "Text, Sparql")
    private String type;
    @ApiModelProperty(value = "Query is optional and only expected for drop-down filters. Validation on configurations page in admin panel should not remove queries from the list that have filters without query.", allowEmptyValue = true)
    private String query;
    @ApiModelProperty(value = "The name of the binding variable to which the value to filter on will be applied.")
    private String variable;

    public FilterFieldDto(FilterFieldEntity entity) {
        if (entity != null) {
            name = entity.getName();
            type = entity.getType();
            query = entity.getQuery();
            variable = entity.getVariable();
        }
    }

    public FilterFieldEntity toEntity() {
        return FilterFieldEntity.builder()
                .name(name)
                .query(query)
                .type(type)
                .variable(variable)
                .build();
    }
}

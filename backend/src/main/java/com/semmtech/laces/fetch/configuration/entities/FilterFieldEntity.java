package com.semmtech.laces.fetch.configuration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterFieldEntity {
    private String name;
    private String type;
    private String query;
    private String variable;
}

package com.semmtech.laces.fetch.configuration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnEntity {
    private String bindingName;
    private String displayName;
    private boolean visible;

    public ColumnEntity(String name) {
        this(name, name, true);
    }
}

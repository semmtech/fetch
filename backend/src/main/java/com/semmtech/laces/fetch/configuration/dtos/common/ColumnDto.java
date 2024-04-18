package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.ColumnEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDto implements EntityProvider<ColumnEntity> {
    private String bindingName;
    private String displayName;
    private boolean visible;

    public ColumnDto(final ColumnEntity entity) {
        if (entity != null) {
            this.bindingName = entity.getBindingName();
            this.displayName = entity.getDisplayName();
            this.visible = entity.isVisible();
        }
    }

    public ColumnDto(String name) {
        this(name, name, true);
    }

    public ColumnEntity toEntity() {
        return ColumnEntity.builder()
                .bindingName(bindingName)
                .displayName(displayName)
                .visible(visible)
                .build();
    }
}

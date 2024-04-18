package com.semmtech.laces.fetch.visualization.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class Column {
    @NonNull
    private String name;
    private String display;
    @Builder.Default
    private boolean show = true;
}

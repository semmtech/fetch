package com.semmtech.laces.fetch.visualization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisualizationMetadata {
    private boolean enablePagination;
    private String title;
    private String subtitle;
}

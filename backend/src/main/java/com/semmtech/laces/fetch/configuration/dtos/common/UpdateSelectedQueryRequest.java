package com.semmtech.laces.fetch.configuration.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSelectedQueryRequest {
    private String previousSelectedRootsQueryId;
    private String selectedRootsQueryId;
    private LinkedHashMap<String, ColumnDto> configuredColumns;
}

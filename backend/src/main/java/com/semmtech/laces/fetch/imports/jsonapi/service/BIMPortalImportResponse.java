package com.semmtech.laces.fetch.imports.jsonapi.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BIMPortalImportResponse {
    private List<String> result;
    private String message;
}

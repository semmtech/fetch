package com.semmtech.laces.fetch.imports.generic.service;

import com.semmtech.laces.fetch.visualization.model.QueryExecutionRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QueryExecutionRequestParentsPreprocessor {
    public void removeParentsFromOrphans(QueryExecutionRequest request) {
        List<String> selectedURIs =
                request.getValues()
                        .stream()
                        .map(entry -> entry.get("uri"))
                        .collect(Collectors.toList());

        request.getValues().stream()
                .filter(entry -> !selectedURIs.contains(entry.get("parentUri")))
                .forEach(this::clearParentInfo);
    }

    private void clearParentInfo(Map<String, String> record) {
        // Intermediate list of keys is required to avoid ConcurrentModificationException on remove
        List<String> parentKeys = record.keySet()
                .stream()
                .filter(key -> key.startsWith("parent"))
                .collect(Collectors.toList());

        parentKeys.forEach(record::remove);
    }
}

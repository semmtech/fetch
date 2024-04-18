package com.semmtech.laces.fetch.sparql;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class SparqlTypeCache {
    Map<String, String> typesByVariableName = new HashMap<>();

    public void cache(SparqlResults results) {
        results.getBindings()
                .stream()
                .map(namedBinding -> namedBinding.entrySet())
                .flatMap(Set::stream)
                .forEach(namedBinding -> typesByVariableName.put(namedBinding.getKey(), namedBinding.getValue().getType()));
    }

    public String getTypeFor(String variableName) {
        return typesByVariableName.getOrDefault(variableName, "uri");
    }
}

package com.semmtech.laces.fetch.sparql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparqlResults {
    @Builder.Default
    List<Map<String, SparqlBinding>> bindings = new ArrayList<>();
}

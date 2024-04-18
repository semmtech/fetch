package com.semmtech.laces.fetch.configuration.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparqlEndpointTestRequest {
    private String selectedQuery;
    private List<String> defaultGraphs;
    private SparqlEndpointDto sparqlEndpoint;
}

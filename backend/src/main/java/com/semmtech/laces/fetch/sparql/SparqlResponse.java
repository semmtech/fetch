package com.semmtech.laces.fetch.sparql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparqlResponse {
    private SparqlHead head;
    private SparqlResults results;
}

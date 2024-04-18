package com.semmtech.laces.fetch.sparql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparqlHead {
    @JsonProperty("vars")
    @Singular
    List<String> variables;
}

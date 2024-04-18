package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@TypeAlias("SparqlQuery")
@Document(collection = "sparqlqueries")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SparqlQueryEntity implements Identifiable {
    @Id
    private String id;
    private String name;
    private String description;
    private String query;
    private String type;
    @Singular
    private List<FilterFieldEntity> filterFields;
}

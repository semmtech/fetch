package com.semmtech.laces.fetch.configuration.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias("SparqlEndpoint")
@Document(collection = "sparqlendpoints")
public class SparqlEndpointEntity implements Identifiable {
    @Id
    private String id;
    private String name;
    @ApiModelProperty(required = true, name = "url", notes = "A valid url pointing to a Sparql backend")
    private String url;
    @ApiModelProperty(required = true)
    private AuthenticationMethodEntity authenticationMethod;
}
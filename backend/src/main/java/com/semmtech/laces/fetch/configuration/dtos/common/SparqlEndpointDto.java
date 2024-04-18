package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparqlEndpointDto implements EntityProvider<SparqlEndpointEntity> {
    private String id;
    private String name;
    @ApiModelProperty(required = true, name = "url", notes = "A valid url pointing to a Sparql backend")
    private String url;
    @ApiModelProperty(required = true)
    private AuthenticationMethodDto authenticationMethod;

    public SparqlEndpointDto(SparqlEndpointEntity sparqlEndpointEntity) {
        if (sparqlEndpointEntity != null) {
            this.id = sparqlEndpointEntity.getId();
            this.name = sparqlEndpointEntity.getName();
            this.url = sparqlEndpointEntity.getUrl();
            if (sparqlEndpointEntity.getAuthenticationMethod() != null) {
                this.authenticationMethod = sparqlEndpointEntity.getAuthenticationMethod().toDto();
            }
        }
    }

    public SparqlEndpointEntity toEntity() {
        return SparqlEndpointEntity.builder()
                .id(id)
                .url(url)
                .name(name)
                .authenticationMethod(NullSafeEntityDtoMapper.toEntity(authenticationMethod))
                .build();
    }
}

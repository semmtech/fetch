package com.semmtech.laces.fetch.configuration.dtos.jsonapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.semmtech.laces.fetch.configuration.dtos.common.EntityProvider;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JsonApiDto implements Identifiable, EntityProvider<JsonApiEntity> {
    private String id;
    private String name;
    private String serviceUrl;
    @Builder.Default
    private List<JsonApiEndpointDto> endpoints = new ArrayList<>();

    public JsonApiDto(JsonApiEntity entity) {
        if (entity != null) {
            id = entity.getId();
            name = entity.getName();
            serviceUrl = entity.getServiceUrl();
            endpoints = new ArrayList<>();
        }
    }

    public JsonApiEntity toEntity() {
        return JsonApiEntity.builder()
                .id(id)
                .name(name)
                .serviceUrl(serviceUrl)
                .build();
    }

    @JsonIgnore
    public List<JsonApiEndpointDto> getEndpoints() {
        return endpoints;
    }

    @JsonProperty
    public void setEndpoints(List<JsonApiEndpointDto> endpoints) {
        this.endpoints = endpoints;
    }
}

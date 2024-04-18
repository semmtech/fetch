package com.semmtech.laces.fetch.configuration.dtos.jsonapi;

import com.semmtech.laces.fetch.configuration.dtos.common.EntityProvider;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.entities.JsonApiEndpointEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class JsonApiEndpointDto implements Identifiable, EntityProvider<JsonApiEndpointEntity> {
    private String id;
    private String name;
    private String path;
    private String type;

    public JsonApiEndpointDto(JsonApiEndpointEntity entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.path = entity.getPath();
            this.type = entity.getType();
        }
    }

    public JsonApiEndpointEntity toEntity() {
        return toApiLessEntity().build();
    }

    private JsonApiEndpointEntity.JsonApiEndpointEntityBuilder toApiLessEntity() {
        return JsonApiEndpointEntity.builder()
                .id(id)
                .name(name)
                .path(path)
                .type(type);
    }

    public JsonApiEndpointEntity toEntity(String apiId) {
        return toApiLessEntity().apiId(apiId).build();
    }
}

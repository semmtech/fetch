package com.semmtech.laces.fetch.configuration.dtos.relatics;

import com.semmtech.laces.fetch.configuration.dtos.common.EntityProvider;
import com.semmtech.laces.fetch.configuration.entities.EnvironmentEntity;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvironmentDto implements EntityProvider<EnvironmentEntity> {
    private String id;
    private String environmentId;
    private String name;
    private String serviceUrl;
    private String namespace;

    public String getName() {
        if (StringUtils.isEmpty(name) && StringUtils.isNotEmpty(serviceUrl)) {
            return serviceUrl;
        }
        return name;
    }

    public EnvironmentDto(EnvironmentEntity entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.environmentId = entity.getEnvironmentId();
            this.name = entity.getName();
            this.serviceUrl = entity.getServiceUrl();
            this.namespace = entity.getNamespace();
        }
    }

    @Override
    public EnvironmentEntity toEntity() {
        return
                EnvironmentEntity.builder()
                        .id(id)
                        .environmentId(environmentId)
                        .name(name)
                        .serviceUrl(serviceUrl)
                        .namespace(namespace)
                        .build();
    }
}

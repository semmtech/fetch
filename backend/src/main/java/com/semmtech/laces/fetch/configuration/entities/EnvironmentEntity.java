package com.semmtech.laces.fetch.configuration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("Environment")
@Document(collection = "environments")
public class EnvironmentEntity implements Identifiable {
    @Id
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
}

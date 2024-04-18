package com.semmtech.laces.fetch.configuration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "jsonendpoints")
public class JsonApiEndpointEntity implements Identifiable {
    @Id
    private String id;
    private String name;
    private String path;
    private String apiId;
    private String type;
}

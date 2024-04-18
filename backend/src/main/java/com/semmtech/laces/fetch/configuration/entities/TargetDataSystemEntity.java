package com.semmtech.laces.fetch.configuration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias("TargetDataSystemConfiguration")
@Document(collection = "targetConfigurations")
public class TargetDataSystemEntity implements Identifiable {
    @Id
    private String id;
    private String operationName;
    private String entryCode;
    private String xPathExpression;
    private String workspaceId;
    private String type;
}

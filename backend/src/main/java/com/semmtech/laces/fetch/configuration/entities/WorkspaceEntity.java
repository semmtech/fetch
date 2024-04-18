package com.semmtech.laces.fetch.configuration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeAlias("Workspace")
@Document(collection = "workspaces")
public class WorkspaceEntity implements Identifiable {
    @Id
    private String id;
    private String environmentId;
    private String workspaceId;
    private String workspaceName;
    private EnvironmentType environmentType;
}

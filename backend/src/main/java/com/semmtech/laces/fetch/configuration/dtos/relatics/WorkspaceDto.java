package com.semmtech.laces.fetch.configuration.dtos.relatics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.semmtech.laces.fetch.configuration.dtos.common.EntityProvider;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.entities.WorkspaceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class WorkspaceDto implements Identifiable, EntityProvider<WorkspaceEntity> {

    private String id;
    private String name;
    private String environmentId;
    private String workspaceId;

    @Singular
    private List<TargetDataSystemDto> targetDataSystems = new ArrayList<>();

    public WorkspaceDto() {
        targetDataSystems = new ArrayList<>();
    }

    public WorkspaceDto(String environmentId, String workspaceId, String name) {
        this.name = name;
        this.workspaceId = workspaceId;
        this.environmentId = environmentId;
        this.targetDataSystems = new ArrayList<>();
    }

    public WorkspaceDto(WorkspaceEntity cond) {
        this(cond.getEnvironmentId(), cond.getWorkspaceId(), cond.getWorkspaceName());
        id = cond.getId();
    }

    public WorkspaceEntity toEntity() {
        return WorkspaceEntity.builder()
                .id(id)
                .environmentId(environmentId)
                .workspaceName(name)
                .workspaceId(workspaceId)
                .build();
    }

    @JsonIgnore
    public List<TargetDataSystemDto> getTargetDataSystems() {
        return targetDataSystems;
    }

    @JsonProperty("targetDataSystems")
    public void setTargetDataSystems(List<TargetDataSystemDto> targetDataSystems) {
        this.targetDataSystems = targetDataSystems;
    }
}

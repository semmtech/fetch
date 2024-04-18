package com.semmtech.laces.fetch.configuration.dtos.relatics;

import com.semmtech.laces.fetch.configuration.dtos.common.EntityProvider;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetDataSystemDto implements Identifiable, EntityProvider<TargetDataSystemEntity> {
    private String id;
    private String operationName;
    private String entryCode;
    private String xPathExpression;
    private String workspaceId;
    private String type;

    public TargetDataSystemDto(TargetDataSystemEntity entity) {
        if (entity != null) {
            id = entity.getId();
            entryCode = entity.getEntryCode();
            operationName = entity.getOperationName();
            type = entity.getType();
            workspaceId = entity.getWorkspaceId();
            xPathExpression = entity.getXPathExpression();
        }
    }

    public TargetDataSystemDto(String operationName, String entryCode, String xPathExpression, String workspaceId, String type) {
        this.entryCode = entryCode;
        this.operationName = operationName;
        this.xPathExpression = xPathExpression;
        this.workspaceId = workspaceId;
        this.type = type;
    }


    public TargetDataSystemEntity toEntity() {
        return TargetDataSystemEntity.builder()
                .entryCode(entryCode)
                .xPathExpression(xPathExpression)
                .type(type)
                .workspaceId(workspaceId)
                .operationName(operationName)
                .id(id)
                .build();
    }

    public boolean haveConflictingProperties(TargetDataSystemEntity entity) {
        return !(entity != null &&
                StringUtils.equals(id, entity.getId()) &&
                StringUtils.equals(entryCode, entity.getEntryCode()) &&
                StringUtils.equals(operationName, entity.getOperationName()) &&
                StringUtils.equals(type, entity.getType()) &&
                StringUtils.equals(workspaceId, entity.getWorkspaceId()) &&
                StringUtils.equals(xPathExpression, entity.getXPathExpression()));
    }
}

package com.semmtech.laces.fetch.configuration.dtos.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.semmtech.laces.fetch.configuration.entities.AuthenticationMethodEntity;
import com.semmtech.laces.fetch.configuration.entities.SemmtechTokenAuthenticationEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.beans.ConstructorProperties;

@ApiModel(parent = AuthenticationMethodEntity.class,
        description = "The most secure authentication method. Requires a private key and " +
                "application id to sign the request. To make sure the API interprets it correctly, " +
                "add type:\"LACES_TOKEN\" to the JSON object.")
@Getter
@EqualsAndHashCode(callSuper = true)
public class SemmtechTokenAuthenticationDto extends AuthenticationMethodDto implements EntityProvider<AuthenticationMethodEntity> {
    @ApiModelProperty(required = true)
    private final String privateKey;
    @ApiModelProperty(required = true)
    private final String applicationId;

    @JsonCreator
    @ConstructorProperties({"privateKey", "applicationId"})
    public SemmtechTokenAuthenticationDto(String privateKey, String applicationId) {
        this.privateKey = privateKey;
        this.applicationId = applicationId;
    }

    @Override
    public AuthenticationMethodEntity toEntity() {
        return new SemmtechTokenAuthenticationEntity(privateKey, applicationId);
    }
}

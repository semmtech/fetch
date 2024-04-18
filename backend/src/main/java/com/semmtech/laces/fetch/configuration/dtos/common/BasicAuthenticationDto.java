package com.semmtech.laces.fetch.configuration.dtos.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.semmtech.laces.fetch.configuration.entities.AuthenticationMethodEntity;
import com.semmtech.laces.fetch.configuration.entities.BasicAuthenticationEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;

@ApiModel(parent = AuthenticationMethodDto.class,
        description = "Simple username-password security. This is less secure than using the semmtech token. " +
                "To make sure the API interprets it correctly, add type=\"BASIC\".")
@Data
@EqualsAndHashCode(callSuper = true)
public class BasicAuthenticationDto extends AuthenticationMethodDto implements EntityProvider<AuthenticationMethodEntity> {
    @ApiModelProperty(required = true)
    private final String userName;
    @ApiModelProperty(required = true)
    private final String password;

    @JsonCreator
    @ConstructorProperties({"userName", "password"})
    public BasicAuthenticationDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public AuthenticationMethodEntity toEntity() {
        return new BasicAuthenticationEntity(userName, password);
    }
}

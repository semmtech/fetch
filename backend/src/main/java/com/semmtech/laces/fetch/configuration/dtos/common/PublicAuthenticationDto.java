package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.AuthenticationMethodEntity;
import com.semmtech.laces.fetch.configuration.entities.PublicAuthenticationEntity;
import io.swagger.annotations.ApiModel;

@ApiModel(parent = AuthenticationMethodDto.class,
        description = "This authentication type is used when the endpoint is not secured. " +
                "To make sure the API interprets it correctly, add type=\"NONE\"")
public class PublicAuthenticationDto extends AuthenticationMethodDto implements EntityProvider<AuthenticationMethodEntity> {
    @Override
    public AuthenticationMethodEntity toEntity() {
        return new PublicAuthenticationEntity();
    }
}

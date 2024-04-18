package com.semmtech.laces.fetch.configuration.dtos.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.semmtech.laces.fetch.configuration.entities.AuthenticationMethodEntity;
import io.swagger.annotations.ApiModel;

@ApiModel(
        subTypes = {PublicAuthenticationDto.class, BasicAuthenticationDto.class, SemmtechTokenAuthenticationDto.class},
        discriminator = "type",
        description = "Abstract super class for PublicAuthentication, BasicAuthentication and " +
                "SemmtechTokenAuthentication. Use the type field in the JSON to make sure the right type is unmarshalled.")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PublicAuthenticationDto.class, name = "NONE"),
        @JsonSubTypes.Type(value = BasicAuthenticationDto.class, name = "BASIC"),
        @JsonSubTypes.Type(value = SemmtechTokenAuthenticationDto.class, name = "LACES_TOKEN")
})
public abstract class AuthenticationMethodDto implements EntityProvider<AuthenticationMethodEntity> {
    public abstract AuthenticationMethodEntity toEntity();
}

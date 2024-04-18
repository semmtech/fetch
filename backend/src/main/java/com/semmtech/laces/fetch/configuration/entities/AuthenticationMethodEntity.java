package com.semmtech.laces.fetch.configuration.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.semmtech.laces.fetch.configuration.dtos.common.AuthenticationMethodDto;
import io.swagger.annotations.ApiModel;
import org.springframework.http.HttpHeaders;

@ApiModel(
        subTypes = {PublicAuthenticationEntity.class, BasicAuthenticationEntity.class, SemmtechTokenAuthenticationEntity.class},
        discriminator = "type",
        description = "Abstract super class for PublicAuthentication, BasicAuthentication and " +
                "SemmtechTokenAuthentication. Use the type field in the JSON to make sure the right type is unmarshalled.")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PublicAuthenticationEntity.class, name = "NONE"),
        @JsonSubTypes.Type(value = BasicAuthenticationEntity.class, name = "BASIC"),
        @JsonSubTypes.Type(value = SemmtechTokenAuthenticationEntity.class, name = "LACES_TOKEN")
})
public abstract class AuthenticationMethodEntity {
    public abstract HttpHeaders headers(String url);
    public abstract AuthenticationMethodDto toDto();
}

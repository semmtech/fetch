package com.semmtech.laces.fetch.configuration.entities;

import com.semmtech.laces.fetch.configuration.dtos.common.AuthenticationMethodDto;
import com.semmtech.laces.fetch.configuration.dtos.common.BasicAuthenticationDto;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.http.HttpHeaders;

import java.nio.charset.Charset;
import java.util.Base64;
@ApiModel(parent = AuthenticationMethodEntity.class,
        description = "Simple username-password security. This is less secure than using the semmtech token. " +
                "To make sure the API interprets it correctly, add type=\"BASIC\".")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(value = "BasicAuthentication")
public class BasicAuthenticationEntity extends AuthenticationMethodEntity {

    private String userName;
    private String password;

    @Override
    public HttpHeaders headers(String url) {
        HttpHeaders headers = new HttpHeaders();
        String auth = userName + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        return headers;
    }

    @Override
    public AuthenticationMethodDto toDto() {
        return new BasicAuthenticationDto(userName, password);
    }
}


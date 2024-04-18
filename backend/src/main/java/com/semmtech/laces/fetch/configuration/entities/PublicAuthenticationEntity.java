package com.semmtech.laces.fetch.configuration.entities;

import com.semmtech.laces.fetch.configuration.dtos.common.AuthenticationMethodDto;
import com.semmtech.laces.fetch.configuration.dtos.common.PublicAuthenticationDto;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.http.HttpHeaders;

@TypeAlias(value = "PublicAuthentication")
public class PublicAuthenticationEntity extends AuthenticationMethodEntity {

    public HttpHeaders headers(String url) {
        return new HttpHeaders();
    }

    @Override
    public AuthenticationMethodDto toDto() {
        return new PublicAuthenticationDto();
    }
}

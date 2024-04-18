package com.semmtech.laces.fetch.configuration.dtos.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum TargetType {
    @JsonProperty("Relatics")
    Relatics("Relatics", com.semmtech.laces.fetch.configuration.dtos.relatics.AddOnDto.class),
    @JsonProperty("JSON API")
    JSONApi("JSON_API", com.semmtech.laces.fetch.configuration.dtos.jsonapi.AddOnDto.class);

    private final String value;
    private final Class<? extends AddOnDto> addOnDtoClass;

    public static TargetType fromValue(String requestedValue) {
        return Arrays.stream(TargetType.values())
                .filter(value -> StringUtils.equals(value.getValue(), requestedValue))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
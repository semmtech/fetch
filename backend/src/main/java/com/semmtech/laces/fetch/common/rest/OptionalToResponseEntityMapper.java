package com.semmtech.laces.fetch.common.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OptionalToResponseEntityMapper {

    public <T> ResponseEntity<T> buildEntity(Optional<T> response) {
        return response.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

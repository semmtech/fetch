package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.common.EntityProvider;
import com.semmtech.laces.fetch.configuration.dtos.common.NullSafeEntityDtoMapper;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.streams.StreamUtils;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Function;

@Api("laces-fetch-api")
public abstract class GenericController<T extends Identifiable, U extends EntityProvider<T>> {

    protected GenericService<T> service;
    protected OptionalToResponseEntityMapper responseMapper;
    protected Function<T, U> toDtoMapper;

    public GenericController(GenericService<T> service, OptionalToResponseEntityMapper responseMapper, Function<T, U> toDtoMapper) {
        this.service = service;
        this.responseMapper = responseMapper;
        this.toDtoMapper = toDtoMapper;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<U> findById(@NotNull @PathVariable("id") String id) {
        return responseMapper.buildEntity(
                service.get(id).map(toDtoMapper));
    }

    @GetMapping
    public ResponseEntity<List<U>> findAll() {
        return ResponseEntity.ok(
                StreamUtils.transformList(
                        service.getAll(),
                        toDtoMapper));
    }

    @PostMapping
    public ResponseEntity<U> create(@NotNull @RequestBody U newObject) {
        return ResponseEntity.ok(
                toDtoMapper.apply(
                        service.create(
                                NullSafeEntityDtoMapper.toEntity(newObject)
                        )));
    }

    @PutMapping
    public ResponseEntity<U> update(@NotNull @Valid @RequestBody U updatedObject) {
        return responseMapper.buildEntity(
                service.update(
                        NullSafeEntityDtoMapper.toEntity(updatedObject))
                        .map(toDtoMapper));
    }

    @DeleteMapping
    public ResponseEntity<List<String>> delete(@NotNull @RequestBody List<U> objectToDelete) {
        return ResponseEntity.ok(
                service.delete(
                        StreamUtils.transformList(objectToDelete, NullSafeEntityDtoMapper::toEntity)));
    }
}
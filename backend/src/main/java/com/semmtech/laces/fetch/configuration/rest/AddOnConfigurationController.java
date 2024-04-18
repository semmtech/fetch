package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto;
import com.semmtech.laces.fetch.configuration.dtos.common.AddOnDeleteRequest;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.configuration.facade.ServiceRegistry;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/configurations")
public class AddOnConfigurationController {
    private final OptionalToResponseEntityMapper responseEntityMapper;
    private final GenericService<AddOnEntity> addOnConfigurationService;
    private final ServiceRegistry<AddOnDto> addOnDtoServices;

    public AddOnConfigurationController(
            OptionalToResponseEntityMapper responseEntityMapper,
            GenericService<AddOnEntity> addOnConfigurationService,
            ServiceRegistry<AddOnDto> addOnDtoServices) {

        this.responseEntityMapper = responseEntityMapper;
        this.addOnConfigurationService = addOnConfigurationService;
        this.addOnDtoServices = addOnDtoServices;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AddOnDto> findById(@NotNull @PathVariable("id") String id) {
        return responseEntityMapper.buildEntity(
                addOnConfigurationService.get(id)
                        .map(addOnDtoServices::entityToDto));
    }

    @GetMapping
    public ResponseEntity<List<? extends AddOnDto>> findAll() {
        return ResponseEntity.ok(
                addOnConfigurationService.getAll()
                        .stream()
                        .map(addOnDtoServices::entityToDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AddOnDto> create(@RequestBody AddOnDto addOn) {
        return ResponseEntity.ok(addOnDtoServices.create(addOn));
    }

    @PutMapping
    public ResponseEntity<AddOnDto> update(@RequestBody AddOnDto addOn) {
        return responseEntityMapper.buildEntity(addOnDtoServices.update(addOn));
    }

    @DeleteMapping
    public ResponseEntity<Collection<String>> delete(@RequestBody Collection<AddOnDeleteRequest> addOnDeleteRequests) {
        Collection<AddOnEntity> addOnsToDelete =
                addOnDeleteRequests.stream()
                        .map(dto -> AddOnEntity.builder().id(dto.id).build())
                        .collect(Collectors.toList());
        return ResponseEntity.ok(addOnConfigurationService.delete(addOnsToDelete));
    }

    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        addOnConfigurationService.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}
package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.dtos.relatics.TargetDataSystemDto;
import com.semmtech.laces.fetch.configuration.entities.TargetDataSystemEntity;
import com.semmtech.laces.fetch.configuration.service.TargetSystemService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("laces-fetch-api")
@RestController
@RequestMapping("/api/targetsystems")
public class TargetSystemController extends GenericController<TargetDataSystemEntity, TargetDataSystemDto> {
    protected TargetSystemService service;

    public TargetSystemController(TargetSystemService service, OptionalToResponseEntityMapper responseMapper) {
        super(service, responseMapper, TargetDataSystemDto::new);
        this.service = service;
    }

    @GetMapping(params = "workspaceId")
    public ResponseEntity<List<TargetDataSystemEntity>> targetSystemsByWorkspace(@RequestParam("workspaceId") String workspaceId) {
        return ResponseEntity.ok(service.getTargetSystemsByWorkspace(workspaceId));
    }

    @GetMapping(path = "/clear-cache")
    public ResponseEntity<String> clearCache() {
        service.clearCache();
        return ResponseEntity.ok("Cache has been cleared!");
    }
}

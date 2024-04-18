package com.semmtech.laces.fetch.configuration.service;

import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import com.semmtech.laces.fetch.configuration.repository.FindByIdInRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SparqlEndpointService extends GenericService<SparqlEndpointEntity> {
    private final AddOnConfigurationService configurationService;

    public SparqlEndpointService(FindByIdInRepository<SparqlEndpointEntity, String> repository,
                                 AddOnConfigurationService configurationService) {
        super(repository);
        this.configurationService = configurationService;
    }

    public Optional<SparqlEndpointEntity> update(SparqlEndpointEntity endpointEntity) {
        final var endpointId = endpointEntity.getId();

        Optional<SparqlEndpointEntity> savedEntity = super.update(endpointEntity);

        refreshConfigurationCaches(endpointId);

        return savedEntity;
    }

    private void refreshConfigurationCaches(String endpointId) {
        log.debug("Refreshing Configurations which contain SparqlEndpoint {}...", endpointId);
        configurationService
                .findConfigurationsByEndpoint(endpointId)
                .forEach(configuration -> configurationService.refresh(configuration.getId()));
    }

    public List<String> delete(Collection<? extends SparqlEndpointEntity> objectsToDelete) {
        List<String> objectIds = objectsToDelete.stream().map(SparqlEndpointEntity::getId).collect(Collectors.toList());

        List<String> results = super.delete(objectsToDelete);
        objectIds.forEach(this::refreshConfigurationCaches);

        return results;
    }
}

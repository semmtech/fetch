package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.SparqlEndpointEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SparqlEndpointConfigurationRepository extends FindByIdInRepository<SparqlEndpointEntity, String> {
}

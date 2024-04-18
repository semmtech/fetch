package com.semmtech.laces.fetch.configuration.repository;

import com.semmtech.laces.fetch.configuration.entities.SparqlQueryEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SparqlQueryRepository extends FindByIdInRepository<SparqlQueryEntity, String> {
    List<SparqlQueryEntity> findByType(String type);
}

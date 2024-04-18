package com.semmtech.laces.fetch.configuration.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface FindByIdInRepository<T, U> extends MongoRepository<T, U> {
    List<T> findByIdIn(List<U> ids);
}

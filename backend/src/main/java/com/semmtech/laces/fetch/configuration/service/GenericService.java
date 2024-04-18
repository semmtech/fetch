package com.semmtech.laces.fetch.configuration.service;

import com.google.common.collect.Lists;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.exceptions.ItemAlreadyExistsException;
import com.semmtech.laces.fetch.configuration.repository.FindByIdInRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MHE (2021-04-21):
 * This service also contains a Map holding the cache; every operation also results in
 * either checking the cache or updating the cache. Should a cache miss occur, the repository is
 * checked (and cache updated).
 *
 * @param <U> The class for the entity used
 */
@Slf4j
@Validated
public class GenericService<U extends Identifiable> {

    protected final FindByIdInRepository<U, String> repository;
    private final Map<String, U> cache = new ConcurrentHashMap<>();

    public GenericService(FindByIdInRepository<U, String> repository) {
        this.repository = repository;
    }

    public void clearCache() {
        cache.clear();
    }

    public Optional<U> get(String id) {
        if (cache.containsKey(id)) {
            return Optional.ofNullable(cache.get(id));
        }
        return repository.findById(id).map(cache());
    }

    /**
     * Will be triggered if a single configuration needs to be refreshed in cache due to changes (update/delete) of
     * related entities (like SparqlQuery, SparqlEndpoint, TargetSystem, and/or Workspace).
     *
     * @param id
     */
    public void refresh(String id) {
        log.debug("Refreshing cache for Configurations {}...", id);
        repository.findById(id).map(cache());
    }

    public List<U> getAll() {
        if (!cache.isEmpty()) {
            return Lists.newArrayList(cache.values());
        }
        return repository.findAll().stream()
                .map(cache())
                .collect(Collectors.toList());
    }

    public U create(U newObject) {
        if (newObject.getId() != null && repository.existsById(newObject.getId())) {
            throw new ItemAlreadyExistsException(newObject);
        }
        U saved = save().apply(newObject);
        if (saved != null) {
            cache().apply(saved);
        }
        return saved;
    }

    public List<U> createMany(Collection<U> newObjects) {
        List<U> existing =
                repository.findByIdIn(extractIds(newObjects));
        if (CollectionUtils.isNotEmpty(existing)) {
            throw new ItemAlreadyExistsException(existing);
        }
        return repository.saveAll(newObjects)
                .stream()
                .map(cache())
                .collect(Collectors.toList());
    }

    private List<String> extractIds(Collection<U> newObjects) {
        return newObjects.stream()
                .map(U::getId)
                .collect(Collectors.toList());
    }

    //    @Transactional
    public Optional<U> update(U updatedObject) {
        return repository.findById(updatedObject.getId())
                .map(dbObject -> updatedObject)
                .map(save())
                .map(cache());
    }

    public List<U> updateMany(Collection<U> updatedObjects) {
        return repository.saveAll(updatedObjects)
                .stream()
                .map(cache())
                .collect(Collectors.toList());
    }

    protected Function<U, U> cache() {
        return obj -> {
            cache.put(obj.getId(), obj);
            return obj;
        };
    }

    protected Function<U, U> save() {
        return repository::save;
    }

    public List<String> delete(Collection<? extends U> objectsToDelete) {
        if (CollectionUtils.isNotEmpty(objectsToDelete)) {
            return objectsToDelete.stream()
                    .filter(objectToDelete -> repository.existsById(objectToDelete.getId()))
                    .map(this::delete)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String delete(U objectToDelete) {
        cache.remove(objectToDelete.getId());
        repository.delete(objectToDelete);
        return objectToDelete.getId();
    }
}

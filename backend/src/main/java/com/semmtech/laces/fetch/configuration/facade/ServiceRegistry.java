package com.semmtech.laces.fetch.configuration.facade;

import com.semmtech.laces.fetch.configuration.dtos.common.TargetType;
import com.semmtech.laces.fetch.configuration.entities.AddOnEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class is a facade for handling requests for controllers that accept different
 * types of objects as input or produce multiple different types of objects as output.
 *
 * For each subclass you want to handle, you can register the following services:
 * - create: create a new instance of this type in the DB
 * - update: update an existing instance of this type in the DB
 * - entityToDto: convert stored entities to a DTO of the right type
 *
 *  Services are registered as Functions
 *
 * @param <T> The supertype served by this registry. All handled types should be subclasses of T
 */
public class ServiceRegistry<T> {
    private Map<Class<? extends T>, Function<T,T>> createServiceMap = new HashMap<>();
    private Map<Class<? extends T>, Function<T,Optional<T>>> updateServiceMap = new HashMap<>();
    private Map<Class<? extends T>, Function<AddOnEntity, T>> entityToDtoMappers = new HashMap<>();

    public void registerCreate(Class<? extends T> handledClass, Function<T, T> creator) {
        createServiceMap.put(handledClass, creator);
    }

    public void registerUpdate(Class<? extends T> handledClass, Function<T, Optional<T>> updater) {
        updateServiceMap.put(handledClass, updater);
    }

    public void registerEntityToDtoMapper(Class<? extends T> handledClass, Function<AddOnEntity, T> mapper) {
        entityToDtoMappers.put(handledClass, mapper);
    }

    public T create(T objectToHandle) {
        return createServiceMap.get(objectToHandle.getClass()).apply(objectToHandle);
    }

    public Optional<T> update(T objectToHandle) {
        return updateServiceMap.get(objectToHandle.getClass()).apply(objectToHandle);
    }

    public T entityToDto(AddOnEntity entity) {
        var targetType = TargetType.fromValue(entity.getTargetType());
        return entityToDtoMappers.get(targetType.getAddOnDtoClass()).apply(entity);
    }
}

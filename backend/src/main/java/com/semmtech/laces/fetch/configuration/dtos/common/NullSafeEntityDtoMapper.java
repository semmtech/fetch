package com.semmtech.laces.fetch.configuration.dtos.common;

import java.util.function.Function;

public class NullSafeEntityDtoMapper {
    public static <Dto extends EntityProvider<Entity>, Entity> Entity toEntity(Dto dto) {
        return dto != null ? dto.toEntity() : null;
    }

    public static <Dto, Entity> Dto toDto(Entity entity, Function<Entity, Dto> entityToDtoMapper) {
        return entity != null ? entityToDtoMapper.apply(entity) : null;
    }
}

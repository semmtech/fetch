package com.semmtech.laces.fetch.configuration.rest;

import com.semmtech.laces.fetch.common.rest.OptionalToResponseEntityMapper;
import com.semmtech.laces.fetch.configuration.entities.Identifiable;
import com.semmtech.laces.fetch.configuration.service.GenericService;
import com.semmtech.laces.fetch.streams.StreamUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.semmtech.laces.fetch.streams.StreamUtils.transformList;

public class GenericDataTargetController
        <ParentDto extends Identifiable, ChildDto extends Identifiable,
                ParentEntity extends Identifiable, ChildEntity extends Identifiable> {

    private final OptionalToResponseEntityMapper responseMapper;
    private Map<UpdateStatus, BiFunction<GenericService<ChildEntity>, List<ChildEntity>, List<ChildEntity>>> updateActionsByStatus;

    public GenericDataTargetController(
            final BiFunction<GenericService<ChildEntity>, List<ChildEntity>, List<ChildEntity>> newFunction,
            final BiFunction<GenericService<ChildEntity>, List<ChildEntity>, List<ChildEntity>> updateFunction,
            final OptionalToResponseEntityMapper responseMapper
    ) {
        this.responseMapper = responseMapper;
        this.updateActionsByStatus =
                Map.of(
                        UpdateStatus.NEW, newFunction,
                        UpdateStatus.UPDATED, updateFunction,
                        UpdateStatus.UNCHANGED, (service, objects) -> objects);
    }

    public ResponseEntity<List<ParentDto>> findAll(GenericService<ParentEntity> service, Function<ParentEntity, ParentDto> dtoConverter) {
        List<ParentEntity> entities = service.getAll();
        List<ParentDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entities)) {
            dtos = StreamUtils.transformList(entities, dtoConverter);
        }
        return ResponseEntity.ok(dtos);
    }

    /**
     * Create a workspace or jsonapi. Since there are no hard links in the db between workspaces and targetdatasystems,
     * or jsonapi's and their endpoints, creating either of these requires saving the parent and their children in
     * separate steps.
     * @param parentDto the dto representing either the Workspace or the JsonApi
     * @param parentService the service to work with Workspaces or JsonApi's
     * @param childrenService the service to work with TargetDataSystems (Relatics webservices) or JsonApiEndpoints
     * @param childDtoFromParentFunction a function to retrieve the children from the supplied parent dto
     * @param childDtoToEntityFunction a function to transform a child dto into a corresponding child entity
     * @param parentDtoToEntityFunction a function to transform a parent dto into a corresponding parent entity
     * @param parentEntityToDtoFunction a function to transform a parent entity into a corresponding parent dto
     * @return a dto representing the newly created parent instance
     */
    public ResponseEntity<ParentDto> create(
            ParentDto parentDto,
            GenericService<ParentEntity> parentService,
            GenericService<ChildEntity> childrenService,
            Function<ParentDto, List<ChildDto>> childDtoFromParentFunction,
            BiFunction<ChildDto, String, ChildEntity> childDtoToEntityFunction,
            Function<ParentDto, ParentEntity> parentDtoToEntityFunction,
            Function<ParentEntity, ParentDto> parentEntityToDtoFunction
    ) {

        ParentEntity parentEntity = parentService.create(parentDtoToEntityFunction.apply(parentDto));

        List<ChildDto> children = childDtoFromParentFunction.apply(parentDto);
        if (CollectionUtils.isNotEmpty(children)) {
            List<ChildEntity> childrenToSave =
                    StreamUtils.transformList(children, dto -> childDtoToEntityFunction.apply(dto, parentEntity.getId()));

            List<ChildEntity> createdChildEntities = childrenService.createMany(childrenToSave);
        }
        return ResponseEntity.ok(parentEntityToDtoFunction.apply(parentEntity));
    }

    public ResponseEntity<ParentDto> update(
            ParentDto parentDto,
            GenericService<ParentEntity> parentService,
            GenericService<ChildEntity> childService,
            Function<String, List<ChildEntity>> childEntitiesProvider,
            Function<ParentDto, List<ChildDto>> childDtoFromParentFunction,
            BiPredicate<ChildDto, ChildEntity> childDtoAndEntityConflictsFunction,
            Function<ChildDto, ChildEntity> childDtoToEntityFunction,
            Function<ParentDto, ParentEntity> parentDtoToEntityFunction,
            Function<ParentEntity, ParentDto> parentEntityToDtoFunction,
            Function<ChildEntity, ChildDto> childEntityToDtoFunction,
            BiFunction<ParentDto, List<ChildDto>, ParentDto> updateChildrenInParentFunction) {

        Map<String, ChildEntity> existingChildren = fetchExistingChildren(parentDto, childEntitiesProvider);

        List<ChildDto> childDtosAfterUpdate = new ArrayList<>();
        final var suppliedChildren = childDtoFromParentFunction.apply(parentDto);
        if (CollectionUtils.isNotEmpty(suppliedChildren)) {
            var childEntitiesByUpdateStatus =
                    classifyAllWithUpdateStatus(childDtoAndEntityConflictsFunction, childDtoToEntityFunction,
                            existingChildren, suppliedChildren);

            childDtosAfterUpdate = updateChildren(childService, childEntityToDtoFunction, childEntitiesByUpdateStatus);
        }

        deleteUnmatchedChildren(childService, existingChildren);

        return responseMapper.buildEntity(
                updateParent(parentDto, parentService, parentDtoToEntityFunction, parentEntityToDtoFunction,
                        updateChildrenInParentFunction, childDtosAfterUpdate));

    }

    private Map<String, ChildEntity> fetchExistingChildren(ParentDto parentDto, Function<String, List<ChildEntity>> childEntitiesProvider) {
        return childEntitiesProvider.apply(parentDto.getId())
                .stream()
                .collect(Collectors.toMap(ChildEntity::getId, Function.identity()));
    }

    private Map<UpdateStatus, List<ChildEntity>> classifyAllWithUpdateStatus(
            BiPredicate<ChildDto, ChildEntity> childDtoAndEntityConflictsFunction,
            Function<ChildDto, ChildEntity> childDtoToEntityFunction,
            Map<String, ChildEntity> existingChildren,
            List<ChildDto> suppliedChildren) {

        final Collector<Tuple2<UpdateStatus, ChildEntity>, ?, Map<UpdateStatus, List<ChildEntity>>> tuple2MapCollector = Collectors.groupingBy(Tuple2::v1, Collectors.mapping(Tuple2::v2, Collectors.toList()));
        return suppliedChildren
                .stream()
                .map(childDto ->
                        classifyWithUpdateStatus(
                                childDto,
                                existingChildren,
                                childDtoAndEntityConflictsFunction,
                                childDtoToEntityFunction)
                )
                .collect(StreamUtils.groupByTupleValue());
    }

    private List<ChildDto> updateChildren(
            GenericService<ChildEntity> childService,
            Function<ChildEntity, ChildDto> childEntityToDtoFunction,
            Map<UpdateStatus, List<ChildEntity>> childEntitiesByUpdateStatus) {

        return childEntitiesByUpdateStatus
                .entrySet()
                .stream()
                .map(entityByStatus -> getUpdateAction(entityByStatus.getKey()).apply(childService, entityByStatus.getValue()))
                .flatMap(Collection::stream)
                .map(childEntityToDtoFunction)
                .collect(Collectors.toList());
    }

    private void deleteUnmatchedChildren(
            GenericService<ChildEntity> childService, Map<String, ChildEntity> existingChildren) {
        if (MapUtils.isNotEmpty(existingChildren)) {
            childService.delete(existingChildren.values());
        }
    }

    private Optional<ParentDto> updateParent(
            ParentDto parentDto,
            GenericService<ParentEntity> parentService,
            Function<ParentDto, ParentEntity> parentDtoToEntityFunction,
            Function<ParentEntity, ParentDto> parentEntityToDtoFunction,
            BiFunction<ParentDto, List<ChildDto>, ParentDto> updateChildrenInParentFunction,
            List<ChildDto> finalChildDtosAfterUpdate) {

        return parentService.update(parentDtoToEntityFunction.apply(parentDto))
                .map(parentEntityToDtoFunction)
                .map(dto -> updateChildrenInParentFunction.apply(dto, finalChildDtosAfterUpdate));
    }

    public ResponseEntity<List<String>> delete(
            List<ParentDto> parentsToDelete,
            GenericService<ParentEntity> parentService,
            GenericService<ChildEntity> childService,
            final Function<ParentDto, ParentEntity> parentEntityFunction,
            final Function<List<String>, List<ChildEntity>> childrenEntityProvider) {

        List<String> deletedParentObjectIds = parentService.delete(StreamUtils.transformList(parentsToDelete, parentEntityFunction));

        List<ChildEntity> childEntitiesToDelete = childrenEntityProvider.apply(deletedParentObjectIds);

        childService.delete(childEntitiesToDelete);

        return ResponseEntity.ok(deletedParentObjectIds);
    }

    private Tuple2<UpdateStatus, ChildEntity> classifyWithUpdateStatus(
            ChildDto endpointDto,
            Map<String, ChildEntity> childEntitiesById,
            BiPredicate<ChildDto, ChildEntity> dtoAndEntityConflictsFunction,
            Function<ChildDto, ChildEntity> linkedDtoToEntityFunction) {

        UpdateStatus status = UpdateStatus.UNCHANGED;
        if (childEntitiesById.containsKey(endpointDto.getId())) {
            if (dtoAndEntityConflictsFunction.test(endpointDto, childEntitiesById.get(endpointDto.getId()))) {
                status = UpdateStatus.UPDATED;
            }
            childEntitiesById.remove(endpointDto.getId());
        } else {
            status = UpdateStatus.NEW;
        }
        return new Tuple2<>(status, linkedDtoToEntityFunction.apply(endpointDto));
    }

    private BiFunction<GenericService<ChildEntity>, List<ChildEntity>, List<ChildEntity>> getUpdateAction(UpdateStatus status) {
        return updateActionsByStatus.getOrDefault(status, updateActionsByStatus.get(UpdateStatus.UNCHANGED));
    }

    private enum UpdateStatus {
        NEW, UPDATED, UNCHANGED;
    }
}

package com.semmtech.laces.fetch.configuration.dtos.common;

import com.semmtech.laces.fetch.configuration.entities.VisualizationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.jooq.lambda.tuple.Tuple2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseVisualizationDto implements EntityProvider<VisualizationEntity> {
    private SparqlQueryWithDefaultGraphsDto rootsQuery;
    private SparqlQueryWithDefaultGraphsDto childrenQuery;
    private LinkedHashMap<String, ColumnDto> columns;

    private boolean enablePagination;
    private SparqlQueryDto titleQuery;

    public BaseVisualizationDto(VisualizationEntity visualization) {
        if (visualization != null) {
            this.rootsQuery = NullSafeEntityDtoMapper.toDto(visualization.getRootsQuery(), SparqlQueryWithDefaultGraphsDto::new);
            this.childrenQuery = NullSafeEntityDtoMapper.toDto(visualization.getChildrenQuery(), SparqlQueryWithDefaultGraphsDto::new);
            this.columns = adaptColumns(visualization.getColumns(), entity -> NullSafeEntityDtoMapper.toDto(entity, ColumnDto::new));
            this.enablePagination = visualization.isEnablePagination();
            this.titleQuery = NullSafeEntityDtoMapper.toDto(visualization.getTitleQuery(), SparqlQueryDto::new);
        }
    }

    public VisualizationEntity toEntity() {
        return VisualizationEntity.builder()
                .enablePagination(enablePagination)
                .rootsQuery(NullSafeEntityDtoMapper.toEntity(rootsQuery))
                .childrenQuery(NullSafeEntityDtoMapper.toEntity(childrenQuery))
                .columns(adaptColumns(columns, NullSafeEntityDtoMapper::toEntity))
                .additionalInputsConfiguration(getAdditionaInputsId())
                .titleQuery(NullSafeEntityDtoMapper.toEntity(titleQuery))
                .build();
    }

    protected abstract String getAdditionaInputsId();

    /**
     * This function converts between maps with DTOs as value and Entity values.
     * @param originalMap the map to execute the conversion on
     * @param mapper a function that converts the objects from the original type into the desired type
     * @param <SourceType> the type from which we start the conversion. This is inferred from the input parameters.
     * @param <TargetType> the type which we convert to. This is inferred from the input parameters.
     * @return A map with the same keys, but the values are converted objects.
     */
    private <SourceType, TargetType> LinkedHashMap<String, TargetType> adaptColumns(Map<String, SourceType> originalMap, Function<SourceType, TargetType> mapper) {
        if (MapUtils.isNotEmpty(originalMap)) {
            return originalMap.entrySet()
                    .stream()
                    .map(entry -> new Tuple2<String, TargetType> (
                            entry.getKey(),
                            mapper.apply(entry.getValue())
                    ))
                    .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2, (a,b) -> a, LinkedHashMap::new));
        }
        return null;
    }
}

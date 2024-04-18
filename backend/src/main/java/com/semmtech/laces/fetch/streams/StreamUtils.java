package com.semmtech.laces.fetch.streams;

import com.semmtech.laces.fetch.configuration.rest.GenericDataTargetController;
import org.apache.commons.collections.CollectionUtils;
import org.jooq.lambda.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {
    public static final <T,U> Collector<Tuple2<T, U>, ?, Map<T, List<U>>> groupByTupleValue()  {
        return Collectors.groupingBy(Tuple2::v1, Collectors.mapping(Tuple2::v2, Collectors.toList()));
    }

    public static <T,U> List<U> transformList(List<T> source, Function<T, U> transformer) {
        if (CollectionUtils.isNotEmpty(source)) {
            return source.stream().map(transformer).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static <T> Stream<T> streamCollection(Collection<T> collection) {
        Stream<T> stream = Stream.of();
        if (CollectionUtils.isNotEmpty(collection)) {
            stream = collection.stream();
        }
        return stream;
    }
}

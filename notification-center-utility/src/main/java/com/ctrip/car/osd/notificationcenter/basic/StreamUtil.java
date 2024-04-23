package com.ctrip.car.osd.notificationcenter.basic;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xiayx on 2019/11/5.
 */
public final class StreamUtil {
    private StreamUtil() {
    }

    public static <T> Stream<T> streamNullable(final Collection<T> collection) {
        if (collection == null) {
            return Stream.empty();
        } else {
            return collection.stream();
        }
    }

    public static <T> Stream<T> streamNullable(final T[] array) {
        if (array == null) {
            return Stream.empty();
        } else {
            return Stream.of(array);
        }
    }

    public static <T> Stream<T> ofNullable(final T object) {
        if (object == null) {
            return Stream.empty();
        } else {
            return Stream.of(object);
        }
    }

    public static <K, V> Map<K, V> mapIdentity(final Collection<V> collection, final Function<V, K> keyMapper) {
        return streamNullable(collection).collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    public static <K, V> Map<K, V> mapIdentity(final V[] array, final Function<V, K> keyMapper) {
        return streamNullable(array).collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    public static <K, V> Map<K, List<V>> groupBy(final Collection<V> collection, final Function<V, K> keyMapper) {
        return streamNullable(collection).collect(Collectors.groupingBy(keyMapper));
    }

    public static <K, V> Map<K, List<V>> groupBy(final V[] array, final Function<V, K> keyMapper) {
        return streamNullable(array).collect(Collectors.groupingBy(keyMapper));
    }
}
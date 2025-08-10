package net.nemerosa.ontrack.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @deprecated Will be removed in V5
 */
@Deprecated
public class MapBuilder<K, V> {

    public static <K, V> MapBuilder<K, V> create() {
        return new MapBuilder<K, V>();
    }

    public static Map<String, Object> emptyParams() {
        return Collections.emptyMap();
    }

    public static MapBuilder<String, Object> params() {
        return create();
    }

    public static MapBuilder<String, Object> params(String key, Object value) {
        return of(key, value);
    }

    public static <K, V> MapBuilder<K, V> of(K key, V value) {
        return MapBuilder.<K, V>create().with(key, value);
    }

    private final Map<K, V> map = new LinkedHashMap<K, V>();

    public MapBuilder<K, V> with(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> get() {
        return map;
    }
}

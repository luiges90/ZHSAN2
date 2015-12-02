package com.zhsan.gameobject;

import com.zhsan.common.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * various caches
 * Created by Peter on 1/12/2015.
 */
public final class Caches {

    private Caches(){}

    /**
     * Convenience method for getting from caches.
     * @param cache The Cache map
     * @param key Key
     * @param ifMiss Supplier returning the value of the corresponding key, will be calculated if cache misses
     * @param <K> Key Type
     * @param <V> Value Type
     * @return Value
     */
    public static <K, V> V get(Map<K, V> cache, K key, Supplier<V> ifMiss) {
        if (!cache.containsKey(key)) {
            cache.put(key, ifMiss.get());
        }
        return cache.get(key);
    }

    public static final Map<Point, Architecture> architectureAtPoint = new HashMap<>();

    public static final Map<Troop, Military> troopMilitaries = new HashMap<>();

    public static final Map<TerrainDetail, Boolean> isTerrainPassableByAnyMilitaryKind = new HashMap<>();

}

package com.zhsan.gameobject;

import java.util.*;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameObjectList<T extends GameObject> implements Iterable<T> {

    private SortedMap<Integer, T> content = new TreeMap<>();

    public void add(T obj) {
        content.put(obj.getId(), obj);
    }

    public T get(int id) {
        return content.get(id);
    }

    @Override
    public Iterator<T> iterator() {
        return content.values().iterator();
    }

    public int size() {
        return content.size();
    }

}

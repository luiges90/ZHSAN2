package com.zhsan.gameobject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameObjectList<T extends GameObject> implements Iterable<T> {

    private HashMap<Integer, T> content = new HashMap<>();

    public void add(T obj) {
        content.put(obj.id, obj);
    }

    public T get(int id) {
        return content.get(id);
    }

    @Override
    public Iterator<T> iterator() {
        return content.values().iterator();
    }

}

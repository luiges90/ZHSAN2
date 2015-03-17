package com.zhsan.gameobject;

import java.util.*;

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

    public List<T> getListOrderedById() {
        List<T> t = new ArrayList<>(content.values());
        t.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.id - o2.id;
            }
        });
        return t;
    }

}

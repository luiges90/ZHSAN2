package com.zhsan.gameobject;

import java.util.*;
/**
 * Created by Peter on 17/3/2015.
 */
public class GameObjectList<T extends GameObject> implements Iterable<T> {

    private SortedMap<Integer, T> content = new TreeMap<>();
    private boolean unmodifiable = false;

    public GameObjectList(){}

    public GameObjectList(GameObjectList<T> old) {
        content = new TreeMap<>(old.content);
        unmodifiable = old.unmodifiable;
    }

    public void add(T obj) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        content.put(obj.getId(), obj);
    }

    public T get(int id) {
        return content.get(id);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return content.values().iterator().hasNext();
            }

            @Override
            public T next() {
                return content.values().iterator().next();
            }

            @Override
            public void remove() {
                if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
                content.values().iterator().remove();
            }
        };
    }

    public int size() {
        return content.size();
    }

    /**
     * Return a new GameObjectList that is not modifiable
     * @return
     */
    public GameObjectList<T> asUnmodifiable() {
        GameObjectList<T> result = new GameObjectList<>(this);
        result.unmodifiable = true;
        return result;
    }

}

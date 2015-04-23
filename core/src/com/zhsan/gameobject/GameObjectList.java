package com.zhsan.gameobject;

import java.util.*;
/**
 * Created by Peter on 17/3/2015.
 */
public class GameObjectList<T extends GameObject> implements Iterable<T> {

    private SortedMap<Integer, T> content = new TreeMap<>();
    private boolean unmodifiable = false;

    public GameObjectList(){}

    public GameObjectList(GameObjectList<T> old, boolean unmodifiable) {
        if (unmodifiable) {
            content = Collections.unmodifiableSortedMap(old.content);
        } else {
            content = new TreeMap<>(old.content);
        }
        this.unmodifiable = unmodifiable;
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
        return content.values().iterator();
    }

    public int size() {
        return content.size();
    }

    /**
     * Return a new GameObjectList that is not modifiable
     * @return
     */
    public GameObjectList<T> asUnmodifiable() {
        GameObjectList<T> result = new GameObjectList<>(this, true);
        return result;
    }

}

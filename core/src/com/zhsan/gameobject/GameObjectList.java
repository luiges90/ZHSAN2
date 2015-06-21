package com.zhsan.gameobject;

import com.zhsan.gamecomponents.common.XmlHelper;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public void addAll(GameObjectList<T> objs) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        content.putAll(objs.content);
    }

    public T get(int id) {
        return content.get(id);
    }

    public T getFirst() {
        return content.isEmpty() ? null : content.get(content.firstKey());
    }

    public Collection<T> getAll() {
        return content.values();
    }

    public boolean contains(T t) {
        return content.containsValue(t);
    }

    public T remove(T t) {
        return content.remove(t.getId());
    }

    @Override
    public Iterator<T> iterator() {
        // TODO make this be unmodifiable
        return content.values().iterator();
    }

    public GameObjectList<T> filter(Predicate<T> predicate) {
        return content.values().stream().filter(predicate).collect(new ToGameObjectList<>());
    }

    public int size() {
        return content.size();
    }

    public int getFreeId() {
        return content.isEmpty() ? 1 : content.lastKey() + 1;
    }

    public GameObjectList<T> getItemsFromCSV(String s) {
        List<Integer> ids = XmlHelper.loadIntegerListFromXml(s);
        return content.values().stream().filter(x -> ids.contains(x.getId())).collect(new ToGameObjectList<>());
    }

    public String toCSV() {
        return content.keySet().stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    /**
     * Return a new GameObjectList that is not modifiable
     * @return
     */
    public GameObjectList<T> asUnmodifiable() {
        return new GameObjectList<>(this, true);
    }

    private class ToGameObjectList<T extends GameObject> implements Collector<T, GameObjectList<T>, GameObjectList<T>> {

        @Override
        public Supplier<GameObjectList<T>> supplier() {
            return GameObjectList<T>::new;
        }

        @Override
        public BiConsumer<GameObjectList<T>, T> accumulator() {
            return GameObjectList::add;
        }

        @Override
        public BinaryOperator<GameObjectList<T>> combiner() {
            return (x, y) -> {
                x.addAll(y);
                return x;
            };
        }

        @Override
        public Function<GameObjectList<T>, GameObjectList<T>> finisher() {
            return x -> x;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(
                    Characteristics.IDENTITY_FINISH,
                    Characteristics.CONCURRENT,
                    Characteristics.UNORDERED));
        }
    }

}

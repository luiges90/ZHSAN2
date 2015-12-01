package com.zhsan.gameobject;

import com.zhsan.gamecomponents.common.XmlHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Peter on 17/3/2015.
 */
public class GameObjectList<T extends GameObject> extends AbstractCollection<T> {

    private SortedMap<Integer, T> content = new TreeMap<>();
    private boolean unmodifiable = false;

    public GameObjectList(){}

    public GameObjectList(GameObjectList<T> old) {
        this(old, false);
    }

    public GameObjectList(GameObjectList<T> old, boolean unmodifiable) {
        if (unmodifiable) {
            content = Collections.unmodifiableSortedMap(old.content);
        } else {
            content = new TreeMap<>(old.content);
        }
        this.unmodifiable = unmodifiable;
    }

    public boolean add(T obj) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        if (obj != null) {
            content.put(obj.getId(), obj);
            return true;
        } else {
            return false;
        }
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

    @Override
    public boolean remove(Object o) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        return super.remove(o);
    }

    public T remove(T t) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        return content.remove(t.getId());
    }

    public List<T> shuffledList() {
        List<T> result = new ArrayList<>(content.values());
        Collections.shuffle(result);
        return result;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Iterator<T> parent = content.values().iterator();

            @Override
            public boolean hasNext() {
                return parent.hasNext();
            }

            @Override
            public T next() {
                return parent.next();
            }

            @Override
            public void remove() {
                if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
                parent.remove();
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                parent.forEachRemaining(action);
            }
        };
    }

    public GameObjectList<T> filter(Predicate<T> predicate) {
        return content.values().stream().filter(predicate).collect(Collectors.toCollection(GameObjectList<T>::new));
    }

    public boolean remove(Predicate<T> predicate) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        return content.values().removeIf(predicate);
    }

    public List<T> sort(Comparator<T> comparator) {
        return content.values().stream().sorted(comparator).collect(Collectors.toList());
    }

    public T max(Comparator<T> comparator) {
        return content.values().stream().max(comparator).get();
    }

    public T max(Comparator<T> comparator, T def) {
        return content.values().stream().max(comparator).orElse(def);
    }

    public T min(Comparator<T> comparator) {
        return content.values().stream().min(comparator).get();
    }

    public T min(Comparator<T> comparator, T def) {
        return content.values().stream().min(comparator).orElse(def);
    }

    public int size() {
        return content.size();
    }

    public int getFreeId() {
        return content.isEmpty() ? 1 : content.lastKey() + 1;
    }

    public static <T extends GameObject> GameObjectList<T> singleton(T item) {
        GameObjectList<T> r = new GameObjectList<>();
        r.add(item);
        return r;
    }

    public GameObjectList<T> getItemsFromCSV(String s) {
        List<Integer> ids = XmlHelper.loadIntegerListFromXml(s);
        return content.values().stream().filter(x -> ids.contains(x.getId())).collect(Collectors.toCollection(GameObjectList<T>::new));
    }

    public GameObjectList<T> getItemsFromIds(Collection<Integer> list) {
        return list.stream().map(content::get).collect(Collectors.toCollection(GameObjectList<T>::new));
    }

    public String toCSV() {
        return content.keySet().stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return "GameObjectList{" +
                "content=" + content +
                ", unmodifiable=" + unmodifiable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameObjectList<?> that = (GameObjectList<?>) o;

        return content.equals(that.content);

    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }
}

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

    public void add(T obj) {
        if (unmodifiable) throw new IllegalStateException("This list has been made unmodifiable");
        if (obj != null) {
            content.put(obj.getId(), obj);
        }
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

    public List<T> shuffledList() {
        List<T> result = new ArrayList<>(content.values());
        Collections.shuffle(result);
        return result;
    }

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
                throw new UnsupportedOperationException("This list has been made unmodifiable");
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                parent.forEachRemaining(action);
            }
        };
    }

    public GameObjectList<T> filter(Predicate<T> predicate) {
        return content.values().parallelStream().filter(predicate).collect(new ToGameObjectList<>());
    }

    public boolean remove(Predicate<T> predicate) {
        return content.values().removeIf(predicate);
    }

    public List<T> sort(Comparator<T> comparator) {
        return content.values().parallelStream().sorted(comparator).collect(Collectors.toList());
    }

    public T max(Comparator<T> comparator) {
        return content.values().parallelStream().max(comparator).get();
    }

    public T max(Comparator<T> comparator, T def) {
        return content.values().parallelStream().max(comparator).orElse(def);
    }

    public T min(Comparator<T> comparator) {
        return content.values().parallelStream().min(comparator).get();
    }

    public T min(Comparator<T> comparator, T def) {
        return content.values().parallelStream().min(comparator).orElse(def);
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
        return content.values().parallelStream().filter(x -> ids.contains(x.getId())).collect(new ToGameObjectList<>());
    }

    public String toCSV() {
        return content.keySet().parallelStream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    public static class ToGameObjectList<T extends GameObject> implements Collector<T, GameObjectList<T>, GameObjectList<T>> {

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
                    Characteristics.CONCURRENT));
        }
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

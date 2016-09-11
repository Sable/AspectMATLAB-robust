package utils;

import java.util.*;

/** an implementation of bijective map (i.e. no two keys have the same value) */
public class UniqueMap<K, V> extends AbstractMap<K, V> implements Cloneable {
    private final HashMap<K, V> directMap = new HashMap<>();
    private final HashMap<V, K> inverseMap = new HashMap<>();

    /**
     * implementation on entry set required by {@link AbstractMap}, the {@link Iterator} of the return set has
     * implemented the remove method
     * @return a set containing all key-value pairs defined in the map
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    private final Iterator<Entry<K, V>> directMapIterator = directMap.entrySet().iterator();
                    private Entry<K, V> prev = null;
                    @Override
                    public boolean hasNext() {
                        return directMapIterator.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        if (!directMapIterator.hasNext()) throw new NoSuchElementException();
                        prev = directMapIterator.next();
                        return prev;
                    }

                    @Override
                    public void remove() {
                        directMap.remove(Optional.ofNullable(prev).orElseThrow(IllegalStateException::new).getKey());
                        inverseMap.remove(Optional.ofNullable(prev).orElseThrow(IllegalStateException::new).getValue());
                    }
                };
            }

            @Override
            public int size() {
                return directMap.size();
            }
        };
    }

    /**
     * add a give key-value pair to the map
     * @param key key
     * @param value value
     * @return if the map has already assigned a value to the key, then such value will be returned, otherwise it
     * will return {@code null}
     * @throws NullPointerException if {@code key} or {@code value} is null
     */
    @Override
    public V put(K key, V value) {
        if (key == null) throw new NullPointerException();
        if (value == null) throw new NullPointerException();
        if (directMap.values().contains(value) && !inverseMap.get(value).equals(key)) {
            throw new IllegalArgumentException();
        }
        inverseMap.remove(directMap.get(key));
        inverseMap.put(value, key);
        return directMap.put(key, value);
    }

    /**
     * retrieve value by a given key
     * @param key key
     * @return the associated value of such key in the map
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Override
    public V get(Object key) {
        return directMap.get(Optional.ofNullable(key).orElseThrow(NullPointerException::new));
    }

    /**
     * retrieve key by a given value
     * @param value value
     * @return the associated key of such value in the map
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public K getInverse(Object value) {
        return inverseMap.get(Optional.ofNullable(value).orElseThrow(NullPointerException::new));
    }

    /**
     * build a inverse map of such map, i.e. if in the current map A is associate to B as a key, then in the returned
     * map, B will associate to A as a key
     * @return the inverse map
     */
    public UniqueMap<V, K> getInverseMap() {
        UniqueMap<V, K> retMap = new UniqueMap<V, K>();
        entrySet().stream().forEach(entry -> retMap.put(entry.getValue(), entry.getKey()));
        return retMap;
    }

    /**
     * build a copy of this map, notice that this will only copy the map, the returning map will containing the same
     * reference as the current map
     * @return the copied map
     */
    @Override
    public UniqueMap<K, V> clone() {
        UniqueMap<K, V> returnMap = new UniqueMap<>();
        this.entrySet().forEach(entry -> returnMap.put(entry.getKey(), entry.getValue()));
        return returnMap;
    }

    @Override
    public String toString() {
        return directMap.toString();
    }
}

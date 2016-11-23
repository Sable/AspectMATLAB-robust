package utils;

import java.util.*;

/** generate literal {@link java.util.List} and {@link java.util.Set} */
public class LiteralBuilder<T> {
    private LinkedList<T> bufferList = new LinkedList<T>();

    /**
     * add a elements to the internal buffer
     * @param element the element to add
     * @return the builder itself
     */
    public LiteralBuilder<T> put(T element) {
        bufferList.add(element);
        return this;
    }

    /**
     * add elements to the internal buffer
     * @param elements elements to add
     * @return the builder itself
     */
    public LiteralBuilder<T> put(T... elements) {
        for (T element : elements) bufferList.add(element);
        return this;
    }

    /**
     * add elements to the internal buffer, it will be added according the order specify by the iterator
     * @param elements elements to add
     * @return the builder itself
     * @throws NullPointerException if {@code elements} is {@code null}
     */
    public LiteralBuilder<T> putAll(Iterable<T> elements) {
        Optional.ofNullable(elements).orElseThrow(NullPointerException::new).forEach(bufferList::add);
        return this;
    }

    /**
     * generate a set from buffer
     * @return the generated set
     * @throws IllegalArgumentException if duplicate elements are found
     */
    public Set<T> asSet() {
        HashSet<T> builtSet = new HashSet<T>();
        for (T element : bufferList) {
            if (builtSet.contains(element)) {
                throw new IllegalArgumentException();
            } else {
                builtSet.add(element);
            }
        }
        return builtSet;
    }

    /**
     * generate a list from buffer
     * @return the generated list
     */
    public List<T> asList() {
        return bufferList;
    }
}
package aspectMATLAB.utils;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergableHashSet<T> extends HashSet<T> implements SetOperation<MergableHashSet<T>> {
    /**
     * @param set the given set
     * @return returns the intersection between this set and the given set
     * @throws NullPointerException if {@code set} is {@code null}
     */
    @Override
    public MergableHashSet<T> intersection(MergableHashSet<T> set) {
        if (set == null) throw new NullPointerException();
        MergableHashSet<T> retSet = new MergableHashSet<>();
        retSet.addAll(Stream.concat(this.stream(), set.stream())
                .filter(element -> (this.contains(element) && set.contains(element)))
                .collect(Collectors.toSet()));
        return retSet;
    }

    /**
     * @param set the given set
     * @return returns the union between this set and the given set
     * @throws NullPointerException if {@code set} is {@code null}
     */
    @Override
    public MergableHashSet<T> union(MergableHashSet<T> set) {
        if (set == null) throw new NullPointerException();
        MergableHashSet<T> retSet = new MergableHashSet<>();
        retSet.addAll(Stream.concat(this.stream(), set.stream()).collect(Collectors.toSet()));
        return retSet;
    }

    /**
     * @param set the given set
     * @return returns this set subtract the given set
     * @throws NullPointerException if {@code set} is {@code null}
     */
    @Override
    public MergableHashSet<T> subtraction(MergableHashSet<T> set) {
        if (set == null) throw new NullPointerException();
        MergableHashSet<T> retSet = new MergableHashSet<>();
        retSet.addAll(this.stream().filter(element -> !set.contains(element)).collect(Collectors.toSet()));
        return retSet;
    }
}

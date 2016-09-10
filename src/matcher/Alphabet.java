package matcher;

import utils.SetOperation;
import utils.UniqueMap;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * an implementation of alphabet in extended argument matching algorithm
 * @param <T> alphabet base class
 */
public class Alphabet<T> implements SetOperation<Alphabet<T>> {
    private final Iterator<Integer> letterCodeAssigner = IntStream.iterate(2, x -> x + 1).iterator();
    private final UniqueMap<Integer, T> alphabetMap = new UniqueMap<>();
    public static final int epsilonTransitionCode = 0;
    public static final int sigmaTransitionCode = 1;

    /**
     * retrieve letter code by a given letter
     * @param letter letter
     * @return letter code associated with the letter
     * @throws NullPointerException if {@code letter} is {@code null}
     */
    public int getLetterCode(T letter) {
        if (letter == null) throw new NullPointerException();
        return alphabetMap.values().contains(letter)?alphabetMap.getInverse(letter):sigmaTransitionCode;
    }

    /**
     * retrieve letter by a given letter code, if such letter code does not assigned to any letter {@code null} will
     * be returned.
     * @param code letter code
     * @return letter associated with the letter code
     */
    public T getLetter(int code) {
        return alphabetMap.keySet().contains(code)?alphabetMap.get(code):null;
    }

    /**
     * add a letter to the alphabet, if such letter is already existed in the alphabet, it will get ignored
     * @param letter letter to append
     * @return an reference to such alphabet
     * @throws NullPointerException if {@code letter} is {@code null}
     */
    public Alphabet<T> add(T letter) {
        if (letter == null) throw new NullPointerException();
        if (alphabetMap.values().contains(letter)) return this;
        alphabetMap.put(letterCodeAssigner.next(), letter);
        return this;
    }

    /**
     * add letters to the alphabet. If letters contains duplicate letter, only one occurrence of the letter will be
     * added to the alphabet.
     * @param letters letters to add
     * @return an reference to such alphabet
     * @throws NullPointerException if {@code letters} is {@code null}
     * @throws IllegalArgumentException if any of {@code letters} is {@code null}
     */
    public Alphabet<T> addAll(Collection<T> letters) {
        Optional.ofNullable(letters).orElseThrow(NullPointerException::new)
                .forEach(letter -> add(Optional.ofNullable(letter).orElseThrow(IllegalArgumentException::new)));
        return this;
    }

    /**
     * construct map from such alphabet
     * @return constructed map
     */
    public UniqueMap<Integer, T> toMap() {
        UniqueMap<Integer, T> retMap = new UniqueMap<>();
        alphabetMap.forEach((key, value) -> retMap.put(key, value));
        return retMap;
    }

    /**
     * perform a mathematical intersection of such alphabet and a given alphabet. Note that the letter may get
     * associated to different letter code in the returned alphabet
     * @param alphabet the given alphabet
     * @return a new alphabet containing intersection between this alphabet and a given alphabet
     * @throws NullPointerException if {@code alphabet} is {@code null}
     */
    @Override
    public Alphabet<T> intersection(Alphabet<T> alphabet) {
        UniqueMap<Integer, T> targetAlphabetMap = Optional
                .ofNullable(alphabet)
                .orElseThrow(NullPointerException::new)
                .toMap();
        Alphabet<T> retAlphabet = new Alphabet<>();
        Stream.concat(alphabetMap.values().stream(), targetAlphabetMap.values().stream())
                .filter(letter -> alphabetMap.containsValue(letter) && targetAlphabetMap.containsValue(letter))
                .forEach(letter -> retAlphabet.add(letter));
        return retAlphabet;
    }

    /**
     * perform a mathematical union of such alphabet and a given alphabet. Note that the letter may get associated to
     * different letter code in the returned alphabet
     * @param alphabet the given alphabet
     * @return a new alphabet containing union between this alphabet and a given alphabet
     * @throws NullPointerException if {@code alphabet} is {@code null}
     */
    @Override
    public Alphabet<T> union(Alphabet<T> alphabet) {
        return new Alphabet<T>()
                .addAll(alphabetMap.values())
                .addAll(Optional.ofNullable(alphabet).orElseThrow(NullPointerException::new).toMap().values());
    }

    /**
     * perform a mathematical set subtraction of such alphabet and a given alphabet. Note that the letter may get
     * associated to different letter code in the returned alphabet
     * @param alphabet the given alphabet
     * @return a new alphabet containing the subtraction between this alphabet and a given alphabet
     * @throws NullPointerException if {@code alphabet} is {@code null}
     */
    @Override
    public Alphabet<T> subtraction(Alphabet<T> alphabet) {
        Alphabet<T> retAlphabet = new Alphabet<>();
        alphabetMap.values().stream()
                .filter(letter -> !Optional
                        .ofNullable(alphabet)
                        .orElseThrow(NullPointerException::new)
                        .toMap().values().contains(letter))
                .forEach(letter -> retAlphabet.add(letter));
        return retAlphabet;
    }

    @Override
    public String toString() {
        StringBuilder retStringBuffer = new StringBuilder().append('{');
        retStringBuffer.append("epsilon").append('=').append(epsilonTransitionCode).append(", ");
        retStringBuffer.append("sigma")  .append('=').append(sigmaTransitionCode);
        alphabetMap.forEach((code, letter) ->
                retStringBuffer.append(", ").append(letter.toString()).append('=').append(code)
        );
        return retStringBuffer.append('}').toString();
    }
}

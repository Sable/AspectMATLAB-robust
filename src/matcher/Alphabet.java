package matcher;

import utils.SetOperation;
import utils.UniqueMap;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Alphabet<T> implements SetOperation<Alphabet<T>> {
    private final Iterator<Integer> letterCodeAssigner = IntStream.iterate(2, x -> x + 1).iterator();
    private final UniqueMap<Integer, T> alphabetMap = new UniqueMap<>();
    private final int epsilonTransitionCode = 0;
    private final int sigmaTransitionCode = 1;

    public int getLetterCode(T letter) {
        if (letter == null) throw new NullPointerException();
        return alphabetMap.values().contains(letter)?alphabetMap.getInverse(letter):sigmaTransitionCode;
    }

    public T getLetter(int code) {
        return alphabetMap.keySet().contains(code)?alphabetMap.get(code):null;
    }

    public synchronized Alphabet<T> add(T letter) {
        if (letter == null) throw new NullPointerException();
        if (alphabetMap.values().contains(letter)) return this;
        alphabetMap.put(letterCodeAssigner.next(), letter);
        return this;
    }

    public Alphabet<T> addAll(Collection<T> letters) {
        Optional.ofNullable(letters).orElseThrow(NullPointerException::new)
                .forEach(letter -> add(Optional.ofNullable(letter).orElseThrow(IllegalArgumentException::new)));
        return this;
    }

    public UniqueMap<Integer, T> toMap() {
        UniqueMap<Integer, T> retMap = new UniqueMap<>();
        alphabetMap.forEach((key, value) -> retMap.put(key, value));
        return retMap;
    }

    @Override
    public Alphabet<T> intersection(Alphabet<T> alphabet) {
        Alphabet<T> retAlphabet = new Alphabet<>();
        Stream.concat(alphabetMap.values().stream(), alphabet.toMap().values().stream())
                .filter(letter -> alphabetMap.containsValue(letter) && alphabet.toMap().containsValue(letter))
                .forEach(letter -> retAlphabet.add(letter));
        return retAlphabet;
    }

    @Override
    public Alphabet<T> union(Alphabet<T> alphabet) {
        return new Alphabet<T>().addAll(alphabetMap.values()).addAll(alphabet.toMap().values());
    }

    @Override
    public Alphabet<T> substraction(Alphabet<T> alphabet) {
        Alphabet<T> retAlphabet = new Alphabet<>();
        alphabetMap.values().stream()
                .filter(letter -> !alphabet.toMap().values().contains(letter))
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

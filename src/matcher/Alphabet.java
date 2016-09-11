package matcher;

import utils.LiteralBuilder;
import utils.UniqueMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * an implementation of alphabet in the extend algorithm matching algorithm
 * @param <K> base class of symbol code
 * @param <V> base class of symbol
 */
public class Alphabet<K, V> {
    private final UniqueMap<K, V> alphabetMap = new UniqueMap<>();
    private final Iterator<K> keyGenerator;
    public final K epsilonTransitionCode;
    public final K sigmaTransitionCode;

    /**
     * construct a alphabet by giving a symbol code generator, an epsilon transition code, and a sigma transition
     * code. By contract, symbol code generator should always generate a new code other than epsilon and sigma
     * transition code, when {@link Iterator#next()} is invoked
     * @param keyGenerator symbol code generator
     * @param epsilonTransitionCode epsilon transition code
     * @param sigmaTransitionCode sigma transition code
     * @throws NullPointerException if any of {@code keyGenerator}, {@code epsilonTransitionCode} or
     *                              {@code sigmaTransitionCode} is {@code null}.
     */
    public Alphabet(Iterator<K> keyGenerator, K epsilonTransitionCode, K sigmaTransitionCode) {
        this.keyGenerator = Optional.ofNullable(keyGenerator).orElseThrow(NullPointerException::new);
        this.epsilonTransitionCode = Optional.ofNullable(epsilonTransitionCode).orElseThrow(NullPointerException::new);
        this.sigmaTransitionCode = Optional.ofNullable(sigmaTransitionCode).orElseThrow(NullPointerException::new);
    }

    /**
     * add a symbol to the alphabet, the alphabet will assign a new code to the symbol, if the symbol does not
     * contained in the alphabet.
     * @param symbol symbol to be added
     * @return assigned code to such symbol
     * @throws NullPointerException if {@code symbol} is {@code null}
     * @throws IllegalStateException if duplicate symbol code is provided by the symbol code generator
     */
    public K addSymbol(V symbol) {
        if (symbol == null) throw new NullPointerException();
        if (alphabetMap.values().contains(symbol)) return alphabetMap.getInverse(symbol);
        K associatedCode = keyGenerator.next();
        if (alphabetMap.keySet().contains(associatedCode)) throw new IllegalStateException();
        alphabetMap.put(associatedCode, symbol);
        return associatedCode;
    }

    /**
     * get the symbol by providing the code of such symbol. If the given code is epsilon transition code or sigma
     * transition code, {@code null} will be provided.
     * @param code provided symbol code
     * @return symbol associate with such code. {@code null} if {@code code} is epsilon transition code or sigma
     * transition code.
     * @throws NullPointerException if {@code code} is {@code null}
     */
    public V getSymbolByCode(K code) {
        if (code == null) throw new NullPointerException();
        if (epsilonTransitionCode.equals(code) || sigmaTransitionCode.equals(code)) return null;
        if (!alphabetMap.keySet().contains(code)) throw new IllegalArgumentException();
        return alphabetMap.get(code);
    }

    /**
     * get the symbol code by providing the symbol associated to such symbol. If the given symbol is {@code null},
     * epsilon transition code is returned. If the given symbol is not contained in the alphabet, sigma transition
     * code will be returned.
     * @param symbol given symbol
     * @return symbol code associated to the given symbol
     */
    public K getCodeBySymbol(V symbol) {
        if (symbol == null) return epsilonTransitionCode;
        if (!alphabetMap.values().contains(symbol)) return sigmaTransitionCode;
        return alphabetMap.getInverse(symbol);
    }

    /**
     * convert the alphabet in to a equivalent map. This map does not contain epsilon transition code and sigma
     * transition code.
     * @return map constructed from the alphabet
     */
    public Map<K, V> toMap() {
        return alphabetMap.clone();
    }

    @Override
    public String toString() {
        Set<String> contentSet = new LiteralBuilder<String>()
                .putAll(alphabetMap.entrySet().stream()
                        .map(entry -> String.format("%s=%s", entry.getKey().toString(), entry.getValue().toString()))
                        .collect(Collectors.toSet())
                )
                .put(String.format("%s=epsilon", epsilonTransitionCode.toString()))
                .put(String.format("%s=sigma",sigmaTransitionCode.toString()))
                .asSet();
        return contentSet.toString();
    }
}

package matcher.nfa;

import matcher.Alphabet;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * an implementation of NFA state in the extended algorithm matching algorithm
 * @param <T> base class of symbol of state transition
 */
public class NFAState<T> {
    private final Object stateName;
    private final Alphabet alphabet;
    private final Map<Object, Set<NFAState<T>>> stateTransferMap = new HashMap<>();
    private final NFA<T> parentNFA;

    /**
     * construct a new NFA state by a given state name, alphabet and parent NFA
     * @param stateName state name of this NFA state
     * @param parentNFA parent NFA of this NFA state
     * @throws NullPointerException if either {@code stateName} or {@code parentNFA} is {@code null}
     */
    @Deprecated
    public NFAState(Object stateName, NFA<T> parentNFA) {
        this.stateName = Optional.ofNullable(stateName).orElseThrow(NullPointerException::new);
        this.alphabet = Optional.ofNullable(parentNFA).orElseThrow(NullPointerException::new).getAlphabet();
        this.parentNFA = Optional.ofNullable(parentNFA).orElseThrow(NullPointerException::new);

        stateTransferMap.put(alphabet.epsilonTransitionCode, new HashSet<>());
        stateTransferMap.put(alphabet.sigmaTransitionCode, new HashSet<>());
        alphabet.toMap().keySet().forEach(symbolCode -> stateTransferMap.put(symbolCode, new HashSet<>()));
    }

    /**
     * add a new state transfer to {@code nfaState} of {@code symbol}
     * @param symbol symbol of alphabet
     * @param nfaState target NFA state
     * @return an reference to current NFA state
     * @throws NullPointerException if either {@code symbol} or {@code nfaState} is {@code null}
     * @throws IllegalArgumentException if {@code symbol} has epsilon or sigma transition code in the alphabet, in
     *                                  this case, use {@link NFAState#addEpsilonStateTransfer(NFAState)} and
     *                                  {@link NFAState#addSigmaStateTransfer(NFAState)} in stead
     * @throws IllegalArgumentException if {@code nfaState} does not belong to the same NFA with current NFA state
     */
    public NFAState<T> addStateTransfer(T symbol, NFAState<T> nfaState) {
        Object symbolCode = alphabet.getCodeBySymbol(Optional
                .ofNullable(symbol)
                .orElseThrow(NullPointerException::new)
        );
        if (alphabet.sigmaTransitionCode.equals(symbolCode)) throw new IllegalArgumentException();
        if (alphabet.epsilonTransitionCode.equals(symbolCode)) throw new IllegalArgumentException();
        if (!parentNFA.hasState(nfaState)) throw new IllegalArgumentException();
        stateTransferMap.get(symbolCode).add(Optional.ofNullable(nfaState).orElseThrow(NullPointerException::new));
        return this;
    }

    /**
     * add a transfer to {@code nfaState} via epsilon state transition
     * @param nfaState target NFA state
     * @return an reference to current NFA state
     * @throws NullPointerException if {@code nfaState} is {@code null}.
     * @throws IllegalArgumentException if {@code nfaState} does not belong to the same NFA with current state
     */
    public NFAState<T> addEpsilonStateTransfer(NFAState<T> nfaState) {
        if (nfaState == null) throw new NullPointerException();
        if (!parentNFA.hasState(nfaState)) throw new IllegalArgumentException();
        stateTransferMap.get(alphabet.epsilonTransitionCode).add(nfaState);
        return this;
    }

    /**
     * add a transfer to {@code nfaState} via sigma state transition
     * @param nfaState target NFA state
     * @return an reference to current NFA state
     * @throws NullPointerException if {@code nfaState} is {@code null}.
     * @throws IllegalArgumentException if {@code nfaState} does not belong to the same NFA with current state
     */
    public NFAState<T> addSigmaStateTransfer(NFAState<T> nfaState) {
        if (nfaState == null) throw new NullPointerException();
        if (!parentNFA.hasState(nfaState)) throw new IllegalArgumentException();
        stateTransferMap.get(alphabet.sigmaTransitionCode).add(nfaState);
        return this;
    }

    /**
     * @return current state name
     */
    public Object getStateName() {
        return stateName;
    }

    /**
     * get epsilon closure set of the current NFA state (i.e. set of NFA state that are reachable from current NFA
     * state with only epsilon transition)
     * @return epsilon closure set of NFA state
     */
    public Set<NFAState<T>> getEpsilonClosureSet() {
        Set<NFAState<T>> retSet = new HashSet<>();
        new Consumer<NFAState<T>>() {
            @Override
            public void accept(NFAState<T> state) {
                if (retSet.contains(state)) return;
                retSet.add(state);
                state.stateTransferMap.get(alphabet.epsilonTransitionCode).forEach(this);
            }
        }.accept(this);
        return retSet;
    }

    /**
     * get sigma closure set of the current NFA state (i.e. set of NFA state that are reachable from current NFA
     * state with only epsilon transitions or one sigma transition)
     * @return sigma closure set of NFA state
     */
    public Set<NFAState<T>> getSigmaClosureSet() {
        Set<NFAState<T>> retSet = new HashSet<>();
        getEpsilonClosureSet()
                .forEach(state -> retSet.addAll(state.stateTransferMap.get(alphabet.sigmaTransitionCode)));
        return retSet;
    }

    /**
     * get state closure set of the current NFA state (i.e. set of NFA state that are reachable from current NFA
     * state with only epsilon transition, and one given symbol). If {@code symbol} is null, this method will simply
     * return the same result as {@link NFAState#getEpsilonClosureSet()}, if {@code symbol} is not contained in the
     * alphabet, this method will return the same result as {@link NFAState#getSigmaClosureSet()}.
     * @param symbol
     * @return
     */
    public Set<NFAState<T>> getStateClosureSet(T symbol) {
        if (symbol == null) return getEpsilonClosureSet();
        Object symbolCode = alphabet.getCodeBySymbol(symbol);
        Set<NFAState<T>> stateClosureSet = new HashSet<>();
        getEpsilonClosureSet().forEach(state -> stateClosureSet.addAll(state.stateTransferMap.get(symbolCode)));
        return stateClosureSet;
    }

    @Override
    public String toString() {
        String stateName = getStateName().toString();
        String stateContent = stateTransferMap.entrySet().stream()
                .map(entry -> entry.getValue().isEmpty()?"":
                        String.format("%s=%s",
                                entry.getKey().equals(alphabet.epsilonTransitionCode)?"epsilon":
                                        entry.getKey().equals(alphabet.sigmaTransitionCode)?"sigma":
                                                alphabet.getSymbolByCode(entry.getKey()).toString(),
                                entry.getValue().stream()
                                        .map(state -> state.getStateName().toString())
                                        .collect(Collectors.toList()).toString()))
                .filter(printedString -> !printedString.isEmpty())
                .collect(Collectors.toList()).toString();
        return String.format("<%s, %s>", stateName, stateContent);
    }
}

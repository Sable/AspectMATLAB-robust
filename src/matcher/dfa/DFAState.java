package matcher.dfa;

import matcher.Alphabet;
import matcher.nfa.NFA;
import matcher.nfa.NFAState;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * an implementation of DFA state in the extended algorithm matching algorithm
 * @param <T> base class of symbol of state transition
 */
public class DFAState<T> {
    private final Object stateName;
    private final Set<NFAState<T>> correspondNFASubset;
    private final Map<Object, DFAState<T>> stateTransferMap = new HashMap<>();
    private final DFA<T> parentDFA;

    private final Alphabet alphabet;
    private final NFA<T> buildFrom;

    /**
     * construct from a given state name, a equivalent NFA state subset, and a parent DFA
     * @param stateName state name
     * @param correspondNFASubset corresponding NFA state set (from subset construction method)
     * @param parentDFA parent DFA
     * @throws NullPointerException if any of {@code stateName}, {@code correspondNFASubset} or {@code parentDFA} is
     *                              {@code null}
     */
    @Deprecated
    public DFAState(Object stateName, Set<NFAState<T>> correspondNFASubset, DFA<T> parentDFA) {
        this.stateName = Optional.ofNullable(stateName).orElseThrow(NullPointerException::new);
        this.correspondNFASubset = Optional.ofNullable(correspondNFASubset).orElseThrow(NullPointerException::new);
        this.parentDFA = Optional.ofNullable(parentDFA).orElseThrow(NullPointerException::new);
        alphabet = Optional.ofNullable(parentDFA).orElseThrow(NullPointerException::new).getAlphabet();
        buildFrom = Optional.ofNullable(parentDFA).orElseThrow(NullPointerException::new).getBuildFrom();
    }

    /**
     * determine if current DFA contains the same NFA subset as a given DFA state. If the given DFA state belong to a
     * different DFA other than the current DFA state's parent DFA, this method will always return {@code false}.
     * @param dfaState given DFA state
     * @return if the given DFA represent the same NFA subset return {@code true}, otherwise return {@code false}.
     */
    public boolean isEquivalent(DFAState<T> dfaState) {
        if (dfaState == null) return false;
        if (!parentDFA.equals(dfaState.parentDFA)) return false;
        return Stream.concat(correspondNFASubset.stream(), dfaState.correspondNFASubset.stream())
                .map(node -> correspondNFASubset.contains(node) && dfaState.correspondNFASubset.contains(node))
                .noneMatch(result -> !result);
    }

    /**
     * determine if current DFA contains the same NFA subset as the given set.
     * @param nfaStates set of NFA states to compare
     * @return if the given set of NFA state equal to the subset NFA state represented by the current DFA state,
     *         return {@code true}, otherwise return {@code false}.
     * @throws NullPointerException if {@code nfaState} is {@code null}
     */
    public boolean isEquivalent(Set<NFAState<T>> nfaStates) {
        return Stream.concat(
                correspondNFASubset.stream(),
                Optional.ofNullable(nfaStates).orElseThrow(NullPointerException::new).stream())
                .map(state -> correspondNFASubset.contains(state) && nfaStates.contains(state))
                .noneMatch(result -> !result);
    }

    /**
     * set the state transfer for the current DFA, by given a symbol and target DFA state
     * @param symbol transfer symbol
     * @param dfaState target DFA state
     * @return an reference to the current DFA state
     * @throws NullPointerException if either {@code symbol} or {@code dfaState} is {@code null}
     * @throws IllegalArgumentException if {@code symbol} has sigma transition in the alphabet
     * @throws IllegalArgumentException if target DFA state is not belong to the same DFA as the current DFA state
     */
    public DFAState<T> setStateTransfer(T symbol, DFAState<T> dfaState) {
        if (symbol == null || dfaState == null) throw new NullPointerException();
        if (alphabet.getCodeBySymbol(symbol).equals(alphabet.sigmaTransitionCode)) throw new IllegalArgumentException();
        if (!parentDFA.hasState(dfaState)) throw new IllegalArgumentException();
        stateTransferMap.put(alphabet.getCodeBySymbol(symbol), dfaState);
        return this;
    }

    /**
     * set the state transfer for sigma transition, by given a target DFA state
     * @param dfaState target DFA state
     * @return an reference to the current DFA state
     * @throws NullPointerException if {@code dfaState} is {@code null}
     * @throws IllegalArgumentException if target DFA state is not belong to the same DFA as the current DFA state
     */
    public DFAState<T> setSigmaTransfer(DFAState<T> dfaState) {
        if (dfaState == null) throw new NullPointerException();
        if (!parentDFA.hasState(dfaState)) throw new IllegalArgumentException();
        stateTransferMap.put(alphabet.sigmaTransitionCode, dfaState);
        return this;
    }

    /**
     * @return the set of NFA states represent by the current DFA state
     */
    public Set<NFAState<T>> getCorrespondNFASubset() {
        Set<NFAState<T>> retSet = new HashSet<>();
        retSet.addAll(correspondNFASubset);
        return retSet;
    }

    /**
     * @return return the state name of current DFA state
     */
    public Object getStateName() {
        return stateName;
    }

    /**
     * get the target state from current state via a given symbol in alphabet
     * @param symbol the given symbol
     * @return the target state
     * @throws NullPointerException if {@code symbol} is {@code null}
     * @throws IllegalArgumentException if {@code symbol} does not have any target state
     */
    public DFAState<T> getStateTransfer(T symbol) {
        Object symbolCode = alphabet.getCodeBySymbol(symbol);
        return Optional.ofNullable(
                this.stateTransferMap.get(Optional.ofNullable(symbolCode).orElseThrow(NullPointerException::new))
        ).orElseThrow(IllegalArgumentException::new);
    }


    /**
     * get the sigma transition state form the current node, if current state do not have a sigma transition state,
     * it will return {@code null}
     * @return the target state
     */
    public DFAState<T> getSigmaTransfer() {
        return this.stateTransferMap.get(alphabet.sigmaTransitionCode);
    }

    @Override
    public String toString() {
        String nameToString = stateName.toString();
        String reachableSetToString = stateTransferMap.entrySet().stream()
                .map(entry -> String.format("%s=%s",
                        entry.getKey().equals(alphabet.sigmaTransitionCode)?"sigma":
                                alphabet.getSymbolByCode(entry.getKey()).toString(),
                        entry.getValue().stateName.toString()))
                .collect(Collectors.toSet())
                .toString();
        return String.format("<%s, %s>", nameToString, reachableSetToString);
    }
}

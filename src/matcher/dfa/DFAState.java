package matcher.dfa;

import matcher.Alphabet;
import matcher.nfa.NFA;
import matcher.nfa.NFAState;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DFAState<T> {
    private final Object stateName;
    private final Set<NFAState<T>> correspondNFASubset;
    private final Map<Object, DFAState<T>> stateTransferMap = new HashMap<>();
    private final DFA<T> parentDFA;

    private final Alphabet alphabet;
    private final NFA<T> buildFrom;

    @Deprecated
    public DFAState(Object stateName, Set<NFAState<T>> correspondNFASubset, DFA<T> parentDFA) {
        this.stateName = Optional.ofNullable(stateName).orElseThrow(NullPointerException::new);
        this.correspondNFASubset = Optional.ofNullable(correspondNFASubset).orElseThrow(NullPointerException::new);
        this.parentDFA = Optional.ofNullable(parentDFA).orElseThrow(NullPointerException::new);
        alphabet = Optional.ofNullable(parentDFA).orElseThrow(NullPointerException::new).getAlphabet();
        buildFrom = Optional.ofNullable(parentDFA).orElseThrow(NullPointerException::new).getBuildFrom();
    }


    public boolean isEquvalent(DFAState<T> dfaState) {
        if (dfaState == null) return false;
        if (!parentDFA.equals(dfaState.parentDFA)) return false;
        return Stream.concat(correspondNFASubset.stream(), dfaState.correspondNFASubset.stream())
                .map(node -> correspondNFASubset.contains(node) && dfaState.correspondNFASubset.contains(node))
                .noneMatch(result -> !result);
    }

    public boolean isEquvalent(Set<NFAState<T>> nfaStates) {
        return Stream.concat(
                correspondNFASubset.stream(),
                Optional.ofNullable(nfaStates).orElseThrow(NullPointerException::new).stream())
                .map(state -> correspondNFASubset.contains(state) && nfaStates.contains(state))
                .noneMatch(result -> !result);
    }

    public DFAState<T> setStateTransfer(T symbol, DFAState<T> dfaState) {
        if (symbol == null || dfaState == null) throw new NullPointerException();
        if (alphabet.getCodeBySymbol(symbol).equals(alphabet.sigmaTransitionCode)) throw new IllegalArgumentException();
        if (!parentDFA.hasState(dfaState)) throw new IllegalArgumentException();
        stateTransferMap.put(alphabet.getCodeBySymbol(symbol), dfaState);
        return this;
    }

    public DFAState<T> setSigmaTransfer(DFAState<T> dfaState) {
        stateTransferMap.put(alphabet.sigmaTransitionCode, Optional
                .ofNullable(dfaState)
                .orElseThrow(NullPointerException::new)
        );
        return this;
    }

    public Set<NFAState<T>> getCorrespondNFASubset() {
        Set<NFAState<T>> retSet = new HashSet<>();
        retSet.addAll(correspondNFASubset);
        return retSet;
    }

    public Object getStateName() {
        return stateName;
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

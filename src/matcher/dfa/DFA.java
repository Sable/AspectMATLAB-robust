package matcher.dfa;

import matcher.Alphabet;
import matcher.nfa.NFA;
import matcher.nfa.NFAState;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * an implementation of DFA in the extended algorithm matching algorithm
 * @param <T> base class of symbol of state transition
 */
public class DFA<T> {
    private final Alphabet<?, T> alphabet;
    private final NFA<T> buildFrom;
    private final Set<DFAState<T>> stateSet = new HashSet<>();
    private final Iterator dfaNameGenerator;

    private DFAState<T> startingState = null;
    private Set<DFAState<T>> acceptingState = new HashSet<>();

    /**
     * construct DFA from a given NFA and DFA state name generator
     * @param nfa NFA to built from
     * @param dfaNameGenerator name generator to generate name for DFA states. By Contract, {@code dfaNameGenerator}
     *                         should generate names with none pair of names are equal.
     * @throws NullPointerException if {@code nfa} or {@code dfaNameGenerator} is {@code null}
     * @throws IllegalArgumentException if {@code nfa} is not complete
     */
    public DFA(NFA<T> nfa, Iterator<?> dfaNameGenerator) {
        if (nfa == null) throw new NullPointerException();
        if (!nfa.isComplete()) throw new IllegalArgumentException();
        this.alphabet = nfa.getAlphabet();
        this.buildFrom = nfa;
        this.dfaNameGenerator = Optional.ofNullable(dfaNameGenerator).orElseThrow(NullPointerException::new);
        constructDFAFromNFA();
    }

    /**
     * construct DFA from a given NFA and use trivial name generator {@code IntStream.iterate(1, x -> x + 1).iterator()}
     * @param nfa NFA to built from
     * @throws NullPointerException if {@code nfa} is {@code null}
     * @throws IllegalArgumentException if {@code nfa} is not complete
     */
    public DFA(NFA<T> nfa) {
        if (nfa == null) throw new NullPointerException();
        if (!nfa.isComplete()) throw new IllegalArgumentException();
        this.alphabet = nfa.getAlphabet();
        this.buildFrom = nfa;
        this.dfaNameGenerator = IntStream.iterate(1, x -> x + 1).iterator();
        constructDFAFromNFA();
    }

    /**
     * a helper method to construct DFA from NFA using subset construction method
     */
    private void constructDFAFromNFA() {
        Consumer<DFAState<T>> recCollector = new Consumer<DFAState<T>>() {
            @Override
            public void accept(DFAState<T> dfaState) {
                for (T symbol : alphabet.toMap().values()) {
                    Set<NFAState<T>> reachableSet = new HashSet<>();
                    dfaState.getCorrespondNFASubset()
                            .forEach(state -> reachableSet.addAll(state.getStateClosureSet(symbol)));
                    DFAState<T> targetDFAState = stateSet.stream()
                            .filter(state -> state.isEquivalent(reachableSet))
                            .findAny()
                            .orElseGet(() -> newState(reachableSet));
                    if (!stateSet.contains(targetDFAState)) {
                        stateSet.add(targetDFAState);
                        this.accept(targetDFAState);
                    }
                    dfaState.setStateTransfer(symbol, targetDFAState);
                }
                Set<NFAState<T>> sigmaReachableSet = new HashSet<>();
                dfaState.getCorrespondNFASubset()
                        .forEach(state -> sigmaReachableSet.addAll(state.getSigmaClosureSet()));
                DFAState<T> targetSigmaDFAState = stateSet.stream()
                        .filter(state -> state.isEquivalent(sigmaReachableSet))
                        .findAny()
                        .orElseGet(() -> newState(sigmaReachableSet));
                if (!stateSet.contains(targetSigmaDFAState)) {
                    stateSet.add(targetSigmaDFAState);
                    this.accept(targetSigmaDFAState);
                }
                dfaState.setSigmaTransfer(targetSigmaDFAState);
            }
        };
        DFAState<T> startState = newState(buildFrom.getStartingState().getEpsilonClosureSet());
        stateSet.add(startState);
        startingState = startState;
        recCollector.accept(startState);
        buildFrom.getAcceptingState().forEach(nfaState ->
            stateSet.forEach(dfaState -> {
                if (dfaState.getCorrespondNFASubset().contains(nfaState)) acceptingState.add(dfaState);
            })
        );
    }

    /**
     * create a new state in DFA
     * @param correspondingNFASet NFA subset get from the subset construction
     * @return an reference to the newly constructed DFA state
     */
    @SuppressWarnings("deprecation")
    public DFAState<T> newState(Set<NFAState<T>> correspondingNFASet) {
        if (correspondingNFASet == null) throw new NullPointerException();
        if (correspondingNFASet.stream().anyMatch(state -> !buildFrom.hasState(state))) {
            throw new IllegalArgumentException();
        }
        DFAState<T> newState = new DFAState<T>(dfaNameGenerator.next(), correspondingNFASet, this);
        return newState;
    }

    /**
     * @return alphabet corresponding to this DFA
     */
    public Alphabet<?, T> getAlphabet() {
        return alphabet;
    }

    /**
     * @return the base NFA
     */
    public NFA<T> getBuildFrom() {
        return buildFrom;
    }

    /**
     * determine whether a DFA state is belong to current DFA
     * @param state state to determine
     * @return if {@code state} is within current DFA return {@code true}, otherwise return {@code false}.
     */
    public boolean hasState(DFAState<T> state) {
        if (state == null) return false;
        return stateSet.contains(state);
    }

    /**
     * @return set of states within the DFA
     */
    public Set<DFAState<T>> getStateSet() {
        return this.stateSet;
    }

    /**
     * @return set of accepting states within the DFA
     */
    public Set<DFAState<T>> getAcceptingStateSet() {
        Set<DFAState<T>> returnSet = new HashSet<>();
        acceptingState.forEach(returnSet::add);
        return returnSet;
    }

    /**
     * @return the starting state of the current DFA, if it is not set, {@code null} will be returned.
     */
    public DFAState<T> getStartingState() {
        return startingState;
    }

    @Override
    public String toString() {
        String statesToString = stateSet.toString();
        String startStateToString = startingState.getStateName().toString();
        String acceptStatesToString = acceptingState.stream()
                .map(state -> state.getStateName().toString())
                .collect(Collectors.toSet()).toString();
        return String.format("<%s, %s, %s>", statesToString, startStateToString, acceptStatesToString);
    }
}

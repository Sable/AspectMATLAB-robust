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

public class DFA<T> {
    private final Alphabet<?, T> alphabet;
    private final NFA<T> buildFrom;
    private final Set<DFAState<T>> stateSet = new HashSet<>();
    private final Iterator dfaNameGenerator;

    private DFAState<T> startingState = null;
    private Set<DFAState<T>> accpetingState = new HashSet<>();

    public DFA(NFA<T> nfa, Iterator<?> dfaNameGenerator) {
        if (nfa == null) throw new NullPointerException();
        this.alphabet = nfa.getAlphabet();
        this.buildFrom = nfa;
        this.dfaNameGenerator = Optional.ofNullable(dfaNameGenerator).orElseThrow(NullPointerException::new);
        constructDFAFromNFA();
    }

    public DFA(NFA<T> nfa) {
        if (nfa == null) throw new NullPointerException();
        this.alphabet = nfa.getAlphabet();
        this.buildFrom = nfa;
        this.dfaNameGenerator = IntStream.iterate(1, x -> x + 1).iterator();
        constructDFAFromNFA();
    }

    private void constructDFAFromNFA() {
        Consumer<DFAState<T>> recCollector = new Consumer<DFAState<T>>() {
            @Override
            public void accept(DFAState<T> dfaState) {
                for (T symbol : alphabet.toMap().values()) {
                    Set<NFAState<T>> reachableSet = new HashSet<>();
                    dfaState.getCorrespondNFASubset()
                            .forEach(state -> reachableSet.addAll(state.getStateClosureSet(symbol)));
                    DFAState<T> targetDFAState = stateSet.stream()
                            .filter(state -> state.isEquvalent(reachableSet))
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
                        .filter(state -> state.isEquvalent(sigmaReachableSet))
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
                if (dfaState.getCorrespondNFASubset().contains(nfaState)) accpetingState.add(dfaState);
            })
        );
    }

    @SuppressWarnings("deprecation")
    public DFAState<T> newState(Set<NFAState<T>> correspondingNFASet) {
        if (correspondingNFASet == null) throw new NullPointerException();
        if (correspondingNFASet.stream().anyMatch(state -> !buildFrom.hasState(state))) {
            throw new IllegalArgumentException();
        }
        DFAState<T> newState = new DFAState<T>(dfaNameGenerator.next(), correspondingNFASet, this);
        return newState;
    }

    public Alphabet<?, T> getAlphabet() {
        return alphabet;
    }

    public NFA<T> getBuildFrom() {
        return buildFrom;
    }

    public boolean hasState(DFAState<T> state) {
        if (state == null) return false;
        return stateSet.contains(state);
    }

    @Override
    public String toString() {
        String statesToString = stateSet.toString();
        String startStateToString = startingState.getStateName().toString();
        String acceptStatesToString = accpetingState.stream()
                .map(state -> state.getStateName().toString())
                .collect(Collectors.toSet()).toString();
        return String.format("<%s, %s, %s>", statesToString, startStateToString, acceptStatesToString);
    }
}

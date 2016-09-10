package matcher.nfa;

import matcher.Alphabet;
import utils.MergableHashSet;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * implementation of NFA state in extend argument matching algorithm
 * @param <T> NFA state base class
 */
public class NFAState<T> {
    private final Map<Integer, MergableHashSet<NFAState<T>>> stateTransferMap = new HashMap<>();
    private final Object stateName;
    private final Alphabet<T> alphabet;

    /**
     * construct a new NFA state.
     * @param stateNameGenerator state name generator, the new state will have name of {@code stateNameGenerator.next()}
     * @param alphabet alphabet associated to the NFA state
     * @throws NullPointerException if either {@code stateNameGenerator} or {@code alphabet} is {@code null}
     * @throws IllegalStateException if {@code stateNameGenerator} cannot generate more name
     */
    @Deprecated
    public NFAState(Iterator<?> stateNameGenerator, Alphabet<T> alphabet) {
        if (!stateNameGenerator.hasNext()) throw new IllegalStateException();
        this.stateName = Optional.ofNullable(stateNameGenerator).orElseThrow(NullPointerException::new).next();
        this.alphabet = Optional.ofNullable(alphabet).orElseThrow(NullPointerException::new);
    }

    /**
     * add a new state transfer to such state (i.e. edges in NFA), if letter is {@code null} this will be regarded as
     * a epsilon transition.
     * @param letter letter
     * @param targetState target NFA state associate with such letter
     * @return an reference to the current NFA state
     * @throws NullPointerException if {@code targetState} is {@code null}
     * @throws IllegalArgumentException if {@code letter} is not contained in the alphabet
     */
    public NFAState<T> addStateTransfer(T letter, NFAState<T> targetState) {
        int stateTransferCode = letter == null? Alphabet.epsilonTransitionCode: alphabet.getLetterCode(letter);
        if (stateTransferCode == Alphabet.sigmaTransitionCode) throw new IllegalArgumentException();
        MergableHashSet<NFAState<T>> reachableNodeSet = Optional
                .ofNullable(stateTransferMap.get(stateTransferCode))
                .orElseGet(MergableHashSet::new);
        reachableNodeSet.add(Optional.ofNullable(targetState).orElseThrow(NullPointerException::new));
        stateTransferMap.put(stateTransferCode, reachableNodeSet);
        return this;
    }

    /**
     * add a new epsilon transition to such state (i.e. an edge with epsilon transition in NFA)
     * @param targetState target NFA state associate with such epsilon transition
     * @return an reference to the current NFA state
     * @throws NullPointerException if {@code targetState} is {@code null}
     */
    public NFAState<T> addEpsilonTransfer(NFAState<T> targetState) {
        MergableHashSet<NFAState<T>> reachableNodeSet = Optional
                .ofNullable(stateTransferMap.get(Alphabet.epsilonTransitionCode))
                .orElseGet(MergableHashSet::new);
        reachableNodeSet.add(Optional.ofNullable(targetState).orElseThrow(NullPointerException::new));
        stateTransferMap.put(Alphabet.epsilonTransitionCode, reachableNodeSet);
        return this;
    }

    /**
     * add a sigma transition to such state (i.e. an edge with sigma transition in NFA)
     * @param targetState target NFA state associate with such sigma transition
     * @return an reference to the current NFA state
     * @throws NullPointerException if {@code targetState} is {@code null}
     */
    public NFAState<T> addSigmaTransfer(NFAState<T> targetState) {
        MergableHashSet<NFAState<T>> reachableNodeSet = Optional
                .ofNullable(stateTransferMap.get(Alphabet.sigmaTransitionCode))
                .orElseGet(MergableHashSet::new);
        reachableNodeSet.add(Optional.ofNullable(targetState).orElseThrow(NullPointerException::new));
        stateTransferMap.put(Alphabet.sigmaTransitionCode, reachableNodeSet);
        return this;
    }

    /**
     * calculate the epsilon closure set of this NFA state (i.e. set of states which are reachable without consuming any
     * input tokens)
     * @return epsilon closure set of this NFA state
     */
    public Set<NFAState<T>> getEpsilonClosureSet() {
        MergableHashSet<NFAState<T>> resultSet = new MergableHashSet<>();
        Consumer<NFAState<T>> recCollector = new Consumer<NFAState<T>>() {
            @Override
            public void accept(NFAState<T> currentNFAState) {
                if (resultSet.contains(currentNFAState)) return;
                resultSet.add(currentNFAState);
                currentNFAState.stateTransferMap.get(Alphabet.epsilonTransitionCode).forEach(this);
            }
        };
        recCollector.accept(this);
        return resultSet;
    }

    /**
     * calculate the sigma closure set of this NFA state (i.e. set of states which are reachable only consuming on
     * sigma token)
     * @return sigma closure set of this NFA state
     */
    public Set<NFAState<T>> getSigmaColsureSet() {
        Set<NFAState<T>> epsilonClosureSet = getEpsilonClosureSet();
        MergableHashSet<NFAState<T>> resultSet = new MergableHashSet<>();
        epsilonClosureSet.stream()
                .map(node -> Optional
                    .ofNullable(node.stateTransferMap.get(Alphabet.sigmaTransitionCode))
                    .orElseGet(MergableHashSet::new))
                .forEach(resultSet::addAll);
        return resultSet;
    }

    /**
     * calculate the set of NFA nodes closure under consuming a given letter. If {@code letter} is {@code null}, this
     * method will return the epsilon closure set of this NFA state
     * @param letter the given letter
     * @return set of NFA nodes close under {@code letter}
     */
    public Set<NFAState<T>> getLetterColsureSet(T letter) {
        if (letter == null) return getEpsilonClosureSet();
        int stateTransferCode = alphabet.getLetterCode(letter);
        Set<NFAState<T>> epsilonClosureSet = new MergableHashSet<>();
        MergableHashSet<NFAState<T>> resultSet = new MergableHashSet<>();
        epsilonClosureSet.stream()
                .map(node -> Optional
                    .ofNullable(node.stateTransferMap.get(stateTransferCode))
                    .orElseGet(MergableHashSet::new))
                .forEach(resultSet::addAll);
        return resultSet;
    }

    /** @return the name of this state */
    public Object getStateName() {
        return stateName;
    }

    @Override
    public String toString() {
        Set<String> mapToString = new HashSet<>();
        for (Map.Entry<Integer, MergableHashSet<NFAState<T>>> entry : stateTransferMap.entrySet()) {
            String keyToString = (entry.getKey() == Alphabet.epsilonTransitionCode)? "epsilon":
                    (entry.getKey() == Alphabet.sigmaTransitionCode)?"sigma":
                            alphabet.getLetter(entry.getKey()).toString();
            String valueToString = String.join("=", entry.getValue().stream()
                    .map(node -> node.getStateName().toString())
                    .collect(Collectors.toSet())
            );
            mapToString.add(String.format("%s={%s}", keyToString, valueToString));
        }
        return String.format("<%s, [%s]>", stateName.toString(), String.join(", ", mapToString));
    }
}

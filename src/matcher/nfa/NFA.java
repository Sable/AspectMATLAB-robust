package matcher.nfa;

import matcher.Alphabet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * an implementation of NFA in the extended algorithm matching algorithm
 * @param <T> base class of symbol of state transition
 */
public class NFA<T> {
    private final Set<NFAState<T>> stateSet = new HashSet<>();
    private final Iterator<?> stateNameGenerator;
    private final Alphabet<?, T> alphabet;
    private NFAState<T> startingState;
    private Set<NFAState<T>> acceptingState = new HashSet<>();

    /**
     * construct form a given state name generator and a alphabet
     * @param stateNameGenerator state name generator
     * @param alphabet alphabet
     * @throws NullPointerException if either {@code stateNameGenerator} or {@code alphabet} is {@code null}
     */
    public NFA(Iterator<?> stateNameGenerator, Alphabet<?, T> alphabet) {
        this.stateNameGenerator = Optional.ofNullable(stateNameGenerator).orElseThrow(NullPointerException::new);
        this.alphabet = Optional.ofNullable(alphabet).orElseThrow(NullPointerException::new);
    }

    /**
     * construct from a given alphabet, and using default name generator <br>
     * <code>
     *     IntStream.iterate(1, x -&gt; x + 1).iterator()
     * </code>
     * @param alphabet alphabet
     * @throws NullPointerException if {@code alphabet} is {@code null}
     */
    public NFA(Alphabet<?, T> alphabet) {
        this.stateNameGenerator = IntStream.iterate(1, x -> x + 1).iterator();
        this.alphabet = Optional.ofNullable(alphabet).orElseThrow(NullPointerException::new);
    }

    /**
     * @return return current NFA alphabet
     */
    public Alphabet<?, T> getAlphabet() {
        return alphabet;
    }

    /**
     * @return return set containing all states of such NFA
     */
    public Set<NFAState<T>> getStateSet() {
        Set<NFAState<T>> retSet = new HashSet<>();
        stateSet.forEach(retSet::add);
        return retSet;
    }

    /**
     * determine if a state is within current NFA, if {@code state} is {@code null}, {@code false} will be returned
     * @param state state to test
     * @return {@code true} if {@code state} is within current NFA, otherwise, {@code false} will be returned
     */
    public boolean hasState(NFAState<T> state) {
        if (state == null) return false;
        return stateSet.contains(state);
    }

    /**
     * create a new state within current NFA
     * @return new state
     */
    @SuppressWarnings("deprecation")
    public NFAState<T> newState() {
        NFAState<T> newState = new NFAState<T>(stateNameGenerator.next(), this);
        stateSet.add(newState);
        return newState;
    }

    /**
     * set start start state of this NFA
     * @param startingState starting state
     * @throws NullPointerException if {@code startingState} is {@code null}
     * @throws IllegalArgumentException if this NFA does not contain {@code startingState}
     */
    public void setStartingState(NFAState<T> startingState) {
        if (startingState == null) throw new NullPointerException();
        if (!hasState(startingState)) throw new IllegalArgumentException();
        this.startingState = startingState;
    }

    /**
     * @return the start state of this NFA
     */
    public NFAState<T> getStartingState() {
        return startingState;
    }

    /**
     * @return the accepting state of this NFA
     */
    public Set<NFAState<T>> getAcceptingState() {
        Set<NFAState<T>> retSet = new HashSet<>();
        retSet.addAll(acceptingState);
        return retSet;
    }

    /**
     * add a accepting state to this NFA
     * @param acceptingState appending accept state
     * @throws NullPointerException if {@code acceptingState} is {@code null}
     * @throws IllegalArgumentException if this NFA does not contain {@code acceptingState}
     */
    public void addAcceptingState(NFAState<T> acceptingState) {
        if (acceptingState == null) throw new NullPointerException();
        if (!hasState(acceptingState)) throw new IllegalArgumentException();
        this.acceptingState.add(acceptingState);
    }

    @Override
    public String toString() {
        String statesToString = stateSet.toString();
        String startStateToString = startingState.getStateName().toString();
        String acceptStatesToString = acceptingState.stream()
                .map(node -> node.getStateName().toString())
                .collect(Collectors.toList()).toString();
        return String.format("<%s, %s, %s>", statesToString, startStateToString, acceptStatesToString);
    }
}

package matcher.nfa;

import matcher.Alphabet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * implementation of nondeterministic finite state automaton in extend argument matching algorithm
 * @param <T> NFA base class
 */
public class NFA<T> {
    private final Set<NFAState> nfaStates = new HashSet<>();
    private final Alphabet<T> alphabet;
    private final Iterator<?> nfaNameGenerator;

    private NFAState<T> initState;
    private Set<NFAState<T>> acceptStates = new HashSet<>();

    /**
     * construct NFA by a given NFA name generator and alphabet. By contract the {@code nfaNameGenerator} each
     * {@link Iterator#next()} call will return a new "name", i.e. no two result returned by {@link Iterator#next()}
     * satisfy {@code A.equals(B)}.
     * @param nfaNameGenerator NFA name generator
     * @param alphabet alphabet
     * @throws NullPointerException if either {@code nfaNameGenerator} or {@code alphabet} is {@code null}
     */
    public NFA(Iterator<?> nfaNameGenerator, Alphabet<T> alphabet) {
        this.alphabet = Optional.ofNullable(alphabet).orElseThrow(NullPointerException::new);
        this.nfaNameGenerator = Optional.ofNullable(nfaNameGenerator).orElseThrow(NullPointerException::new);
    }

    /**
     * construct by a given alphabet. This NFA will use {@code IntStream.iterate(1, x -> x + 1).iterator()} as a
     * default NFA name generator.
     * @param alphabet alphabet
     * @throws NullPointerException {@code alphabet} is {@code null}
     */
    public NFA(Alphabet<T> alphabet) {
        this.alphabet = Optional.ofNullable(alphabet).orElseThrow(NullPointerException::new);
        nfaNameGenerator = IntStream.iterate(1, x -> x + 1).iterator();
    }

    /**
     * set the start/init state of NFA
     * @param initState start state
     * @throws NullPointerException if {@code initState} is {@code null}
     */
    public void setInitState(NFAState<T> initState) {
        this.initState = Optional.ofNullable(initState).orElseThrow(NullPointerException::new);
    }

    /**
     * add a accept state to NFA
     * @param acceptState accept state
     * @return an reference to this NFA
     * @throws NullPointerException if {@code acceptState} is {@code null}
     */
    public NFA<T> addAcceptState(NFAState<T> acceptState) {
        if (acceptState == null) throw new NullPointerException();
        if (!nfaStates.contains(acceptState)) throw new IllegalArgumentException();
        acceptStates.add(acceptState);
        return this;
    }

    /**
     * create a new state within NFA
     * @return an reference to the new state
     * @throws IllegalStateException if duplicate name is created by the nfa name generator
     */
    @SuppressWarnings("deprecation")
    public NFAState<T> newState() {
        NFAState<T> newState = new NFAState<>(nfaNameGenerator, alphabet);
        if (nfaStates.stream().map(state -> state.getStateName()).collect(Collectors.toSet())
                .contains(newState.getStateName())) {
            throw new IllegalStateException();
        }
        nfaStates.add(newState);
        return newState;
    }

    /**
     * validate whether this NFA is complete
     * @return {@code true} if it is complete, otherwise return {@code false}
     */
    public boolean isComplete() {
        if (nfaStates.isEmpty()) return false;
        if (initState == null) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s>",
                nfaStates.toString(),
                acceptStates.stream().map(entry -> entry.getStateName()).collect(Collectors.toSet()).toString()
        );
    }
}

package matcher.nfa;

import matcher.Alphabet;

import java.util.Optional;

/** implementation of several NFA building subroutines. */
public class NFABuilder {
    /**
     * construct NFA from a given shape signature list (list consists of integer numbers, star wildcard[*] and
     * dots wildcard[..]) and a given alphabet.
     * @param signatures given signature list
     * @param alphabet given alphabet
     * @return constructed NFA
     * @throws NullPointerException if either {@code signatures} or {@code alphabet} is {@code null}
     * @throws IllegalArgumentException if shape signature is invalid (consists tokens other than non-negative
     *                                  integer, star wildcard[*] or star wildcard[..])
     */
    public static NFA<Integer> fromShapeSignatureList(Iterable<String> signatures, Alphabet<Integer> alphabet) {
        if (alphabet == null) throw new NullPointerException();
        NFA<Integer> returnNFA = new NFA<>(alphabet);
        NFAState<Integer> previousState = returnNFA.newState();
        returnNFA.setInitState(previousState);
        for (String signature : Optional.ofNullable(signatures).orElseThrow(NullPointerException::new)) {
            if (signature == null)  throw new IllegalArgumentException();
            if ("..".equals(signature)) {
                NFAState<Integer> state0 = previousState;
                NFAState<Integer> state1 = returnNFA.newState();
                NFAState<Integer> state2 = returnNFA.newState();
                NFAState<Integer> state3 = returnNFA.newState();

                state0.addEpsilonTransfer(state1);
                alphabet.toMap().values().forEach(letter -> state1.addStateTransfer(letter, state2));
                state1.addEpsilonTransfer(state2);
                state1.addSigmaTransfer(state2);
                state2.addEpsilonTransfer(state3);
                state3.addEpsilonTransfer(state0);

                previousState = state3;
                continue;
            }
            if ("*".equals(signature)) {
                NFAState<Integer> state0 = previousState;
                NFAState<Integer> state1 = returnNFA.newState();

                alphabet.toMap().values().forEach(letter -> state0.addStateTransfer(letter, state1));
                state0.addSigmaTransfer(state1);

                previousState = state1;
                continue;
            }
            try {
                int dimSignature = Integer.parseInt(signature);
                NFAState<Integer> state0 = previousState;
                NFAState<Integer> state1 = returnNFA.newState();

                state0.addStateTransfer(dimSignature, state1);

                previousState = state1;
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException();
            }
        }
        returnNFA.addAcceptState(previousState);
        assert returnNFA.isComplete();
        return returnNFA;
    }

    /**
     * construct NFA from a given shape signature list (list consists of integer numbers, star wildcard[*] and
     * dots wildcard[..]).
     * @param signatures given signature list
     * @return constructed NFA
     * @throws NullPointerException if {@code signatures} is {@code null}
     * @throws IllegalArgumentException if shape signature is invalid (consists tokens other than non-negative
     *                                  integer, star wildcard[*] or star wildcard[..])
     */
    public static NFA<Integer> fromShapeSignatureList(Iterable<String> signatures) {
        Alphabet<Integer> alphabet = new Alphabet<>();
        Optional.ofNullable(signatures).orElseThrow(NullPointerException::new).forEach(signature -> {
            if (signature == null) throw new IllegalArgumentException();
            if (!("..".equals(signature) || "*".equals(signature))) {
                try {
                    int dimSignature = Integer.parseInt(signature);
                    alphabet.add(dimSignature);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException();
                }
            }
        });
        return fromShapeSignatureList(signatures, alphabet);
    }

    /**
     * construct NFA from a given type signature list (list consists of string type names, star wildcard[*] and dots
     * wildcard[..]).
     * @param signatures given signature list
     * @param alphabet constructed NFA
     * @return constructed NFA
     * @throws NullPointerException if either {@code signatures} or {@code alphabet} is {@code null}
     * @throws IllegalArgumentException if type signature list consist {@code} null
     */
    public static NFA<String> fromTypeSignatureList(Iterable<String> signatures, Alphabet<String> alphabet) {
        if (alphabet == null) throw new NullPointerException();
        NFA<String> returnNFA = new NFA<String>(alphabet);
        NFAState<String> previousState = returnNFA.newState();
        for (String signature : Optional.ofNullable(signatures).orElseThrow(NullPointerException::new)) {
            if (signature == null) throw new IllegalArgumentException();
            if ("..".equals(signature)) {
                NFAState<String> state0 = previousState;
                NFAState<String> state1 = returnNFA.newState();
                NFAState<String> state2 = returnNFA.newState();
                NFAState<String> state3 = returnNFA.newState();

                state0.addEpsilonTransfer(state1);
                alphabet.toMap().values().forEach(letter -> state1.addStateTransfer(letter, state2));
                state1.addEpsilonTransfer(state2);
                state1.addSigmaTransfer(state2);
                state2.addEpsilonTransfer(state3);
                state3.addEpsilonTransfer(state0);

                previousState = state3;
                continue;
            }
            if ("*".equals(signature)) {
                NFAState<String> state0 = previousState;
                NFAState<String> state1 = returnNFA.newState();

                state0.addSigmaTransfer(state1);
                alphabet.toMap().values().forEach(letter -> state0.addStateTransfer(letter, state1));

                previousState = state1;
                continue;
            }

            NFAState<String> state0 = previousState;
            NFAState<String> state1 = returnNFA.newState();

            state0.addStateTransfer(signature, state1);

            previousState = state1;
        }
        returnNFA.addAcceptState(previousState);
        assert returnNFA.isComplete();
        return returnNFA;
    }

    /**
     * construct NFA from a given type signature list (list consists of string type names, star wildcard[*] and
     * dots wildcard[..]).
     * @param signatures given signature list
     * @return constructed NFA
     * @throws NullPointerException if {@code signatures} is {@code null}
     * @throws IllegalArgumentException if type signature consists {@code null}
     */
    public static NFA<String> fromTypeSignatureList(Iterable<String> signatures) {
        Alphabet<String> alphabet = new Alphabet<>();
        Optional.ofNullable(signatures).orElseThrow(NullPointerException::new).forEach(signature -> {
            if (signature == null) throw new IllegalArgumentException();
            if (!("..".equals(signature) || "*".equals(signature))) alphabet.add(signature);
        });
        return fromTypeSignatureList(signatures, alphabet);
    }
}

package matcher.nfa;

import matcher.MATLABAlphabet;

import java.util.Optional;

public final class NFABuilder {
    /**
     * build NFA from a shape signature, and a given alphabet
     * @param shapeSignature base shape signature
     * @param alphabet alphabet use to construct NFA
     * @return constructed NFA
     * @throws NullPointerException if {@code shapeSignature} or {@code alphabet} is {@code null}
     * @throws IllegalArgumentException if any part of shape signature is {@code null} or is not a valid signature
     *                                  candidate.
     */
    public static MATLABNFA<Integer> fromShapeSignatureList(Iterable<String> shapeSignature, MATLABAlphabet<Integer> alphabet) {
        if (alphabet == null) throw new NullPointerException();
        MATLABNFA<Integer> returnNFA = new MATLABNFA<>(alphabet);
        NFAState<Integer> previousState = returnNFA.newState();
        returnNFA.setStartingState(previousState);
        for (String signature : Optional.ofNullable(shapeSignature).orElseThrow(NullPointerException::new)) {
            if (signature == null) throw new IllegalArgumentException();
            if ("..".equals(signature)) {
                NFAState<Integer> state0 = previousState;
                NFAState<Integer> state1 = returnNFA.newState();
                NFAState<Integer> state2 = returnNFA.newState();
                NFAState<Integer> state3 = returnNFA.newState();

                state0.addEpsilonStateTransfer(state1);
                state1.addEpsilonStateTransfer(state2);
                state1.addSigmaStateTransfer(state2);
                alphabet.toMap().values().forEach(symbol -> state1.addStateTransfer(symbol, state2));
                state2.addEpsilonStateTransfer(state3);
                state3.addEpsilonStateTransfer(state0);

                previousState = state3;
                continue;
            }
            if ("*".equals(signature)) {
                NFAState<Integer> state0 = previousState;
                NFAState<Integer> state1 = returnNFA.newState();

                state0.addSigmaStateTransfer(state1);
                alphabet.toMap().values().forEach(symbol -> state0.addStateTransfer(symbol, state1));

                previousState = state1;
                continue;
            }
            try {
                int parsedSignature = Integer.parseUnsignedInt(signature);
                NFAState<Integer> state0 = previousState;
                NFAState<Integer> state1 = returnNFA.newState();

                state0.addStateTransfer(parsedSignature, state1);

                previousState = state1;
                continue;
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException();
            }
        }
        returnNFA.addAcceptingState(previousState);
        return returnNFA;
    }

    /**
     * build NFA from a shape signature, by collecting only the symbol appears in the signature
     * @param shapeSignature base shape signature
     * @return constructed NFA
     * @throws NullPointerException if {@code shapeSignature} is {@code null}
     * @throws IllegalArgumentException if any part of shape signature is {@code null} or is not a valid signature
     *                                  candidate.
     */
    public static MATLABNFA<Integer> fromShapeSignatureList(Iterable<String> shapeSignature) {
        MATLABAlphabet<Integer> alphabet = new MATLABAlphabet<>();
        for (String signature : Optional.ofNullable(shapeSignature).orElseThrow(NullPointerException::new)) {
            if (signature == null) throw new IllegalArgumentException();
            try {
                if (!("..".equals(signature) || "*".equals(signature))) {
                    int parsedSignature = Integer.parseUnsignedInt(signature);
                    alphabet.addSymbol(parsedSignature);
                }
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException();
            }
        }
        return fromShapeSignatureList(shapeSignature, alphabet);
    }

    /**
     * build NFA from a type signature, and a given alphabet
     * @param typeSignature base shape signature
     * @param alphabet alphabet use to construct NFA
     * @return constructed NFA
     * @throws NullPointerException if {@code typeSignature} or {@code alphabet} is {@code null}
     * @throws IllegalArgumentException if any part of type signature is {@code null} or is not a valid signature
     *                                  candidate.
     */
    public static MATLABNFA<String> fromTypeSignatureList(Iterable<String> typeSignature, MATLABAlphabet<String> alphabet) {
        if (alphabet == null) throw new NullPointerException();
        MATLABNFA<String> returnNFA = new MATLABNFA<>(alphabet);
        NFAState<String> previousState = returnNFA.newState();
        returnNFA.setStartingState(previousState);
        for (String signature : Optional.ofNullable(typeSignature).orElseThrow(NullPointerException::new)) {
            if (signature == null) throw new IllegalArgumentException();
            if ("..".equals(signature)) {
                NFAState<String> state0 = previousState;
                NFAState<String> state1 = returnNFA.newState();
                NFAState<String> state2 = returnNFA.newState();
                NFAState<String> state3 = returnNFA.newState();

                state0.addEpsilonStateTransfer(state1);
                state1.addEpsilonStateTransfer(state2);
                state1.addSigmaStateTransfer(state2);
                alphabet.toMap().values().forEach(symbol -> state1.addStateTransfer(symbol, state2));
                state2.addEpsilonStateTransfer(state3);
                state3.addEpsilonStateTransfer(state0);

                previousState = state3;
                continue;
            }
            if ("*".equals(signature)) {
                NFAState<String> state0 = previousState;
                NFAState<String> state1 = returnNFA.newState();

                state0.addSigmaStateTransfer(state1);
                alphabet.toMap().values().forEach(symbol -> state0.addStateTransfer(symbol, state1));

                previousState = state1;
                continue;
            }
            NFAState<String> state0 = previousState;
            NFAState<String> state1 = returnNFA.newState();

            state0.addStateTransfer(signature, state1);

            previousState = state1;
            continue;
        }
        returnNFA.addAcceptingState(previousState);
        return returnNFA;
    }

    /**
     * build NFA from a type signature, by collecting only the symbol appears in the signature
     * @param typeSignature base shape signature
     * @return constructed NFA
     * @throws NullPointerException if {@code typeSignature} is {@code null}
     * @throws IllegalArgumentException if any part of shape signature is {@code null} or is not a valid signature
     *                                  candidate.
     */
    public static NFA<String> fromTypeSignatureList(Iterable<String> typeSignature) {
        MATLABAlphabet<String> alphabet = new MATLABAlphabet<>();
        for (String signature : Optional.ofNullable(typeSignature).orElseThrow(NullPointerException::new)) {
            if (signature == null) throw new IllegalArgumentException();
            if (!("..".equals(signature) || "*".equals(signature))) alphabet.addSymbol(signature);
        }
        return fromTypeSignatureList(typeSignature, alphabet);
    }
}

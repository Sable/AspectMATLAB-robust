package matcher.nfa;

import matcher.Alphabet;

import java.util.Optional;

public final class NFABuilder {
    public static NFA<Integer> fromShapeSignatureList(Iterable<String> shapeSignature, Alphabet<?, Integer> alphabet) {
        if (alphabet == null) throw new NullPointerException();
        NFA<Integer> returnNFA = new NFA<>(alphabet);
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
}

import ast.*;
import matcher.*;
import matcher.dfa.DFA;
import matcher.nfa.NFA;
import matcher.nfa.NFABuilder;
import matcher.nfa.NFAState;
import natlab.DecIntNumericLiteralValue;
import utils.LiteralBuilder;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String args[]) {
        Alphabet<Integer, Character> alphabet = new Alphabet(IntStream.iterate(2, x -> x + 1).iterator(), 0, 1);
        alphabet.addSymbol('a');
        alphabet.addSymbol('b');
        alphabet.addSymbol('c');

        NFA<Character> nfa = new NFA<>(alphabet);
        NFAState<Character> state1 = nfa.newState();
        NFAState<Character> state2 = nfa.newState();
        NFAState<Character> state3 = nfa.newState();
        NFAState<Character> state4 = nfa.newState();

        state1.addStateTransfer('a', state1);
        state1.addStateTransfer('b', state1);
        state1.addStateTransfer('c', state1);
        state1.addStateTransfer('a', state2);

        state2.addStateTransfer('b', state3);
        state2.addEpsilonStateTransfer(state3);

        state3.addStateTransfer('a', state4);

        state4.addStateTransfer('a', state4);
        state4.addStateTransfer('b', state4);
        state4.addStateTransfer('c', state4);

        nfa.setStartingState(state1);
        nfa.addAcceptingState(state4);

        System.out.println(nfa.toString());

        System.out.println(new DFA<Character>(nfa));
    }
}

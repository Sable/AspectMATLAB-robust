import ast.Expr;
import ast.MinusExpr;
import ast.PlusExpr;
import matcher.Alphabet;
import matcher.NFA;
import matcher.NFAState;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String args[]) {
        Alphabet<Integer, Class<? extends Expr>> alphabet = new Alphabet<>(
                IntStream.iterate(2, x -> x + 1).iterator(),
                new Integer(0),
                new Integer(1)
        );

        alphabet.addSymbol(PlusExpr.class);
        alphabet.addSymbol(MinusExpr.class);

        NFA<Class<? extends Expr>> nfa = new NFA<>(
                Arrays.asList("state1", "state2").iterator(),
                alphabet
        );
        NFAState<Class<? extends Expr>> startState = nfa.newState();
        NFAState<Class<? extends Expr>> nextState = nfa.newState();

        nfa.setStartingState(startState);
        nfa.addAcceptingState(nextState);
        startState.addStateTransfer(PlusExpr.class, nextState);

        System.out.println(nfa.toString());
    }
}

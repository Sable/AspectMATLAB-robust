import matcher.*;
import matcher.dfa.DFA;
import matcher.nfa.NFA;
import matcher.nfa.NFABuilder;
import utils.LiteralBuilder;

import java.util.stream.IntStream;

public class Main {
    public static void main(String args[]) {
        Alphabet<Integer, Integer> alphabet = new Alphabet<>(
                IntStream.iterate(2, x -> x + 1).iterator(),
                new Integer(0),
                new Integer(1)
        );

        alphabet.addSymbol(1);
        alphabet.addSymbol(2);
        alphabet.addSymbol(3);

        NFA<Integer> nfa = NFABuilder.fromShapeSignatureList(
                new LiteralBuilder<String>().put("1", "2", "3").asList(),
                alphabet
        );

        System.out.println(nfa);
        DFA<Integer> dfa = new DFA<>(nfa);

        System.out.println(dfa);
    }
}

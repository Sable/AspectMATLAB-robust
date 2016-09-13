import ast.*;
import matcher.*;
import matcher.dfa.DFA;
import matcher.nfa.NFA;
import matcher.nfa.NFABuilder;
import natlab.DecIntNumericLiteralValue;
import utils.LiteralBuilder;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String args[]) {
        Alphabet<Integer, String> alphabet = new Alphabet<>(
                IntStream.iterate(2, x -> x + 1).iterator(),
                new Integer(0),
                new Integer(1)
        );

        System.out.println(alphabet.toMATLABFunction(
                ((expr, symbol) ->
                        new ParameterizedExpr(new NameExpr(new Name("isa")), new List<>(expr, new StringLiteralExpr
                                (symbol)))),
                symbolCode -> new IntLiteralExpr(new DecIntNumericLiteralValue(symbolCode.toString())),
                "alphabetFunc",
                IntStream.iterate(1, x -> x + 1).mapToObj(x -> String.format("AM_VAR_%d", x)).iterator()
        ).getPrettyPrinted());

        NFA<String> nfa = NFABuilder.fromTypeSignatureList(
                new LiteralBuilder<String>().put("..").asList(),
                alphabet
        );

        System.out.println(nfa);
        DFA<String> dfa = new DFA<>(nfa);

        System.out.println(dfa);
    }
}

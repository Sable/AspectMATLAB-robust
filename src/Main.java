import ast.*;
import matcher.MATLABAlphabet;
import matcher.dfa.MATLABDFA;
import matcher.nfa.MATLABNFA;
import matcher.nfa.NFABuilder;
import natlab.DecIntNumericLiteralValue;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String args[]) {
        MATLABAlphabet<Integer> alphabet = new MATLABAlphabet<>();
        alphabet.addSymbol(1);
        alphabet.addSymbol(2);
        alphabet.addSymbol(3);

        MATLABNFA<Integer> matlabNFA = NFABuilder.fromShapeSignatureList(Arrays.asList("1", "..", "2"), alphabet);
        MATLABDFA<Integer> matlabDFA = new MATLABDFA<>(matlabNFA);

        Function matlabAST = alphabet.toMATLABFunction(
                (expr, value) -> new EQExpr(expr, new IntLiteralExpr(new DecIntNumericLiteralValue(Integer.toString(value)))),
                "demoAlphabet",
                IntStream.iterate(1, x -> x + 1).mapToObj(x -> "t" + x).iterator()
        );

        Function dfaAST = matlabDFA.toMATLABFunction(matlabAST,
                (x) -> new ParameterizedExpr(new NameExpr(new Name("size")), new List<Expr>(x))
                , "matcher", IntStream.iterate(1, x -> x + 1).mapToObj(x -> "t" + x).iterator());


        dfaAST.addNestedFunction(matlabAST);
        System.out.println(dfaAST.getPrettyPrinted());
    }
}

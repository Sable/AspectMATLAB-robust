import ast.*;
import matcher.Alphabet;
import matcher.nfa.NFA;
import matcher.nfa.NFABuilder;
import matcher.nfa.NFAState;

import java.util.Arrays;

public class Main {
    public static void main(String args[]) {
        /*
        final String aspectFilepath = "/Users/k9/Desktop/AspectMATLAB/src/aspect.matlab";

        INotifier notifier = new INotifier() {
            @Override
            public void Notify(String s, IReport iReport) {
                if (!iReport.GetIsOk()) {
                    for (Message message : iReport) {
                        System.out.println(String.format(
                                "[%8s][%3d : %3d] %s",
                                message.GetSeverity(),
                                message.GetLine(),
                                message.GetColumn(),
                                message.GetText()
                        ));
                    }
                }
            }
        };

        Result<UnitNode> rawAST = MRecognizer.RecognizeFile(aspectFilepath, true, notifier);
        CompilationUnits ast = NodeToAstTransformer.Transform(rawAST.GetValue());

        assert ast.getProgram(0) instanceof AspectDef;

        Expr rawPattern = ((AspectDef) ast.getProgram(0)).getPattern(0).getPattern(0).getExpr();
        abstractPattern.Pattern pattern = Pattern.buildAbstractPattern(rawPattern, aspectFilepath);
        System.out.println(pattern.toString());

        UniqueMap<Integer, String> map = new UniqueMap<>();
        map.put(1, "2");
        map.put(1, "3");
        */
        Alphabet<Integer> alphabet = new Alphabet<Integer>().add(1).add(2).add(3);
        System.out.println(
                NFABuilder.fromDimensionSignatureList(
                        Arrays.asList("1","..","2","*","3"),
                        alphabet
                )
        );
    }
}

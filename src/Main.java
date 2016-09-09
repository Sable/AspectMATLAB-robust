import Matlab.Nodes.UnitNode;
import Matlab.Recognizer.INotifier;
import Matlab.Recognizer.MRecognizer;
import Matlab.Transformer.NodeToAstTransformer;
import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Result;
import abstractPattern.*;
import abstractPattern.Pattern;
import abstractPattern.primitive.Annotation;
import abstractPattern.primitive.Operator;
import ast.*;
import matcher.Alphabet;
import utils.UniqueMap;

import java.util.Iterator;
import java.util.stream.IntStream;

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
        Alphabet<Class<? extends Expr>> testAlphabet = new Alphabet<>();
        testAlphabet.add(PlusExpr.class);
        testAlphabet.add(MinusExpr.class);
        testAlphabet.add(MatrixExpr.class);
        testAlphabet.add(PlusExpr.class);

        Alphabet<Class<? extends Expr>> test2 = new Alphabet<Class<? extends Expr>>().add(PlusExpr.class).add(UPlusExpr.class);

        System.out.println(testAlphabet.toString());
        System.out.println(test2.toString());
        System.out.println("intersection:" + testAlphabet.intersection(test2));
        System.out.println("union:       " + testAlphabet.union(test2));
        System.out.println("subtraction: " + testAlphabet.substraction(test2));
    }
}

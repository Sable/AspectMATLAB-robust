import Matlab.Nodes.UnitNode;
import Matlab.Recognizer.INotifier;
import Matlab.Recognizer.MRecognizer;
import Matlab.Transformer.NodeToAstTransformer;
import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Result;
import abstractPattern.Action;
import ast.*;
import transformer.expr.CopyExprTransformer;
import transformer.expr.examples.IntLiteralsTransform;
import transformer.program.CopyProgramTransformer;
import transformer.stmt.CopyStmtTransformer;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static CompilationUnits parseOrDie(String path) {
        Result<UnitNode> result = MRecognizer.RecognizeFile(path, true, new INotifier() {
            @Override
            public void Notify(String s, IReport iReport) {
                if (iReport.GetIsOk()) return;
                System.out.println(String.format("At file %s:", s));
                for (Message message : iReport) {
                    System.out.println(String.format("[%3d:%3d]\t%s:%s",
                            message.GetLine(),
                            message.GetColumn(),
                            message.GetSeverity(),
                            message.GetText()
                    ));
                    System.exit(1);
                }
            }
        });
        return NodeToAstTransformer.Transform(result.GetValue());
    }

    public static void main(String args[]) throws Exception {
        final String path = "/Users/k9/Desktop/AspectMATLAB/src/aspect.matlab";
        CompilationUnits compilationUnits = parseOrDie(path);

        CopyProgramTransformer<CopyStmtTransformer<CopyExprTransformer>> transformer =
                new CopyProgramTransformer<>(new CopyStmtTransformer<>(new IntLiteralsTransform(x -> x + 1)));

        System.out.println(transformer.transform(compilationUnits).getPrettyPrinted());

    }
}

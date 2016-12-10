import Matlab.Nodes.UnitNode;
import Matlab.Recognizer.INotifier;
import Matlab.Recognizer.MRecognizer;
import Matlab.Transformer.NodeToAstTransformer;
import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Result;
import ast.*;
import transformer.aspect.AspectExprTransformer;
import transformer.expr.CopyExprTransformer;
import transformer.pattern.CopyPatternTransformer;
import transformer.program.CopyProgramTransformer;
import transformer.stmt.CopyStmtTransformer;
import utils.codeGen.builders.IntLiteralExprBuilder;
import utils.codeGen.builders.ParameterizedExprBuilder;

import java.util.*;
import java.util.List;

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

        Script script = (Script) compilationUnits.getProgram(0);
        AspectExprTransformer transformer = new AspectExprTransformer();
        ExprStmt exprStmt = (ExprStmt) script.getStmt(0);
        Expr result = transformer.transform(exprStmt.getExpr());
        transformer.getPrefixStmtList().forEach(statement -> System.out.println(statement.getPrettyPrinted()));
        System.out.println(result.getPrettyPrinted());
        transformer.getSuffixStmtList().forEach(statement -> System.out.println(statement.getPrettyPrinted()));
    }
}

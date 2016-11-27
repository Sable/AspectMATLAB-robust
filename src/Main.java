import Matlab.Nodes.UnitNode;
import Matlab.Recognizer.INotifier;
import Matlab.Recognizer.MRecognizer;
import Matlab.Transformer.NodeToAstTransformer;
import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Result;
import ast.*;
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

        CopyProgramTransformer<DemoStmtTransformer, CopyPatternTransformer> transformer = new CopyProgramTransformer<>(
                new DemoStmtTransformer(), new CopyPatternTransformer()
        );
        System.out.println(compilationUnits.getPrettyPrinted());
        compilationUnits = transformer.transform(compilationUnits);
        System.out.println(compilationUnits.getPrettyPrinted());
    }
}

class DemoStmtTransformer extends CopyStmtTransformer<CopyExprTransformer> {
    public DemoStmtTransformer() {
        super(new CopyExprTransformer(){
            @Override
            protected Expr casePlusExpr(PlusExpr plusExpr) {
                Expr lhs = this.transform(plusExpr.getLHS());
                Expr rhs = this.transform(plusExpr.getRHS());
                if (lhs instanceof IntLiteralExpr && rhs instanceof IntLiteralExpr) {
                    int lhsValue = ((IntLiteralExpr) lhs).getValue().getValue().intValue();
                    int rhsValue = ((IntLiteralExpr) rhs).getValue().getValue().intValue();
                    return new IntLiteralExprBuilder().setValue(lhsValue + rhsValue).build();
                } else {
                    PlusExpr copiedExpr = (PlusExpr) this.ASTNodeHandle(plusExpr);
                    copiedExpr.setLHS(lhs);
                    copiedExpr.setRHS(rhs);
                    return copiedExpr;
                }
            }
        });
    }

    @Override
    protected List<Stmt> caseAssignStmt(AssignStmt assignStmt) {
        List<Stmt> retList = new LinkedList<>(super.caseAssignStmt(assignStmt));
        retList.add(new ExprStmt(new ParameterizedExprBuilder()
                .setTarget("disp")
                .addParameter(new StringLiteralExpr(assignStmt.getPrettyPrinted().trim()))
                .build()
        ));
        return retList;
    }
}
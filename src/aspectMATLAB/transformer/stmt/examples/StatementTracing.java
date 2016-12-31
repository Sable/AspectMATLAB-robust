package aspectMATLAB.transformer.stmt.examples;

import ast.ExprStmt;
import ast.Stmt;
import ast.StringLiteralExpr;
import aspectMATLAB.transformer.expr.InplaceExprTransformer;
import aspectMATLAB.transformer.stmt.InplaceStmtTransformer;
import aspectMATLAB.utils.codeGen.builders.ParameterizedExprBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class StatementTracing extends InplaceStmtTransformer<InplaceExprTransformer> {
    public StatementTracing() {
        super(new InplaceExprTransformer());
    }

    @Override
    public List<Stmt> transform(Stmt statement) {
        List<Stmt> retList = new LinkedList<>(super.transform(statement));
        ExprStmt appendStmt = new ExprStmt(new ParameterizedExprBuilder()
                .setTarget("disp")
                .addParameter(new StringLiteralExpr(
                        "statement of type " + statement.getClass().toString()
                ))
                .build());
        appendStmt.setOutputSuppressed(true);
        retList.add(appendStmt);
        return Collections.unmodifiableList(retList);
    }
}

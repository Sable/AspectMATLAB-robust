package transformer.stmt.examples;

import ast.ExprStmt;
import ast.Stmt;
import ast.StringLiteralExpr;
import transformer.expr.InplaceExprTransformer;
import transformer.stmt.InplaceStmtTransformer;
import utils.codeGen.builders.ParameterizedExprBuilder;

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

package utils.codeGen.builders;

import ast.Expr;
        import ast.ExprStmt;

        import java.util.Optional;

public final class ExprStmtBuilder {
    private Expr expr = null;
    private boolean outputSuppressed = true;

    public ExprStmtBuilder setExpr(Expr expr) {
        this.expr = Optional.ofNullable(expr).orElseThrow(NullPointerException::new);
        return this;
    }

    public ExprStmtBuilder setOutputSuppressed(boolean outputSuppressed) {
        this.outputSuppressed = outputSuppressed;
        return this;
    }

    public ExprStmt build() {
        ExprStmt exprStmt = new ExprStmt();
        exprStmt.setExpr(Optional.ofNullable(expr).orElseThrow(NullPointerException::new));
        exprStmt.setOutputSuppressed(outputSuppressed);
        return exprStmt;
    }
}

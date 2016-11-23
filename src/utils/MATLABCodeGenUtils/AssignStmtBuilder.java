package utils.MATLABCodeGenUtils;

import ast.AssignStmt;
import ast.Expr;

import java.util.Optional;

public final class AssignStmtBuilder {
    private Expr lhs = null;
    private Expr rhs = null;
    private boolean outputSuppressed = true;

    public AssignStmtBuilder setLHS(Expr lhs) {
        this.lhs = Optional.ofNullable(lhs).orElseThrow(NullPointerException::new);
        return this;
    }
    public AssignStmtBuilder setRHS(Expr rhs) {
        this.rhs = Optional.ofNullable(rhs).orElseThrow(NullPointerException::new);
        return this;
    }
    public AssignStmtBuilder setOutputSuppressed(boolean outputSuppressed) {
        this.outputSuppressed = outputSuppressed;
        return this;
    }
    public AssignStmt build() {
        AssignStmt retStmt = new AssignStmt();
        retStmt.setLHS(Optional.ofNullable(lhs).orElseThrow(NullPointerException::new));
        retStmt.setRHS(Optional.ofNullable(rhs).orElseThrow(NullPointerException::new));
        retStmt.setOutputSuppressed(outputSuppressed);
        return retStmt;
    }
}
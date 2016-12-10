package utils.codeGen.builders;

import ast.AssignStmt;
import ast.Expr;
import ast.Name;
import ast.NameExpr;

import java.util.Optional;

public final class AssignStmtBuilder {
    private Expr lhs = null;
    private Expr rhs = null;
    private boolean outputSuppressed = true;

    public AssignStmtBuilder setLHS(Expr lhs) {
        this.lhs = Optional.ofNullable(lhs).orElseThrow(NullPointerException::new);
        return this;
    }

    public AssignStmtBuilder setLHS(String lhs) {
        if (lhs == null) throw new NullPointerException();
        if (lhs.isEmpty()) throw new IllegalArgumentException();
        this.lhs = new NameExpr(new Name(lhs));
        return this;
    }

    public AssignStmtBuilder setRHS(Expr rhs) {
        this.rhs = Optional.ofNullable(rhs).orElseThrow(NullPointerException::new);
        return this;
    }

    public AssignStmtBuilder setRHS(String rhs) {
        if (rhs == null) throw new NullPointerException();
        if (rhs.isEmpty()) throw new IllegalArgumentException();
        this.rhs = new NameExpr(new Name(rhs));
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
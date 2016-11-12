package utils;


import ast.*;
import natlab.DecIntNumericLiteralValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public final class MATLABCodeGenUtil {
    public final class ParameterizedExprBuilder {
        private Expr targetExpr = null;
        private List<Expr> paramList = new LinkedList<>();

        public ParameterizedExprBuilder setTarget(Expr expr) {
            targetExpr = Optional.ofNullable(expr).orElseThrow(NullPointerException::new);
            return this;
        }
        public ParameterizedExprBuilder setTarget(String name) {
            if (name == null) throw new NullPointerException();
            if (name.isEmpty()) throw new IllegalArgumentException();
            targetExpr = new NameExpr(new Name(name));
            return this;
        }
        public ParameterizedExprBuilder addParameter(Expr param) {
            paramList.add(Optional.ofNullable(param).orElseThrow(NullPointerException::new));
            return this;
        }
        public ParameterizedExprBuilder addParameter(String name) {
            if (name == null) throw new NullPointerException();
            if (name.isEmpty()) throw new IllegalArgumentException();
            return this.addParameter(new NameExpr(new Name(name)));
        }
        public ParameterizedExprBuilder addParameter(int intLiteral) {
            return addParameter(new IntLiteralExpr(new DecIntNumericLiteralValue(Integer.toString(intLiteral))));
        }
        public ParameterizedExpr build() {
            ParameterizedExpr retExpr = new ParameterizedExpr();
            retExpr.setTarget(Optional.ofNullable(this.targetExpr).orElseThrow(NullPointerException::new));
            for (Expr param : paramList) retExpr.addArg(param);
            return retExpr;
        }
    }


    public final class CellIndexExprBuilder {
        private Expr targetExpr = null;
    }

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
}

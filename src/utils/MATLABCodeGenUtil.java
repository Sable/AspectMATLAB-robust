package utils;

import ast.*;
import natlab.DecIntNumericLiteralValue;

import java.util.Optional;

public final class MATLABCodeGenUtil {
    public static IntLiteralExpr buildIntLiteralExpr(int integer) {
        DecIntNumericLiteralValue literalValue = new DecIntNumericLiteralValue(Integer.toString(integer));

        IntLiteralExpr intLiteralExpr = new IntLiteralExpr();
        intLiteralExpr.setValue(literalValue);

        return intLiteralExpr;
    }

    public static AssignStmt buildAssignStmt(Expr lhs, Expr rhs) {
        AssignStmt assignStmt = new AssignStmt();
        assignStmt.setLHS(Optional.ofNullable(lhs).orElseThrow(NullPointerException::new));
        assignStmt.setRHS(Optional.ofNullable(rhs).orElseThrow(NullPointerException::new));
        assignStmt.setOutputSuppressed(true);

        lhs.setParent(assignStmt);
        rhs.setParent(assignStmt);
        return assignStmt;
    }

    public static ParameterizedExpr buildParameterizedExpr(String targetName, Expr ...params) {
        ParameterizedExpr parameterizedExpr = new ParameterizedExpr();
        parameterizedExpr.setTarget(new NameExpr(new Name(
                Optional.ofNullable(targetName).orElseThrow(NullPointerException::new)
        )));
        for (Expr param : params) {
            parameterizedExpr.addArg(param);
            param.setParent(parameterizedExpr);
        }
        return parameterizedExpr;
    }
}

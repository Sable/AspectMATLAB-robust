package utils.codeGen.builders;

import ast.Expr;
import ast.IntLiteralExpr;
import ast.UMinusExpr;
import natlab.DecIntNumericLiteralValue;

public final class IntLiteralExprBuilder {
    private int value = 0;

    public IntLiteralExprBuilder setValue(int value) {
        this.value = value;
        return this;
    }
    public Expr build() {
        if (value < 0) {
            return new UMinusExpr(
                    new IntLiteralExpr(new DecIntNumericLiteralValue(Integer.toString(Math.abs(value))))
            );
        } else {
            return new IntLiteralExpr(new DecIntNumericLiteralValue(Integer.toString(value)));
        }
    }
}
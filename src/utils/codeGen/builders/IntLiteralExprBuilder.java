package utils.codeGen.builders;

import ast.Expr;
import ast.IntLiteralExpr;
import ast.UMinusExpr;
import natlab.DecIntNumericLiteralValue;

public final class IntLiteralExprBuilder {
    private long value = 0;

    public IntLiteralExprBuilder setValue(long value) {
        this.value = value;
        return this;
    }

    public IntLiteralExprBuilder setValue(int value) {
        this.value = (long) value;
        return this;
    }

    public Expr build() {
        if (value < 0) {
            return new UMinusExpr(
                    new IntLiteralExpr(new DecIntNumericLiteralValue(Long.toString(Math.abs(value))))
            );
        } else {
            return new IntLiteralExpr(new DecIntNumericLiteralValue(Long.toString(value)));
        }
    }
}
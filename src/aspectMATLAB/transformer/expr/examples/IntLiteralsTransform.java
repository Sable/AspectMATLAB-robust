package aspectMATLAB.transformer.expr.examples;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import aspectMATLAB.transformer.expr.CopyExprTransformer;

import java.util.function.IntUnaryOperator;

public final class IntLiteralsTransform extends CopyExprTransformer {
    private final IntUnaryOperator manipulationFunction;

    public IntLiteralsTransform(IntUnaryOperator function) {
        this.manipulationFunction = function;
    }

    @Override
    protected Expr caseIntLiteralExpr(IntLiteralExpr intLiteralExpr) {
        int newInteger = manipulationFunction
                .applyAsInt(intLiteralExpr.getValue().getValue().intValue());
        IntLiteralExpr newLiteralExpr = new IntLiteralExpr(
                new DecIntNumericLiteralValue(Integer.toString(newInteger))
        );
        return super.caseIntLiteralExpr(newLiteralExpr);
    }
}

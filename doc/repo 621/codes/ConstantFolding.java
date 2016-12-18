package transformer.expr.examples;

import ast.*;
import natlab.FPNumericLiteralValue;
import transformer.expr.CopyExprTransformer;
import utils.codeGen.builders.IntLiteralExprBuilder;

public final class ConstantFolding extends CopyExprTransformer {
    @Override
    protected Expr casePlusExpr(PlusExpr plusExpr) {
        Expr lhsExpr = this.transform(plusExpr.getLHS());
        Expr rhsExpr = this.transform(plusExpr.getRHS());

        if (lhsExpr instanceof IntLiteralExpr && rhsExpr instanceof IntLiteralExpr) {
            int lhsValue = ((IntLiteralExpr) lhsExpr).getValue().getValue().intValue();
            int rhsValue = ((IntLiteralExpr) rhsExpr).getValue().getValue().intValue();

            int resultValue = lhsValue + rhsValue;

            return new IntLiteralExprBuilder().setValue(resultValue).build();
        } else {
            return new PlusExpr(lhsExpr, rhsExpr);
        }
    }

    @Override
    protected Expr caseMinusExpr(MinusExpr minusExpr) {
        Expr lhsExpr = this.transform(minusExpr.getLHS());
        Expr rhsExpr = this.transform(minusExpr.getRHS());

        if (lhsExpr instanceof IntLiteralExpr && rhsExpr instanceof IntLiteralExpr) {
            int lhsValue = ((IntLiteralExpr) lhsExpr).getValue().getValue().intValue();
            int rhsValue = ((IntLiteralExpr) rhsExpr).getValue().getValue().intValue();

            int resultValue = lhsValue - rhsValue;

            return new IntLiteralExprBuilder().setValue(resultValue).build();
        } else {
            return new MinusExpr(lhsExpr, rhsExpr);
        }
    }

    @Override
    protected Expr caseMTimesExpr(MTimesExpr mTimesExpr) {
        Expr lhsExpr = this.transform(mTimesExpr.getLHS());
        Expr rhsExpr = this.transform(mTimesExpr.getRHS());

        if (lhsExpr instanceof IntLiteralExpr && rhsExpr instanceof IntLiteralExpr) {
            int lhsValue = ((IntLiteralExpr) lhsExpr).getValue().getValue().intValue();
            int rhsValue = ((IntLiteralExpr) rhsExpr).getValue().getValue().intValue();

            int resultValue = lhsValue * rhsValue;

            return new IntLiteralExprBuilder().setValue(resultValue).build();
        } else {
            return new MTimesExpr(lhsExpr, rhsExpr);
        }
    }

    @Override
    protected Expr caseMDivExpr(MDivExpr mDivExpr) {
        Expr lhsExpr = this.transform(mDivExpr.getLHS());
        Expr rhsExpr = this.transform(mDivExpr.getRHS());

        if (lhsExpr instanceof IntLiteralExpr && rhsExpr instanceof IntLiteralExpr) {
            int lhsValue = ((IntLiteralExpr) lhsExpr).getValue().getValue().intValue();
            int rhsValue = ((IntLiteralExpr) rhsExpr).getValue().getValue().intValue();

            if (rhsValue == 0) throw new ArithmeticException();
            double resultValue = ((double) lhsValue) / rhsValue;

            return new FPLiteralExpr(new FPNumericLiteralValue(Double.toString(resultValue)));
        } else {
            return new MDivExpr(lhsExpr, rhsExpr);
        }
    }
}

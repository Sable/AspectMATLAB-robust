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

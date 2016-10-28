package transformer.pattern;

import ast.Expr;
import ast.OrExpr;

import java.util.Map;

public final class OrTransformer extends PatternTransformer {
    private final PatternTransformer lhsTransformer;
    private final PatternTransformer rhsTransformer;

    public OrTransformer(OrExpr orExpr, Map<String, Expr> predefinedPattern) {
        super(orExpr, predefinedPattern);
        lhsTransformer = PatternTransformer.buildPatternTransformer(orExpr.getLHS(), predefinedPattern);
        rhsTransformer = PatternTransformer.buildPatternTransformer(orExpr.getRHS(), predefinedPattern);
    }

    @Override
    public boolean hasFurtherTransform() {
        return lhsTransformer.hasFurtherTransform() || rhsTransformer.hasFurtherTransform();
    }

    @Override
    public boolean hasTransformOnCurrentNode() {
        return false;
    }

    @Override
    public Expr copyAndTransform() {
        Expr lhsTransform = lhsTransformer.copyAndTransform();
        Expr rhsTransform = rhsTransformer.copyAndTransform();
        OrExpr copiedOrExpr = (OrExpr) pattern.copy();
        copiedOrExpr.setLHS(lhsTransform);
        copiedOrExpr.setRHS(rhsTransform);
        return copiedOrExpr;
    }
}

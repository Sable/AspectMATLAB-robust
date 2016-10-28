package transformer.pattern;

import ast.AndExpr;
import ast.Expr;

import java.util.Map;

public final class AndTransformer extends PatternTransformer {
    private final PatternTransformer lhsTransformer;
    private final PatternTransformer rhsTransformer;

    public AndTransformer(AndExpr andExpr, Map<String, Expr> predefinedPattern) {
        super(andExpr, predefinedPattern);

        lhsTransformer = PatternTransformer.buildPatternTransformer(andExpr.getLHS(), predefinedPattern);
        rhsTransformer = PatternTransformer.buildPatternTransformer(andExpr.getRHS(), predefinedPattern);
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

        AndExpr copiedAndExpr = (AndExpr) pattern.copy();
        copiedAndExpr.setLHS(lhsTransform);
        copiedAndExpr.setRHS(rhsTransform);
        return copiedAndExpr;
    }
}

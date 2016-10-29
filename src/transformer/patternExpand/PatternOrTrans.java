package transformer.patternExpand;

import ast.Expr;
import ast.OrExpr;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternOrTrans extends PatternTrans {
    private final PatternTrans lhsTransformer;
    private final PatternTrans rhsTransformer;

    public PatternOrTrans(OrExpr orExpr, Map<String, Expr> predefinedPattern) {
        super(orExpr, predefinedPattern);
        lhsTransformer = PatternTrans.buildPatternTransformer(orExpr.getLHS(), predefinedPattern);
        rhsTransformer = PatternTrans.buildPatternTransformer(orExpr.getRHS(), predefinedPattern);
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
    public Expr copyAndTransform() throws UnboundedIdentifier {
        Expr lhsTransform = lhsTransformer.copyAndTransform();
        Expr rhsTransform = rhsTransformer.copyAndTransform();
        OrExpr copiedOrExpr = (OrExpr) pattern.copy();
        copiedOrExpr.setLHS(lhsTransform);
        copiedOrExpr.setRHS(rhsTransform);
        return copiedOrExpr;
    }
}

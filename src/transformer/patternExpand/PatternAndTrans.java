package transformer.patternExpand;

import ast.AndExpr;
import ast.Expr;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternAndTrans extends PatternTrans {
    private final PatternTrans lhsTransformer;
    private final PatternTrans rhsTransformer;

    public PatternAndTrans(AndExpr andExpr, Map<String, Expr> predefinedPattern) {
        super(andExpr, predefinedPattern);

        lhsTransformer = PatternTrans.buildPatternTransformer(andExpr.getLHS(), predefinedPattern);
        rhsTransformer = PatternTrans.buildPatternTransformer(andExpr.getRHS(), predefinedPattern);
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

        AndExpr copiedAndExpr = (AndExpr) pattern.copy();
        copiedAndExpr.setLHS(lhsTransform);
        copiedAndExpr.setRHS(rhsTransform);
        return copiedAndExpr;
    }
}

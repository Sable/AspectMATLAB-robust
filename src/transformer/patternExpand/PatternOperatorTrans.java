package transformer.patternExpand;

import ast.Expr;
import ast.PatternOperator;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternOperatorTrans extends PatternTrans {
    public PatternOperatorTrans(PatternOperator patternOperator, Map<String, Expr> predefinedPattern) {
        super(patternOperator, predefinedPattern);
    }

    @Override
    public boolean hasFurtherTransform() {
        return false;
    }

    @Override
    public boolean hasTransformOnCurrentNode() {
        return false;
    }

    @Override
    public Expr copyAndTransform() throws UnboundedIdentifier {
        return pattern.treeCopy();
    }
}

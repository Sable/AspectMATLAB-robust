package transformer.patternExpand;

import ast.Expr;
import ast.PatternIsType;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternTypeTrans extends PatternTrans {
    public PatternTypeTrans(PatternIsType patternIsType, Map<String, Expr> predefinedPattern) {
        super(patternIsType, predefinedPattern);
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

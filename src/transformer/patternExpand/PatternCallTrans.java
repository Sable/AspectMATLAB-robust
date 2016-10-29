package transformer.patternExpand;

import ast.Expr;
import ast.PatternCall;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternCallTrans extends PatternTrans {
    public PatternCallTrans(PatternCall patternCall, Map<String, Expr> predefinedPattern) {
        super(patternCall, predefinedPattern);
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

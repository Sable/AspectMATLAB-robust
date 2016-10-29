package transformer.patternExpand;

import ast.Expr;
import ast.PatternLoopBody;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternLoopBodyTrans extends PatternTrans {
    public PatternLoopBodyTrans(PatternLoopBody patternLoopBody, Map<String, Expr> predefinedPattern) {
        super(patternLoopBody, predefinedPattern);
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

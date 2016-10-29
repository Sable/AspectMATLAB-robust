package transformer.patternExpand;

import ast.Expr;
import ast.PatternLoopHead;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternLoopHeadTrans extends PatternTrans {
    public PatternLoopHeadTrans(PatternLoopHead patternLoopHead, Map<String, Expr> predefinedPattern) {
        super(patternLoopHead, predefinedPattern);
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

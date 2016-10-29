package transformer.patternExpand;

import ast.Expr;
import ast.PatternWithin;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternWithinTrans extends PatternTrans {
    public PatternWithinTrans(PatternWithin patternWithin, Map<String, Expr> predefinedPattern) {
        super(patternWithin, predefinedPattern);
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

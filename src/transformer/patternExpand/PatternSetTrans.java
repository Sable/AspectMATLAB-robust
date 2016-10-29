package transformer.patternExpand;

import ast.Expr;
import ast.PatternSet;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternSetTrans extends PatternTrans {
    public PatternSetTrans(PatternSet patternSet, Map<String, Expr> predefinedPattern) {
        super(patternSet, predefinedPattern);
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

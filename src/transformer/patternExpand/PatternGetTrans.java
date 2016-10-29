package transformer.patternExpand;

import ast.Expr;
import ast.PatternGet;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternGetTrans extends PatternTrans {
    public PatternGetTrans(PatternGet patternGet, Map<String, Expr> predefinedPattern) {
        super(patternGet, predefinedPattern);
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

package transformer.patternExpand;

import ast.Expr;
import ast.PatternAnnotate;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternAnnotateTrans extends PatternTrans {
    public PatternAnnotateTrans(PatternAnnotate patternAnnotate, Map<String, Expr> predefinedPattern) {
        super(patternAnnotate, predefinedPattern);
    }

    @Override
    public boolean hasTransformOnCurrentNode() {
        return false;
    }

    @Override
    public boolean hasFurtherTransform() {
        return false;
    }

    @Override
    public Expr copyAndTransform() throws UnboundedIdentifier {
        return pattern.treeCopy();
    }
}

package transformer.patternExpand;

import ast.Expr;
import ast.PatternMainExecution;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternMainExecutionTrans extends PatternTrans {
    public PatternMainExecutionTrans(PatternMainExecution patternMainExecution, Map<String, Expr> predefinedPattern) {
        super(patternMainExecution, predefinedPattern);
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

package transformer.patternExpand;

import ast.Expr;
import ast.PatternExecution;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternExecutionTrans extends PatternTrans {
    public PatternExecutionTrans(PatternExecution patternExecution, Map<String, Expr> predefinedPattern) {
        super(patternExecution, predefinedPattern);
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

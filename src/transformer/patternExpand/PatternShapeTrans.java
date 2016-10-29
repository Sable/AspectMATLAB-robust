package transformer.patternExpand;

import ast.Expr;
import ast.PatternDimension;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternShapeTrans extends PatternTrans {
    public PatternShapeTrans(PatternDimension patternDimension, Map<String, Expr> predefinedPattern) {
        super(patternDimension, predefinedPattern);
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

package transformer.patternExpand;

import ast.Expr;
import ast.PatternName;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternNameTrans extends PatternTrans {
    public PatternNameTrans(PatternName patternName, Map<String, Expr> predefinedPattern) {
        super(patternName, predefinedPattern);
    }

    @Override
    public boolean hasFurtherTransform() {
        return true;
    }

    @Override
    public boolean hasTransformOnCurrentNode() {
        return true;
    }

    @Override
    public Expr copyAndTransform() throws UnboundedIdentifier {
        String identifier = ((PatternName) pattern).getName().getID();
        if (!predefinedPattern.containsKey(identifier)) {
            throw new UnboundedIdentifier((PatternName) pattern);
        }
        return predefinedPattern.get(identifier).treeCopy();
    }
}

package transformer.pattern;

import ast.Expr;
import ast.PatternName;
import transformer.UnboundedPatternName;

import java.util.Map;

public final class ExpandPatternTransformer extends CopyPatternTransformer {
    private final Map<String, Expr> predefinedMap;

    public ExpandPatternTransformer(Map<String, Expr> predefinedMap) {
        this.predefinedMap = predefinedMap;
    }

    @Override
    protected Expr caseName(PatternName patternName) {
        if (!predefinedMap.containsKey(patternName.getName().getID())) {
            throw new UnboundedPatternName(patternName);
        }
        return predefinedMap.get(patternName.getName().getID()).treeCopy();
    }
}

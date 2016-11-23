package transformer;

import ast.PatternName;

public final class UnboundedPatternName extends RuntimeException {
    private final PatternName patternName;

    public UnboundedPatternName(PatternName patternName) {
        this.patternName = patternName;
    }

    public PatternName getPatternName() {
        return patternName;
    }
}

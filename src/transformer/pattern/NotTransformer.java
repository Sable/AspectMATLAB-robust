package transformer.pattern;

import ast.Expr;
import ast.NotExpr;

import java.util.Map;

public final class NotTransformer extends PatternTransformer {
    private final PatternTransformer operandTransformer;

    public NotTransformer(NotExpr notExpr, Map<String, Expr> predefinedPattern) {
        super(notExpr, predefinedPattern);
        operandTransformer = PatternTransformer.buildPatternTransformer(notExpr.getOperand(), predefinedPattern);
    }

    @Override
    public boolean hasFurtherTransform() {
        return operandTransformer.hasFurtherTransform();
    }

    @Override
    public boolean hasTransformOnCurrentNode() {
        return false;
    }

    @Override
    public Expr copyAndTransform() {
        Expr operandTransform = operandTransformer.copyAndTransform();

        NotExpr copiedNotExpr = (NotExpr) pattern.copy();
        copiedNotExpr.setOperand(operandTransform);
        return copiedNotExpr;
    }
}

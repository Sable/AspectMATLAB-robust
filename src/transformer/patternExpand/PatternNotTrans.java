package transformer.patternExpand;

import ast.Expr;
import ast.NotExpr;
import transformer.UnboundedIdentifier;

import java.util.Map;

public final class PatternNotTrans extends PatternTrans {
    private final PatternTrans operandTransformer;

    public PatternNotTrans(NotExpr notExpr, Map<String, Expr> predefinedPattern) {
        super(notExpr, predefinedPattern);
        operandTransformer = PatternTrans.buildPatternTransformer(notExpr.getOperand(), predefinedPattern);
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
    public Expr copyAndTransform() throws UnboundedIdentifier {
        Expr operandTransform = operandTransformer.copyAndTransform();

        NotExpr copiedNotExpr = (NotExpr) pattern.copy();
        copiedNotExpr.setOperand(operandTransform);
        return copiedNotExpr;
    }
}

package transformer.patternExpand;

import ast.*;
import transformer.ASTTransformer;
import transformer.UnboundedIdentifier;

import java.util.Map;

public abstract class PatternTrans implements ASTTransformer<Expr> {
    protected Expr pattern;
    protected final Map<String, Expr> predefinedPattern;

    public PatternTrans(Expr pattern, Map<String, Expr> predefinedPattern) {
        this.pattern = pattern;
        this.predefinedPattern = predefinedPattern;
    }

    public abstract boolean hasTransformOnCurrentNode();
    public abstract boolean hasFurtherTransform();
    public abstract Expr copyAndTransform() throws UnboundedIdentifier;

    public static PatternTrans buildPatternTransformer(Expr pattern, Map<String, Expr> predefinedPattern) {
        if (pattern instanceof OrExpr) {
            return new PatternOrTrans((OrExpr) pattern, predefinedPattern);
        } else if (pattern instanceof AndExpr) {
            return new PatternAndTrans((AndExpr) pattern, predefinedPattern);
        } else if (pattern instanceof NotExpr) {
            return new PatternNotTrans((NotExpr) pattern, predefinedPattern);
        } else if (pattern instanceof PatternName) {
            return new PatternNameTrans((PatternName) pattern, predefinedPattern);
        } else if (pattern instanceof PatternGet) {
            return new PatternGetTrans((PatternGet) pattern, predefinedPattern);
        } else if (pattern instanceof PatternSet) {
            return new PatternSetTrans((PatternSet) pattern, predefinedPattern);
        } else if (pattern instanceof PatternCall) {
            return new PatternCallTrans((PatternCall) pattern, predefinedPattern);
        } else if (pattern instanceof PatternExecution) {
            return new PatternExecutionTrans((PatternExecution) pattern, predefinedPattern);
        } else if (pattern instanceof PatternLoop) {
            return new PatternLoopTrans((PatternLoop) pattern, predefinedPattern);
        } else if (pattern instanceof PatternLoopHead) {
            return new PatternLoopHeadTrans((PatternLoopHead) pattern, predefinedPattern);
        } else if (pattern instanceof PatternLoopBody) {
            return new PatternLoopBodyTrans((PatternLoopBody) pattern, predefinedPattern);
        } else if (pattern instanceof PatternAnnotate) {
            return new PatternAnnotateTrans((PatternAnnotate) pattern, predefinedPattern);
        } else if (pattern instanceof PatternMainExecution) {
            return new PatternMainExecutionTrans((PatternMainExecution) pattern, predefinedPattern);
        } else if (pattern instanceof PatternOperator) {
            return new PatternOperatorTrans((PatternOperator) pattern, predefinedPattern);
        } else if (pattern instanceof PatternWithin) {
            return new PatternWithinTrans((PatternWithin) pattern, predefinedPattern);
        } else if (pattern instanceof PatternDimension) {
            return new PatternShapeTrans((PatternDimension) pattern, predefinedPattern);
        } else if (pattern instanceof PatternIsType) {
            return new PatternTypeTrans((PatternIsType) pattern, predefinedPattern);
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }
}

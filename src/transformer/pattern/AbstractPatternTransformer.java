package transformer.pattern;

import ast.*;
import transformer.ASTNodeTransformer;

public abstract class AbstractPatternTransformer implements ASTNodeTransformer {

    @Override
    public abstract ASTNode ASTNodeHandle(ASTNode operand);

    public Expr transform(Expr pattern) {
        if (isBasePattern(pattern)) {
            return caseBasePatterns(pattern);
        } else if (pattern instanceof AndExpr) {
            return caseAndExpr(((AndExpr) pattern));
        } else if (pattern instanceof OrExpr) {
            return caseOrExpr(((OrExpr) pattern));
        } else if (pattern instanceof NotExpr) {
            return caseNotExpr(((NotExpr) pattern));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    protected abstract Expr caseAndExpr(AndExpr andExpr);
    protected abstract Expr caseOrExpr(OrExpr orExpr);
    protected abstract Expr caseNotExpr(NotExpr notExpr);

    protected static boolean isPrimitive(Expr pattern) {
        if (pattern instanceof PatternAnnotate) return true;
        if (pattern instanceof PatternCall) return true;
        if (pattern instanceof PatternExecution) return true;
        if (pattern instanceof PatternGet) return true;
        if (pattern instanceof PatternLoop) return true;
        if (pattern instanceof PatternLoopBody) return true;
        if (pattern instanceof PatternLoopHead) return true;
        if (pattern instanceof PatternMainExecution) return true;
        if (pattern instanceof PatternOperator) return true;
        if (pattern instanceof PatternSet) return true;
        return false;
    }

    protected static boolean isModifier(Expr pattern) {
        if (pattern instanceof PatternWithin) return true;
        if (pattern instanceof PatternDimension) return true;
        if (pattern instanceof PatternIsType) return true;
        return false;
    }

    protected static boolean isBasePattern(Expr pattern) {
        if (isPrimitive(pattern)) return true;
        if (isModifier(pattern)) return true;
        if (pattern instanceof PatternName) return true;
        return false;
    }

    protected Expr caseBasePatterns(Expr basicPattern) {
        if (isPrimitive(basicPattern)) {
            return casePrimitives(basicPattern);
        } else if (isModifier(basicPattern)) {
            return caseModifiers(basicPattern);
        } else if (basicPattern instanceof PatternName) {
            return caseName(((PatternName) basicPattern));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    protected abstract Expr caseName(PatternName patternName);

    protected Expr caseModifiers(Expr modifier) {
        if (modifier instanceof PatternWithin) {
            return caseScope((PatternWithin) modifier);
        } else if (modifier instanceof PatternDimension) {
            return caseShape((PatternDimension) modifier);
        } else if (modifier instanceof PatternIsType) {
            return caseType((PatternIsType) modifier);
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    protected abstract Expr caseScope(PatternWithin patternWithin);
    protected abstract Expr caseShape(PatternDimension patternDimension);
    protected abstract Expr caseType(PatternIsType patternIsType);

    protected Expr casePrimitives(Expr primitive) {
        if (primitive instanceof PatternAnnotate) {
            return caseAnnotation(((PatternAnnotate) primitive));
        } else if (primitive instanceof PatternCall) {
            return caseCall(((PatternCall) primitive));
        } else if (primitive instanceof PatternExecution) {
            return caseExecution(((PatternExecution) primitive));
        } else if (primitive instanceof PatternGet) {
            return caseGet(((PatternGet) primitive));
        } else if (primitive instanceof PatternLoop) {
            return caseLoop(((PatternLoop) primitive));
        } else if (primitive instanceof PatternLoopBody) {
            return caseLoopBody(((PatternLoopBody) primitive));
        } else if (primitive instanceof PatternLoopHead) {
            return caseLoopHead(((PatternLoopHead) primitive));
        } else if (primitive instanceof PatternMainExecution) {
            return caseMainExecution(((PatternMainExecution) primitive));
        } else if (primitive instanceof PatternOperator) {
            return caseOperator(((PatternOperator) primitive));
        } else if (primitive instanceof PatternSet) {
            return caseSet(((PatternSet) primitive));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    protected abstract Expr caseAnnotation(PatternAnnotate patternAnnotate);
    protected abstract Expr caseCall(PatternCall patternCall);
    protected abstract Expr caseExecution(PatternExecution patternExecution);
    protected abstract Expr caseGet(PatternGet patternGet);
    protected abstract Expr caseLoop(PatternLoop patternLoop);
    protected abstract Expr caseLoopBody(PatternLoopBody patternLoopBody);
    protected abstract Expr caseLoopHead(PatternLoopHead patternLoopHead);
    protected abstract Expr caseMainExecution(PatternMainExecution patternMainExecution);
    protected abstract Expr caseOperator(PatternOperator patternOperator);
    protected abstract Expr caseSet(PatternSet patternSet);
}

package transformer.pattern;

import ast.*;

public class InplacePatternTransformer extends AbstractPatternTransformer {
    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Expr caseAndExpr(AndExpr andExpr) {
        Expr lhs = this.transform(andExpr.getLHS());
        Expr rhs = this.transform(andExpr.getRHS());
        andExpr.setLHS(lhs);
        andExpr.setRHS(rhs);
        return andExpr;
    }

    @Override
    protected Expr caseOrExpr(OrExpr orExpr) {
        Expr lhs = this.transform(orExpr.getLHS());
        Expr rhs = this.transform(orExpr.getRHS());
        orExpr.setLHS(lhs);
        orExpr.setRHS(rhs);
        return orExpr;
    }

    @Override
    protected Expr caseNotExpr(NotExpr notExpr) {
        Expr operand = this.transform(notExpr.getOperand());
        notExpr.setOperand(operand);
        return notExpr;
    }

    @Override
    protected Expr caseName(PatternName patternName) {
        return patternName;
    }

    @Override
    protected Expr caseScope(PatternWithin patternWithin) {
        return patternWithin;
    }

    @Override
    protected Expr caseShape(PatternDimension patternDimension) {
        return patternDimension;
    }

    @Override
    protected Expr caseType(PatternIsType patternIsType) {
        return patternIsType;
    }

    @Override
    protected Expr caseAnnotation(PatternAnnotate patternAnnotate) {
        return patternAnnotate;
    }

    @Override
    protected Expr caseCall(PatternCall patternCall) {
        return patternCall;
    }

    @Override
    protected Expr caseExecution(PatternExecution patternExecution) {
        return patternExecution;
    }

    @Override
    protected Expr caseGet(PatternGet patternGet) {
        return patternGet;
    }

    @Override
    protected Expr caseLoop(PatternLoop patternLoop) {
        return patternLoop;
    }

    @Override
    protected Expr caseLoopBody(PatternLoopBody patternLoopBody) {
        return patternLoopBody;
    }

    @Override
    protected Expr caseLoopHead(PatternLoopHead patternLoopHead) {
        return patternLoopHead;
    }

    @Override
    protected Expr caseMainExecution(PatternMainExecution patternMainExecution) {
        return patternMainExecution;
    }

    @Override
    protected Expr caseOperator(PatternOperator patternOperator) {
        return patternOperator;
    }

    @Override
    protected Expr caseSet(PatternSet patternSet) {
        return patternSet;
    }
}

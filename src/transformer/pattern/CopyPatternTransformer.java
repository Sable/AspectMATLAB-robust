package transformer.pattern;

import ast.*;

public class CopyPatternTransformer extends AbstractPatternTransformer {
    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        return operand.copy();
    }

    @Override
    protected Expr caseAndExpr(AndExpr andExpr) {
        AndExpr copiedNode = (AndExpr) ASTNodeHandle(andExpr);

        Expr copiedLHS = this.transform(andExpr.getLHS());
        Expr copiedRHS = this.transform(andExpr.getRHS());
        copiedNode.setLHS(copiedLHS);
        copiedNode.setRHS(copiedRHS);

        return copiedNode;
    }

    @Override
    protected Expr caseOrExpr(OrExpr orExpr) {
        OrExpr copiedNode = (OrExpr) ASTNodeHandle(orExpr);

        Expr copiedLHS = this.transform(orExpr.getLHS());
        Expr copiedRHS = this.transform(orExpr.getRHS());
        copiedNode.setLHS(copiedLHS);
        copiedNode.setRHS(copiedRHS);

        return copiedNode;
    }

    @Override
    protected Expr caseNotExpr(NotExpr notExpr) {
        NotExpr copiedNode = (NotExpr) ASTNodeHandle(notExpr);

        Expr copiedOperand = this.transform(notExpr.getOperand());
        copiedNode.setOperand(copiedOperand);

        return copiedNode;
    }

    @Override
    protected Expr caseName(PatternName patternName) {
        return patternName.treeCopy();
    }

    @Override
    protected Expr caseScope(PatternWithin patternWithin) {
        return patternWithin.treeCopy();
    }

    @Override
    protected Expr caseShape(PatternDimension patternDimension) {
        return patternDimension.treeCopy();
    }

    @Override
    protected Expr caseType(PatternIsType patternIsType) {
        return patternIsType.treeCopy();
    }

    @Override
    protected Expr caseAnnotation(PatternAnnotate patternAnnotate) {
        return patternAnnotate.treeCopy();
    }

    @Override
    protected Expr caseCall(PatternCall patternCall) {
        return patternCall.treeCopy();
    }

    @Override
    protected Expr caseExecution(PatternExecution patternExecution) {
        return patternExecution.treeCopy();
    }

    @Override
    protected Expr caseGet(PatternGet patternGet) {
        return patternGet.treeCopy();
    }

    @Override
    protected Expr caseLoop(PatternLoop patternLoop) {
        return patternLoop.treeCopy();
    }

    @Override
    protected Expr caseLoopBody(PatternLoopBody patternLoopBody) {
        return patternLoopBody.treeCopy();
    }

    @Override
    protected Expr caseLoopHead(PatternLoopHead patternLoopHead) {
        return patternLoopHead.treeCopy();
    }

    @Override
    protected Expr caseMainExecution(PatternMainExecution patternMainExecution) {
        return patternMainExecution.treeCopy();
    }

    @Override
    protected Expr caseOperator(PatternOperator patternOperator) {
        return patternOperator.treeCopy();
    }

    @Override
    protected Expr caseSet(PatternSet patternSet) {
        return patternSet.treeCopy();
    }
}

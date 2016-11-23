package transformer.stmt;

import ast.*;
import transformer.ASTNodeTransformer;
import transformer.expr.AbstractExprTransformer;

import java.util.List;

public abstract class AbstractStmtTransformer<T extends AbstractExprTransformer> implements ASTNodeTransformer{
    protected final T exprTransformer;

    public AbstractStmtTransformer(T exprTransformer) {
        this.exprTransformer = exprTransformer;
    }

    public T getExprTransformer() {
        return exprTransformer;
    }

    @Override
    public abstract ASTNode ASTNodeHandle(ASTNode operand);

    public List<Stmt> transform(Stmt statement) {
        if (statement instanceof ExprStmt) {
            return caseExprStmt(((ExprStmt) statement));
        } else if (statement instanceof AssignStmt) {
            return caseAssignStmt(((AssignStmt) statement));
        } else if (statement instanceof GlobalStmt) {
            return caseGlobalStmt(((GlobalStmt) statement));
        } else if (statement instanceof PersistentStmt) {
            return casePersistentStmt(((PersistentStmt) statement));
        } else if (statement instanceof ShellCommandStmt) {
            return caseShellCommandStmt(((ShellCommandStmt) statement));
        } else if (statement instanceof BreakStmt) {
            return caseBreakStmt(((BreakStmt) statement));
        } else if (statement instanceof ContinueStmt) {
            return caseContinueStmt(((ContinueStmt) statement));
        } else if (statement instanceof ReturnStmt) {
            return caseReturnStmt(((ReturnStmt) statement));
        } else if (statement instanceof EmptyStmt) {
            return caseEmptyStmt(((EmptyStmt) statement));
        } else if (statement instanceof ForStmt) {
            return caseForStmt(((ForStmt) statement));
        } else if (statement instanceof WhileStmt) {
            return caseWhileStmt(((WhileStmt) statement));
        } else if (statement instanceof TryStmt) {
            return caseTryStmt(((TryStmt) statement));
        } else if (statement instanceof SwitchStmt) {
            return caseSwitchStmt(((SwitchStmt) statement));
        } else if (statement instanceof IfStmt) {
            return caseIfStmt(((IfStmt) statement));
        } else if (statement instanceof SpmdStmt) {
            return caseSpmdStmt(((SpmdStmt) statement));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    protected abstract List<Stmt> caseExprStmt(ExprStmt exprStmt);
    protected abstract List<Stmt> caseAssignStmt(AssignStmt assignStmt);
    protected abstract List<Stmt> caseGlobalStmt(GlobalStmt globalStmt);
    protected abstract List<Stmt> casePersistentStmt(PersistentStmt persistentStmt);
    protected abstract List<Stmt> caseShellCommandStmt(ShellCommandStmt shellCommandStmt);
    protected abstract List<Stmt> caseBreakStmt(BreakStmt breakStmt);
    protected abstract List<Stmt> caseContinueStmt(ContinueStmt continueStmt);
    protected abstract List<Stmt> caseReturnStmt(ReturnStmt returnStmt);
    protected abstract List<Stmt> caseEmptyStmt(EmptyStmt emptyStmt);
    protected abstract List<Stmt> caseForStmt(ForStmt forStmt);
    protected abstract List<Stmt> caseWhileStmt(WhileStmt whileStmt);
    protected abstract List<Stmt> caseTryStmt(TryStmt tryStmt);

    protected abstract List<Stmt> caseSwitchStmt(SwitchStmt switchStmt);
    protected abstract List<SwitchCaseBlock> caseSwitchCaseBlock(SwitchCaseBlock switchCaseBlock);
    protected abstract DefaultCaseBlock caseDefaultCaseBlock(DefaultCaseBlock defaultCaseBlock);

    protected abstract List<Stmt> caseIfStmt(IfStmt ifStmt);
    protected abstract List<IfBlock> caseIfBlock(IfBlock ifBlock);
    protected abstract ElseBlock caseElseBlock(ElseBlock elseBlock);

    protected abstract List<Stmt> caseSpmdStmt(SpmdStmt spmdStmt);
}

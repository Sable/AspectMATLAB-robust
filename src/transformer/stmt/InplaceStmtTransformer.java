package transformer.stmt;

import ast.*;
import transformer.InvalidExprTransformer;
import transformer.expr.InplaceExprTransformer;

import java.util.Collections;
import java.util.List;

public class InplaceStmtTransformer<T extends InplaceExprTransformer> extends AbstractStmtTransformer<T> {
    public InplaceStmtTransformer(T exprTransformer) {
        super(exprTransformer);
    }

    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<Stmt> caseExprStmt(ExprStmt exprStmt) {
        Expr transformedExpr = this.exprTransformer.transform(exprStmt.getExpr());
        exprStmt.setExpr(transformedExpr);
        return  Collections.singletonList(exprStmt);
    }

    @Override
    protected List<Stmt> caseAssignStmt(AssignStmt assignStmt) {
        Expr lhsTransformedExpr = this.exprTransformer.transform(assignStmt.getLHS());
        Expr rhsTransformedExpr = this.exprTransformer.transform(assignStmt.getRHS());

        assignStmt.setLHS(lhsTransformedExpr);
        assignStmt.setRHS(rhsTransformedExpr);

        return Collections.singletonList(assignStmt);
    }

    @Override
    protected List<Stmt> caseGlobalStmt(GlobalStmt globalStmt) {
        return Collections.singletonList(globalStmt);
    }

    @Override
    protected List<Stmt> casePersistentStmt(PersistentStmt persistentStmt) {
        return Collections.singletonList(persistentStmt);
    }

    @Override
    protected List<Stmt> caseShellCommandStmt(ShellCommandStmt shellCommandStmt) {
        return Collections.singletonList(shellCommandStmt);
    }

    @Override
    protected List<Stmt> caseBreakStmt(BreakStmt breakStmt) {
        return Collections.singletonList(breakStmt);
    }

    @Override
    protected List<Stmt> caseContinueStmt(ContinueStmt continueStmt) {
        return Collections.singletonList(continueStmt);
    }

    @Override
    protected List<Stmt> caseReturnStmt(ReturnStmt returnStmt) {
        return Collections.singletonList(returnStmt);
    }

    @Override
    protected List<Stmt> caseEmptyStmt(EmptyStmt emptyStmt) {
        return Collections.singletonList(emptyStmt);
    }

    @Override
    protected List<Stmt> caseForStmt(ForStmt forStmt) {
        List<Stmt> transformedAssignStmt = this.transform(forStmt.getAssignStmt());
        if (transformedAssignStmt.size() != 1 || !(transformedAssignStmt.get(0) instanceof AssignStmt)) {
            throw new InvalidExprTransformer(this.exprTransformer, forStmt.getAssignStmt());
        }
        assert transformedAssignStmt.size() == 1;
        assert transformedAssignStmt.get(0) instanceof AssignStmt;

        forStmt.setAssignStmt((AssignStmt) transformedAssignStmt.get(0));

        ast.List<Stmt> newStmtList = new ast.List<>();
        forStmt.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        forStmt.setStmtList(newStmtList);
        return Collections.singletonList(forStmt);
    }

    @Override
    protected List<Stmt> caseWhileStmt(WhileStmt whileStmt) {
        Expr transformedConditionExpr = this.exprTransformer.transform(whileStmt.getExpr());

        ast.List<Stmt> newStmtList = new ast.List<>();
        whileStmt.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        whileStmt.setExpr(transformedConditionExpr);
        whileStmt.setStmtList(newStmtList);

        return Collections.singletonList(whileStmt);
    }

    @Override
    protected List<Stmt> caseTryStmt(TryStmt tryStmt) {
        ast.List<Stmt> newTryStmtList = new ast.List<>();
        tryStmt.getTryStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newTryStmtList::addAll);

        ast.List<Stmt> newCatchStmtList = new ast.List<>();
        tryStmt.getCatchStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newCatchStmtList::addAll);

        tryStmt.setTryStmtList(newTryStmtList);
        tryStmt.setCatchStmtList(newCatchStmtList);

        return Collections.singletonList(tryStmt);
    }

    @Override
    protected List<Stmt> caseSwitchStmt(SwitchStmt switchStmt) {
        Expr transformedConditionExpr = this.exprTransformer.transform(switchStmt.getExpr());

        ast.List<SwitchCaseBlock> newCaseBlockList = new ast.List<>();
        switchStmt.getSwitchCaseBlockList().stream()
                .map(this::caseSwitchCaseBlock)
                .forEachOrdered(newCaseBlockList::addAll);

        if (switchStmt.hasDefaultCaseBlock()) {
            DefaultCaseBlock transformedDefaultBlock = this.caseDefaultCaseBlock(switchStmt.getDefaultCaseBlock());

            switchStmt.setExpr(transformedConditionExpr);
            switchStmt.setDefaultCaseBlock(transformedDefaultBlock);
            switchStmt.setSwitchCaseBlockList(newCaseBlockList);

            return Collections.singletonList(switchStmt);
        } else {
            switchStmt.setExpr(transformedConditionExpr);
            switchStmt.setSwitchCaseBlockList(newCaseBlockList);

            return Collections.singletonList(switchStmt);
        }
    }

    @Override
    protected List<SwitchCaseBlock> caseSwitchCaseBlock(SwitchCaseBlock switchCaseBlock) {
        Expr transformedConditionExpr = this.exprTransformer.transform(switchCaseBlock.getExpr());

        ast.List<Stmt> newStmtList = new ast.List<>();
        switchCaseBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        switchCaseBlock.setExpr(transformedConditionExpr);
        switchCaseBlock.setStmtList(newStmtList);

        return Collections.singletonList(switchCaseBlock);
    }

    @Override
    protected DefaultCaseBlock caseDefaultCaseBlock(DefaultCaseBlock defaultCaseBlock) {
        ast.List<Stmt> newStmtList = new ast.List<>();
        defaultCaseBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        defaultCaseBlock.setStmtList(newStmtList);

        return defaultCaseBlock;
    }

    @Override
    protected List<Stmt> caseIfStmt(IfStmt ifStmt) {
        ast.List<IfBlock> newIfBlockList = new ast.List<>();
        ifStmt.getIfBlockList().stream()
                .map(this::caseIfBlock)
                .forEachOrdered(newIfBlockList::addAll);

        if (ifStmt.hasElseBlock()) {
            ElseBlock transformedElseBlock = this.caseElseBlock(ifStmt.getElseBlock());

            ifStmt.setIfBlockList(newIfBlockList);
            ifStmt.setElseBlock(transformedElseBlock);

            return Collections.singletonList(ifStmt);
        } else {
            ifStmt.setIfBlockList(newIfBlockList);

            return Collections.singletonList(ifStmt);
        }
    }

    @Override
    protected List<IfBlock> caseIfBlock(IfBlock ifBlock) {
        Expr transformedConditionExpr = this.exprTransformer.transform(ifBlock.getCondition());

        ast.List<Stmt> newStmtList = new ast.List<>();
        ifBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        ifBlock.setCondition(transformedConditionExpr);
        ifBlock.setStmtList(newStmtList);

        return Collections.singletonList(ifBlock);
    }

    @Override
    protected ElseBlock caseElseBlock(ElseBlock elseBlock) {
        ast.List<Stmt> newStmtList = new ast.List<>();
        elseBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        elseBlock.setStmtList(newStmtList);

        return elseBlock;
    }

    @Override
    protected List<Stmt> caseSpmdStmt(SpmdStmt spmdStmt) {
        ast.List<Stmt> newStmtList = new ast.List<>();
        spmdStmt.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        if (spmdStmt.hasMinWorker()) {
            if (spmdStmt.hasMaxWorker()) {
                Expr transformedMinWorker = this.exprTransformer.transform(spmdStmt.getMinWorker());
                Expr transformedMaxWorker = this.exprTransformer.transform(spmdStmt.getMaxWorker());

                spmdStmt.setMinWorker(transformedMinWorker);
                spmdStmt.setMaxWorker(transformedMaxWorker);
                spmdStmt.setStmtList(newStmtList);

                return Collections.singletonList(spmdStmt);
            } else {
                Expr transformedMinWorker = this.exprTransformer.transform(spmdStmt.getMinWorker());

                spmdStmt.setMinWorker(transformedMinWorker);
                spmdStmt.setStmtList(newStmtList);

                return Collections.singletonList(spmdStmt);
            }
        } else {
            if (spmdStmt.hasMaxWorker()) {
                Expr transformedMaxWorker = this.exprTransformer.transform(spmdStmt.getMaxWorker());

                spmdStmt.setMaxWorker(transformedMaxWorker);
                spmdStmt.setStmtList(newStmtList);

                return Collections.singletonList(spmdStmt);
            } else {
                spmdStmt.setStmtList(newStmtList);

                return Collections.singletonList(spmdStmt);
            }
        }
    }
}

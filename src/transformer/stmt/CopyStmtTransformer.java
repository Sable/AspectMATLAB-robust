package transformer.stmt;

import ast.*;
import transformer.InvalidExprTransformer;
import transformer.expr.CopyExprTransformer;

import java.util.Collections;
import java.util.List;

public class CopyStmtTransformer<T extends CopyExprTransformer> extends AbstractStmtTransformer<T> {
    public CopyStmtTransformer(T exprTransformer) {
        super(exprTransformer);
    }

    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        return operand.copy();
    }

    @Override
    protected List<Stmt> caseExprStmt(ExprStmt exprStmt) {
        Expr copiedExpr = this.exprTransformer.transform(exprStmt.getExpr());

        ExprStmt copiedStmt = (ExprStmt) ASTNodeHandle(exprStmt);
        copiedStmt.setExpr(copiedExpr);

        return Collections.singletonList(copiedStmt);
    }

    @Override
    protected List<Stmt> caseAssignStmt(AssignStmt assignStmt) {
        Expr copiedLHSExpr = this.exprTransformer.transform(assignStmt.getLHS());
        Expr copiedRHSExpr = this.exprTransformer.transform(assignStmt.getRHS());

        AssignStmt copiedStmt = (AssignStmt) ASTNodeHandle(assignStmt);
        copiedStmt.setLHS(copiedLHSExpr);
        copiedStmt.setRHS(copiedRHSExpr);

        return Collections.singletonList(copiedStmt);
    }

    @Override
    protected List<Stmt> caseGlobalStmt(GlobalStmt globalStmt) {
        GlobalStmt copiedStmt = (GlobalStmt) ASTNodeHandle(globalStmt);

        ast.List<Name> copiedGlobalVarNameList = new ast.List<>();
        globalStmt.getNameList().stream()
                .map(name -> new Name(name.getID()))
                .forEachOrdered(copiedGlobalVarNameList::add);

        copiedStmt.setNameList(copiedGlobalVarNameList);

        return Collections.singletonList(copiedStmt);
    }

    @Override
    protected List<Stmt> casePersistentStmt(PersistentStmt persistentStmt) {
        PersistentStmt copiedStmt = (PersistentStmt) ASTNodeHandle(persistentStmt);

        ast.List<Name> copiedPersistentNameList = new ast.List<>();
        persistentStmt.getNameList().stream()
                .map(name -> new Name(name.getID()))
                .forEachOrdered(copiedPersistentNameList::add);

        copiedStmt.setNameList(copiedPersistentNameList);

        return Collections.singletonList(copiedStmt);
    }

    @Override
    protected List<Stmt> caseShellCommandStmt(ShellCommandStmt shellCommandStmt) {
        ShellCommandStmt copiedStmt = (ShellCommandStmt) ASTNodeHandle(shellCommandStmt);

        copiedStmt.setCommand(shellCommandStmt.getCommand());

        return Collections.singletonList(copiedStmt);
    }

    @Override
    protected List<Stmt> caseBreakStmt(BreakStmt breakStmt) {
        return Collections.singletonList((BreakStmt) ASTNodeHandle(breakStmt));
    }

    @Override
    protected List<Stmt> caseContinueStmt(ContinueStmt continueStmt) {
        return Collections.singletonList((ContinueStmt) ASTNodeHandle(continueStmt));
    }

    @Override
    protected List<Stmt> caseReturnStmt(ReturnStmt returnStmt) {
       return Collections.singletonList((ReturnStmt) ASTNodeHandle(returnStmt));
    }

    @Override
    protected List<Stmt> caseEmptyStmt(EmptyStmt emptyStmt) {
        return Collections.singletonList((EmptyStmt) ASTNodeHandle(emptyStmt));
    }

    @Override
    protected List<Stmt> caseForStmt(ForStmt forStmt) {
        List<Stmt> transformedAssignStmt = this.transform(forStmt.getAssignStmt());
        if (transformedAssignStmt.size() != 1 || !(transformedAssignStmt.get(0) instanceof AssignStmt)) {
            throw new InvalidExprTransformer(this.exprTransformer, forStmt.getAssignStmt());
        }
        assert transformedAssignStmt.size() == 1;
        assert transformedAssignStmt.get(0) instanceof AssignStmt;

        ast.List<Stmt> newStmtList = new ast.List<>();
        forStmt.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        ForStmt copiedStmt = (ForStmt) ASTNodeHandle(forStmt);
        copiedStmt.setAssignStmt((AssignStmt) transformedAssignStmt.get(0));
        copiedStmt.setStmtList(newStmtList);

        return Collections.singletonList(copiedStmt);
    }

    @Override
    protected List<Stmt> caseWhileStmt(WhileStmt whileStmt) {
        Expr transformedConditionExpr = this.exprTransformer.transform(whileStmt.getExpr());

        ast.List<Stmt> newStmtList = new ast.List<>();
        whileStmt.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        WhileStmt copiedStmt = (WhileStmt) ASTNodeHandle(whileStmt);
        copiedStmt.setExpr(transformedConditionExpr);
        copiedStmt.setStmtList(newStmtList);

        return Collections.singletonList(copiedStmt);
    }

    @Override
    protected List<Stmt> caseTryStmt(TryStmt tryStmt) {
        ast.List<Stmt> newTryStmtList = new ast.List<>();
        ast.List<Stmt> newCatchStmtList = new ast.List<>();

        tryStmt.getTryStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newTryStmtList::addAll);

        tryStmt.getCatchStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newCatchStmtList::addAll);

        if (tryStmt.hasCatchName()) {
            TryStmt copiedStmt = (TryStmt) ASTNodeHandle(tryStmt);
            copiedStmt.setCatchName(new Name(copiedStmt.getCatchName().getID()));
            copiedStmt.setTryStmtList(newTryStmtList);
            copiedStmt.setCatchStmtList(newCatchStmtList);

            return Collections.singletonList(copiedStmt);
        } else {
            TryStmt copiedStmt = (TryStmt) ASTNodeHandle(tryStmt);
            copiedStmt.setTryStmtList(newTryStmtList);
            copiedStmt.setCatchStmtList(newCatchStmtList);

            return Collections.singletonList(copiedStmt);
        }
    }

    @Override
    protected List<Stmt> caseSwitchStmt(SwitchStmt switchStmt) {
        Expr transformedSwitchExpr = this.exprTransformer.transform(switchStmt.getExpr());

        ast.List<SwitchCaseBlock> newCaseBlockList = new ast.List<>();
        switchStmt.getSwitchCaseBlockList().stream()
                .map(this::caseSwitchCaseBlock)
                .forEachOrdered(newCaseBlockList::addAll);

        if (switchStmt.hasDefaultCaseBlock()) {
            DefaultCaseBlock transformedDefaultCaseBlock = this.caseDefaultCaseBlock(switchStmt.getDefaultCaseBlock());

            SwitchStmt copiedStmt = (SwitchStmt) ASTNodeHandle(switchStmt);
            copiedStmt.setExpr(transformedSwitchExpr);
            copiedStmt.setSwitchCaseBlockList(newCaseBlockList);
            copiedStmt.setDefaultCaseBlock(transformedDefaultCaseBlock);

            return Collections.singletonList(copiedStmt);
        } else {
            SwitchStmt copiedStmt = (SwitchStmt) ASTNodeHandle(switchStmt);
            copiedStmt.setExpr(transformedSwitchExpr);
            copiedStmt.setSwitchCaseBlockList(newCaseBlockList);

            return Collections.singletonList(copiedStmt);
        }
    }

    @Override
    protected List<SwitchCaseBlock> caseSwitchCaseBlock(SwitchCaseBlock switchCaseBlock) {
        SwitchCaseBlock copiedBlock = (SwitchCaseBlock) ASTNodeHandle(switchCaseBlock);

        Expr copiedConditionExpr = this.exprTransformer.transform(copiedBlock.getExpr());
        copiedBlock.setExpr(copiedConditionExpr);
        ast.List<Stmt> newStmtList = new ast.List<>();
        switchCaseBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        copiedBlock.setStmtList(newStmtList);

        return  Collections.singletonList(copiedBlock);
    }

    @Override
    protected DefaultCaseBlock caseDefaultCaseBlock(DefaultCaseBlock defaultCaseBlock) {
        DefaultCaseBlock copiedBlock = (DefaultCaseBlock) ASTNodeHandle(defaultCaseBlock);

        ast.List<Stmt> newStmtList = new ast.List<>();
        defaultCaseBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        copiedBlock.setStmtList(newStmtList);

        return copiedBlock;
    }

    @Override
    protected List<Stmt> caseIfStmt(IfStmt ifStmt) {
        ast.List<IfBlock> newIfBlockList = new ast.List<>();
        ifStmt.getIfBlockList().stream()
                .map(this::caseIfBlock)
                .forEachOrdered(newIfBlockList::addAll);

        if (ifStmt.hasElseBlock()) {
            ElseBlock copiedElseBlock = this.caseElseBlock(ifStmt.getElseBlock());

            IfStmt copiedStmt = (IfStmt) ASTNodeHandle(ifStmt);
            copiedStmt.setIfBlockList(newIfBlockList);
            copiedStmt.setElseBlock(copiedElseBlock);

            return Collections.singletonList(copiedStmt);
        } else {
            IfStmt copiedStmt = (IfStmt) ASTNodeHandle(ifStmt);

            copiedStmt.setIfBlockList(newIfBlockList);

            return Collections.singletonList(copiedStmt);
        }
    }

    @Override
    protected List<IfBlock> caseIfBlock(IfBlock ifBlock) {
        Expr copiedConditionExpr = this.exprTransformer.transform(ifBlock.getCondition());

        ast.List<Stmt> newStmtList = new ast.List<>();
        ifBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        IfBlock copiedBlock = (IfBlock) ASTNodeHandle(ifBlock);

        copiedBlock.setCondition(copiedConditionExpr);
        copiedBlock.setStmtList(newStmtList);

        return Collections.singletonList(copiedBlock);
    }

    @Override
    protected ElseBlock caseElseBlock(ElseBlock elseBlock) {
        ast.List<Stmt> newStmtList = new ast.List<>();

        elseBlock.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        ElseBlock copiedBlock = (ElseBlock) ASTNodeHandle(elseBlock);

        copiedBlock.setStmtList(newStmtList);

        return copiedBlock;
    }

    @Override
    protected List<Stmt> caseSpmdStmt(SpmdStmt spmdStmt) {
        ast.List<Stmt> newStmtList = new ast.List<>();

        spmdStmt.getStmtList().stream()
                .map(this::transform)
                .forEachOrdered(newStmtList::addAll);

        if (spmdStmt.hasMinWorker()) {
            if (spmdStmt.hasMaxWorker()) {
                Expr copiedMinWorkerExpr = this.exprTransformer.transform(spmdStmt.getMinWorker());
                Expr copiedMaxWorkerExpr = this.exprTransformer.transform(spmdStmt.getMaxWorker());

                SpmdStmt copiedStmt = (SpmdStmt) ASTNodeHandle(spmdStmt);
                copiedStmt.setMaxWorker(copiedMaxWorkerExpr);
                copiedStmt.setMinWorker(copiedMinWorkerExpr);
                copiedStmt.setStmtList(newStmtList);

                return Collections.singletonList(copiedStmt);
            } else {
                Expr copiedMinWorkerExpr = this.exprTransformer.transform(spmdStmt.getMinWorker());

                SpmdStmt copiedStmt = (SpmdStmt) ASTNodeHandle(spmdStmt);
                copiedStmt.setMinWorker(copiedMinWorkerExpr);
                copiedStmt.setStmtList(newStmtList);

                return Collections.singletonList(copiedStmt);
            }
        } else {
            if (spmdStmt.hasMaxWorker()) {
                Expr copiedMaxWorkerExpr = this.exprTransformer.transform(spmdStmt.getMaxWorker());

                SpmdStmt copiedStmt = (SpmdStmt) ASTNodeHandle(spmdStmt);
                copiedStmt.setMaxWorker(copiedMaxWorkerExpr);
                copiedStmt.setStmtList(newStmtList);

                return Collections.singletonList(copiedStmt);
            } else {
                SpmdStmt copiedStmt = (SpmdStmt) ASTNodeHandle(spmdStmt);
                copiedStmt.setStmtList(newStmtList);

                return Collections.singletonList(copiedStmt);
            }
        }
    }
}

package transformer.aspect;

import ast.*;
import ast.List;
import joinpoint.AMJoinPoint;
import natlab.toolkits.analysis.varorfun.VFAnalysis;
import transformer.expr.CopyExprTransformer;
import utils.codeGen.AMTempVarGenerator;
import utils.codeGen.builders.AssignStmtBuilder;

import java.util.*;
import java.util.function.Supplier;

public final class AspectExprTransformer extends CopyExprTransformer {
    private final AMTempVarGenerator AMTempVar = new AMTempVarGenerator("AM_EXPR_");
    private LinkedList<Stmt> prefixStmtList = new LinkedList<>();
    private LinkedList<Stmt> suffixStmtList = new LinkedList<>();
    private Set<AMJoinPoint> joinPointSet = new HashSet<>();

    private VFAnalysis vfAnalysis = null; // TODO

    private Set<abstractPattern.Action> hasTransformationAt(ASTNode node) {  // TODO
        HashSet<abstractPattern.Action> retSet = new HashSet<>();
        retSet.add(null);

        return retSet;
    }

    private Set<abstractPattern.Action> hasTransformationFrom(ASTNode node) {
        HashSet<abstractPattern.Action> retSet = new HashSet<>();
        Set<abstractPattern.Action> currentNodeTransformSet = hasTransformationAt(node);
        retSet.addAll(currentNodeTransformSet);
        for (int childIndex = 0; childIndex < node.getNumChild(); childIndex++) {
            ASTNode childNode = node.getChild(childIndex);
            Set<abstractPattern.Action> childTransformSet = hasTransformationFrom(childNode);
            retSet.addAll(childTransformSet);
        }
        return Collections.unmodifiableSet(retSet);
    }

    private boolean isOrderSensitive(ASTNode node) {
        return true;
    }

    public java.util.List<Stmt> getPrefixStmtList() {
        return Collections.unmodifiableList(prefixStmtList);
    }

    public java.util.List<Stmt> getSuffixStmtList() {
        return Collections.unmodifiableList(suffixStmtList);
    }

    // Short circuit expression -> make sure the evaluation order is correct

    @Override
    protected Expr caseShortCircuitAndExpr(ShortCircuitAndExpr shortCircuitAndExpr) {
        if (!hasTransformationFrom(shortCircuitAndExpr.getRHS()).isEmpty()) {
            Expr lhsExpr = this.transform(shortCircuitAndExpr.getLHS());
            String evaluatingName = AMTempVar.next();
            AssignStmt evaluatingLHSAssignStmt = new AssignStmtBuilder()
                    .setLHS(evaluatingName)
                    .setRHS(lhsExpr)
                    .setOutputSuppressed(true)
                    .build();
            prefixStmtList.add(evaluatingLHSAssignStmt);

            LinkedList<Stmt> prefixStmtListRetain = new LinkedList<>(prefixStmtList);
            LinkedList<Stmt> suffixStmtListRetain = new LinkedList<>(suffixStmtList);

            Expr rhsExpr = this.transform(shortCircuitAndExpr.getRHS());
            IfBlock rhsIfBlock = new IfBlock();
            rhsIfBlock.setCondition(new NameExpr(new Name(evaluatingName)));
            prefixStmtList.stream()
                    .filter(statement -> !prefixStmtListRetain.contains(statement))
                    .forEachOrdered(rhsIfBlock::addStmt);
            AssignStmt evaluatingRHSAssignStmt = new AssignStmtBuilder()
                    .setLHS(evaluatingName)
                    .setRHS(rhsExpr)
                    .setOutputSuppressed(true)
                    .build();
            rhsIfBlock.addStmt(evaluatingRHSAssignStmt);
            suffixStmtList.stream()
                    .filter(statement -> !suffixStmtListRetain.contains(statement))
                    .forEachOrdered(rhsIfBlock::addStmt);

            prefixStmtList = prefixStmtListRetain;
            suffixStmtList = suffixStmtListRetain;

            prefixStmtList.add(new IfStmt(new List<>(rhsIfBlock), new Opt<>()));

            return new NameExpr(new Name(evaluatingName));
        } else {
            return super.caseShortCircuitAndExpr(shortCircuitAndExpr);
        }
    }

    @Override
    protected Expr caseShortCircuitOrExpr(ShortCircuitOrExpr shortCircuitOrExpr) {
        if (!hasTransformationFrom(shortCircuitOrExpr.getRHS()).isEmpty()) {
            Expr lhsExpr = this.transform(shortCircuitOrExpr.getLHS());
            String evaluatingName = AMTempVar.next();
            AssignStmt evaluatingLHSAssignStmt = new AssignStmtBuilder()
                    .setLHS(evaluatingName)
                    .setRHS(lhsExpr)
                    .setOutputSuppressed(true)
                    .build();
            prefixStmtList.add(evaluatingLHSAssignStmt);

            LinkedList<Stmt> prefixStmtListRetain = new LinkedList<>(prefixStmtList);
            LinkedList<Stmt> suffixStmtListRetain = new LinkedList<>(suffixStmtList);

            Expr rhsExpr = this.transform(shortCircuitOrExpr.getRHS());
            IfBlock rhsIfBlock = new IfBlock();
            rhsIfBlock.setCondition(new NotExpr(new NameExpr(new Name(evaluatingName))));
            prefixStmtList.stream()
                    .filter(statement -> !prefixStmtListRetain.contains(statement))
                    .forEachOrdered(rhsIfBlock::addStmt);
            AssignStmt evaluatingRHSAssignStmt = new AssignStmtBuilder()
                    .setLHS(evaluatingName)
                    .setRHS(rhsExpr)
                    .setOutputSuppressed(true)
                    .build();
            suffixStmtList.stream()
                    .filter(statement -> !suffixStmtListRetain.contains(statement))
                    .forEachOrdered(rhsIfBlock::addStmt);

            prefixStmtList = prefixStmtListRetain;
            suffixStmtList = suffixStmtListRetain;

            prefixStmtList.add(new IfStmt(new List<>(rhsIfBlock), new Opt<>()));

            return new NameExpr(new Name(evaluatingName));
        } else {
            return super.caseShortCircuitOrExpr(shortCircuitOrExpr);
        }
    }

    // Operator Pattern

    private Expr binaryExprTransform(BinaryExpr binaryExpr, Supplier<? extends BinaryExpr> subExprSupplier) {
        if (!hasTransformationAt(binaryExpr).isEmpty()) {
            Expr lhsExpr = this.transform(binaryExpr.getLHS());
            String lhsTempVarName = AMTempVar.next();
            AssignStmt lhsPrefixAssignStmt = new AssignStmtBuilder()
                    .setLHS(lhsTempVarName)
                    .setRHS(lhsExpr)
                    .setOutputSuppressed(true)
                    .build();
            prefixStmtList.add(lhsPrefixAssignStmt);

            Expr rhsExpr = this.transform(binaryExpr.getRHS());
            String rhsTempVarName = AMTempVar.next();
            AssignStmt rhsPrefixAssignStmt = new AssignStmtBuilder()
                    .setLHS(rhsTempVarName)
                    .setRHS(rhsExpr)
                    .setOutputSuppressed(true)
                    .build();
            prefixStmtList.add(rhsPrefixAssignStmt);

            String resultTempVarName = AMTempVar.next();
            BinaryExpr evaluatingExpr = subExprSupplier.get();
            evaluatingExpr.setLHS(new NameExpr(new Name(lhsTempVarName)));
            evaluatingExpr.setRHS(new NameExpr(new Name(rhsTempVarName)));
            AssignStmt evaluatingStmt = new AssignStmtBuilder()
                    .setLHS(resultTempVarName)
                    .setRHS(evaluatingExpr)
                    .setOutputSuppressed(true)
                    .build();
            prefixStmtList.add(evaluatingStmt);

            return new NameExpr(new Name(resultTempVarName));
        } else {
            if (binaryExpr instanceof PlusExpr)   return super.casePlusExpr(((PlusExpr) binaryExpr));
            if (binaryExpr instanceof MinusExpr)  return super.caseMinusExpr(((MinusExpr) binaryExpr));
            if (binaryExpr instanceof MTimesExpr) return super.caseMTimesExpr(((MTimesExpr) binaryExpr));
            if (binaryExpr instanceof MDivExpr)   return super.caseMDivExpr(((MDivExpr) binaryExpr));
            if (binaryExpr instanceof MLDivExpr)  return super.caseMLDivExpr(((MLDivExpr) binaryExpr));
            if (binaryExpr instanceof MPowExpr)   return super.caseMPowExpr(((MPowExpr) binaryExpr));

            if (binaryExpr instanceof ETimesExpr) return super.caseETimesExpr(((ETimesExpr) binaryExpr));
            if (binaryExpr instanceof EDivExpr)   return super.caseEDivExpr(((EDivExpr) binaryExpr));
            if (binaryExpr instanceof ELDivExpr)  return super.caseELDivExpr(((ELDivExpr) binaryExpr));
            if (binaryExpr instanceof EPowExpr)   return super.caseEPowExpr(((EPowExpr) binaryExpr));

            if (binaryExpr instanceof AndExpr)    return super.caseAndExpr(((AndExpr) binaryExpr));
            if (binaryExpr instanceof OrExpr)     return super.caseOrExpr(((OrExpr) binaryExpr));
            if (binaryExpr instanceof ShortCircuitAndExpr) {
                return this.caseShortCircuitAndExpr(((ShortCircuitAndExpr) binaryExpr));
            }
            if (binaryExpr instanceof ShortCircuitOrExpr) {
                return this.caseShortCircuitOrExpr(((ShortCircuitOrExpr) binaryExpr));
            }

            if (binaryExpr instanceof LTExpr)     return super.caseLTExpr(((LTExpr) binaryExpr));
            if (binaryExpr instanceof GTExpr)     return super.caseGTExpr(((GTExpr) binaryExpr));
            if (binaryExpr instanceof LEExpr)     return super.caseLEExpr(((LEExpr) binaryExpr));
            if (binaryExpr instanceof GEExpr)     return super.caseGEExpr(((GEExpr) binaryExpr));
            if (binaryExpr instanceof EQExpr)     return super.caseEQExpr(((EQExpr) binaryExpr));
            if (binaryExpr instanceof NEExpr)     return super.caseNEExpr(((NEExpr) binaryExpr));

            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    private Expr unaryExprTransform(UnaryExpr unaryExpr, Supplier<? extends UnaryExpr> subExprSupplier) {
        if (!hasTransformationAt(unaryExpr).isEmpty()) {
            Expr operandExpr = this.transform(unaryExpr.getOperand());
            String operandTempVarName = AMTempVar.next();
            AssignStmt operandPrefixAssignStmt = new AssignStmtBuilder()
                    .setLHS(operandTempVarName)
                    .setRHS(operandExpr)
                    .setOutputSuppressed(true)
                    .build();
            prefixStmtList.add(operandPrefixAssignStmt);

            String resultTempVarName = AMTempVar.next();
            UnaryExpr evaluatingExpr = subExprSupplier.get();
            evaluatingExpr.setOperand(new NameExpr(new Name(operandTempVarName)));
            AssignStmt evaluatingStmt = new AssignStmtBuilder()
                    .setLHS(resultTempVarName)
                    .setRHS(evaluatingExpr)
                    .setOutputSuppressed(true)
                    .build();
            prefixStmtList.add(evaluatingStmt);

            return new NameExpr(new Name(resultTempVarName));
        } else {
            if (unaryExpr instanceof UMinusExpr)     return super.caseUMinusExpr(((UMinusExpr) unaryExpr));
            if (unaryExpr instanceof UPlusExpr)      return super.caseUPlusExpr(((UPlusExpr) unaryExpr));
            if (unaryExpr instanceof NotExpr)        return super.caseNotExpr(((NotExpr) unaryExpr));
            if (unaryExpr instanceof MTransposeExpr) return super.caseMTransposeExpr(((MTransposeExpr) unaryExpr));
            if (unaryExpr instanceof ArrayTransposeExpr) {
                return super.caseArrayTransposeExpr(((ArrayTransposeExpr) unaryExpr));
            }

            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    @Override
    protected Expr casePlusExpr(PlusExpr plusExpr) {
        return binaryExprTransform(plusExpr, PlusExpr::new);
    }

    @Override
    protected Expr caseMinusExpr(MinusExpr minusExpr) {
        return binaryExprTransform(minusExpr, MinusExpr::new);
    }

    @Override
    protected Expr caseMTimesExpr(MTimesExpr mTimesExpr) {
        return binaryExprTransform(mTimesExpr, MTimesExpr::new);
    }

    @Override
    protected Expr caseETimesExpr(ETimesExpr eTimesExpr) {
        return binaryExprTransform(eTimesExpr, ETimesExpr::new);
    }

    @Override
    protected Expr caseMDivExpr(MDivExpr mDivExpr) {
        return binaryExprTransform(mDivExpr, MDivExpr::new);
    }

    @Override
    protected Expr caseEDivExpr(EDivExpr eDivExpr) {
        return binaryExprTransform(eDivExpr, EDivExpr::new);
    }

    @Override
    protected Expr caseMLDivExpr(MLDivExpr mlDivExpr) {
        return binaryExprTransform(mlDivExpr, MLDivExpr::new);
    }

    @Override
    protected Expr caseELDivExpr(ELDivExpr elDivExpr) {
        return binaryExprTransform(elDivExpr, ELDivExpr::new);
    }

    @Override
    protected Expr caseMPowExpr(MPowExpr mPowExpr) {
        return binaryExprTransform(mPowExpr, MPowExpr::new);
    }

    @Override
    protected Expr caseEPowExpr(EPowExpr ePowExpr) {
        return binaryExprTransform(ePowExpr, EPowExpr::new);
    }

    @Override
    protected Expr caseMTransposeExpr(MTransposeExpr mTransposeExpr) {
        return unaryExprTransform(mTransposeExpr, MTransposeExpr::new);
    }

    @Override
    protected Expr caseArrayTransposeExpr(ArrayTransposeExpr arrayTransposeExpr) {
        return unaryExprTransform(arrayTransposeExpr, ArrayTransposeExpr::new);
    }
}

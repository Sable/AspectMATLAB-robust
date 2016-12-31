package aspectMATLAB.transformer.expr;

import ast.*;
import aspectMATLAB.transformer.ASTNodeTransformer;

public abstract class AbstractExprTransformer implements ASTNodeTransformer<Expr, Expr> {
    protected abstract ASTNode ASTNodeHandle(ASTNode operand);

    @Override
    public Expr transform(Expr target) {
        if (isLiteralExpr(target)) {
            return caseLiteralExpr(((LiteralExpr) target));
        } else if (isLValueExpr(target)) {
            return caseLValueExpr(((LValueExpr) target));
        } else if (isUnaryExpr(target)) {
            return caseUnaryExpr(((UnaryExpr) target));
        } else if (isBinaryExpr(target)) {
            return caseBinaryExpr(((BinaryExpr) target));
        } else if (target instanceof RangeExpr) {
            return caseRangeExpr(((RangeExpr) target));
        } else if (target instanceof ColonExpr) {
            return caseColonExpr(((ColonExpr) target));
        } else if (target instanceof EndExpr) {
            return caseEndExpr(((EndExpr) target));
        } else if (target instanceof FunctionHandleExpr) {
            return caseFunctionHandleExpr(((FunctionHandleExpr) target));
        } else if (target instanceof LambdaExpr) {
            return caseLambdaExpr(((LambdaExpr) target));
        } else if (target instanceof CellArrayExpr) {
            return caseCellArrayExpr(((CellArrayExpr) target));
        } else if (target instanceof SuperClassMethodExpr) {
            return caseSuperClassMethodExpr(((SuperClassMethodExpr) target));
        } else {
            /* control flow should not reach hAere */
            throw new AssertionError();
        }
    }

    public static boolean isExpr(ASTNode astNode) {
        if (isLiteralExpr(astNode)) return true;
        if (isLValueExpr(astNode)) return true;
        if (isUnaryExpr(astNode)) return true;
        if (isBinaryExpr(astNode)) return true;
        if (astNode instanceof RangeExpr) return true;
        if (astNode instanceof ColonExpr) return true;
        if (astNode instanceof EndExpr) return true;
        if (astNode instanceof FunctionHandleExpr) return true;
        if (astNode instanceof LambdaExpr) return true;
        if (astNode instanceof CellArrayExpr) return true;
        if (astNode instanceof SuperClassMethodExpr) return true;
        return false;
    }

    protected abstract Expr caseRangeExpr(RangeExpr rangeExpr);
    protected abstract Expr caseColonExpr(ColonExpr colonExpr);
    protected abstract Expr caseEndExpr(EndExpr endExpr);
    protected abstract Expr caseFunctionHandleExpr(FunctionHandleExpr functionHandleExpr);
    protected abstract Expr caseLambdaExpr(LambdaExpr lambdaExpr);
    protected abstract Expr caseCellArrayExpr(CellArrayExpr cellArrayExpr);
    protected abstract Expr caseSuperClassMethodExpr(SuperClassMethodExpr superClassMethodExpr);

    protected Expr caseLValueExpr(LValueExpr lValueExpr) {
        if (lValueExpr instanceof NameExpr) {
            return caseNameExpr(((NameExpr) lValueExpr));
        } else if (lValueExpr instanceof ParameterizedExpr) {
            return caseParameterizedExpr(((ParameterizedExpr) lValueExpr));
        } else if (lValueExpr instanceof CellIndexExpr) {
            return caseCellIndexExpr(((CellIndexExpr) lValueExpr));
        } else if (lValueExpr instanceof DotExpr) {
            return caseDotExpr(((DotExpr) lValueExpr));
        } else if (lValueExpr instanceof MatrixExpr) {
            return caseMatrixExpr(((MatrixExpr) lValueExpr));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    public static boolean isLValueExpr(ASTNode astNode) {
        if (astNode instanceof NameExpr) return true;
        if (astNode instanceof ParameterizedExpr) return true;
        if (astNode instanceof CellIndexExpr) return true;
        if (astNode instanceof DotExpr) return true;
        if (astNode instanceof MatrixExpr) return true;
        return false;
    }

    protected abstract Expr caseNameExpr(NameExpr nameExpr);
    protected abstract Expr caseParameterizedExpr(ParameterizedExpr parameterizedExpr);
    protected abstract Expr caseCellIndexExpr(CellIndexExpr cellIndexExpr);
    protected abstract Expr caseDotExpr(DotExpr dotExpr);
    protected abstract Expr caseMatrixExpr(MatrixExpr matrixExpr);

    protected Expr caseLiteralExpr(LiteralExpr literalExpr) {
        if (literalExpr instanceof IntLiteralExpr) {
            return caseIntLiteralExpr(((IntLiteralExpr) literalExpr));
        } else if (literalExpr instanceof FPLiteralExpr) {
            return caseFPLiteralExpr(((FPLiteralExpr) literalExpr));
        } else if (literalExpr instanceof StringLiteralExpr) {
            return caseStringLiteralExpr(((StringLiteralExpr) literalExpr));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    public static boolean isLiteralExpr(ASTNode astNode) {
        if (astNode instanceof IntLiteralExpr) return true;
        if (astNode instanceof FPLiteralExpr) return true;
        if (astNode instanceof StringLiteralExpr) return true;
        return false;
    }

    protected abstract Expr caseIntLiteralExpr(IntLiteralExpr intLiteralExpr);
    protected abstract Expr caseFPLiteralExpr(FPLiteralExpr fpLiteralExpr);
    protected abstract Expr caseStringLiteralExpr(StringLiteralExpr stringLiteralExpr);

    protected Expr caseUnaryExpr(UnaryExpr unaryExpr) {
        if (unaryExpr instanceof UMinusExpr) {
            return caseUMinusExpr(((UMinusExpr) unaryExpr));
        } else if (unaryExpr instanceof UPlusExpr) {
            return caseUPlusExpr(((UPlusExpr) unaryExpr));
        } else if (unaryExpr instanceof NotExpr) {
            return caseNotExpr(((NotExpr) unaryExpr));
        } else if (unaryExpr instanceof MTransposeExpr) {
            return caseMTransposeExpr(((MTransposeExpr) unaryExpr));
        } else if (unaryExpr instanceof ArrayTransposeExpr) {
            return caseArrayTransposeExpr(((ArrayTransposeExpr) unaryExpr));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    public static boolean isUnaryExpr(ASTNode astNode) {
        if (astNode instanceof UMinusExpr) return true;
        if (astNode instanceof UPlusExpr) return true;
        if (astNode instanceof NotExpr) return true;
        if (astNode instanceof MTransposeExpr) return true;
        if (astNode instanceof ArrayTransposeExpr) return true;
        return false;
    }

    protected abstract Expr caseUMinusExpr(UMinusExpr uMinusExpr);
    protected abstract Expr caseUPlusExpr(UPlusExpr uPlusExpr);
    protected abstract Expr caseNotExpr(NotExpr notExpr);
    protected abstract Expr caseMTransposeExpr(MTransposeExpr mTransposeExpr);
    protected abstract Expr caseArrayTransposeExpr(ArrayTransposeExpr arrayTransposeExpr);

    protected Expr caseBinaryExpr(BinaryExpr binaryExpr) {
        if (binaryExpr instanceof PlusExpr) {
            return casePlusExpr(((PlusExpr) binaryExpr));
        } else if (binaryExpr instanceof MinusExpr) {
            return caseMinusExpr(((MinusExpr) binaryExpr));
        } else if (binaryExpr instanceof MTimesExpr) {
            return caseMTimesExpr(((MTimesExpr) binaryExpr));
        } else if (binaryExpr instanceof MDivExpr) {
            return caseMDivExpr(((MDivExpr) binaryExpr));
        } else if (binaryExpr instanceof MLDivExpr) {
            return caseMLDivExpr(((MLDivExpr) binaryExpr));
        } else if (binaryExpr instanceof MPowExpr) {
            return caseMPowExpr(((MPowExpr) binaryExpr));
        } else if (binaryExpr instanceof ETimesExpr) {
            return caseETimesExpr(((ETimesExpr) binaryExpr));
        } else if (binaryExpr instanceof EDivExpr) {
            return caseEDivExpr(((EDivExpr) binaryExpr));
        } else if (binaryExpr instanceof ELDivExpr) {
            return caseELDivExpr(((ELDivExpr) binaryExpr));
        } else if (binaryExpr instanceof EPowExpr) {
            return caseEPowExpr(((EPowExpr) binaryExpr));
        } else if (binaryExpr instanceof AndExpr) {
            return caseAndExpr(((AndExpr) binaryExpr));
        } else if (binaryExpr instanceof OrExpr) {
            return caseOrExpr(((OrExpr) binaryExpr));
        } else if (binaryExpr instanceof ShortCircuitAndExpr) {
            return caseShortCircuitAndExpr(((ShortCircuitAndExpr) binaryExpr));
        } else if (binaryExpr instanceof ShortCircuitOrExpr) {
            return caseShortCircuitOrExpr(((ShortCircuitOrExpr) binaryExpr));
        } else if (binaryExpr instanceof LTExpr) {
            return caseLTExpr(((LTExpr) binaryExpr));
        } else if (binaryExpr instanceof GTExpr) {
            return caseGTExpr(((GTExpr) binaryExpr));
        } else if (binaryExpr instanceof LEExpr) {
            return caseLEExpr(((LEExpr) binaryExpr));
        } else if (binaryExpr instanceof GEExpr) {
            return caseGEExpr(((GEExpr) binaryExpr));
        } else if (binaryExpr instanceof EQExpr) {
            return caseEQExpr(((EQExpr) binaryExpr));
        } else if (binaryExpr instanceof NEExpr) {
            return caseNEExpr(((NEExpr) binaryExpr));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    public static boolean isBinaryExpr(ASTNode astNode) {
        if (astNode instanceof PlusExpr) return true;
        if (astNode instanceof MinusExpr) return true;
        if (astNode instanceof MTimesExpr) return true;
        if (astNode instanceof MDivExpr) return true;
        if (astNode instanceof MLDivExpr) return true;
        if (astNode instanceof MPowExpr) return true;
        if (astNode instanceof AndExpr) return true;
        if (astNode instanceof OrExpr) return true;
        if (astNode instanceof ShortCircuitAndExpr) return true;
        if (astNode instanceof ShortCircuitOrExpr) return true;
        if (astNode instanceof LTExpr) return true;
        if (astNode instanceof GTExpr) return true;
        if (astNode instanceof LEExpr) return true;
        if (astNode instanceof GEExpr) return true;
        if (astNode instanceof EQExpr) return true;
        if (astNode instanceof NEExpr) return true;
        return false;
    }

    protected abstract Expr casePlusExpr(PlusExpr plusExpr);
    protected abstract Expr caseMinusExpr(MinusExpr minusExpr);
    protected abstract Expr caseMTimesExpr(MTimesExpr mTimesExpr);
    protected abstract Expr caseMDivExpr(MDivExpr mDivExpr);
    protected abstract Expr caseMLDivExpr(MLDivExpr mlDivExpr);
    protected abstract Expr caseMPowExpr(MPowExpr mPowExpr);
    protected abstract Expr caseETimesExpr(ETimesExpr eTimesExpr);
    protected abstract Expr caseEDivExpr(EDivExpr eDivExpr);
    protected abstract Expr caseELDivExpr(ELDivExpr elDivExpr);
    protected abstract Expr caseEPowExpr(EPowExpr ePowExpr);
    protected abstract Expr caseAndExpr(AndExpr andExpr);
    protected abstract Expr caseOrExpr(OrExpr orExpr);
    protected abstract Expr caseShortCircuitAndExpr(ShortCircuitAndExpr shortCircuitAndExpr);
    protected abstract Expr caseShortCircuitOrExpr(ShortCircuitOrExpr shortCircuitOrExpr);
    protected abstract Expr caseLTExpr(LTExpr ltExpr);
    protected abstract Expr caseGTExpr(GTExpr gtExpr);
    protected abstract Expr caseLEExpr(LEExpr leExpr);
    protected abstract Expr caseGEExpr(GEExpr geExpr);
    protected abstract Expr caseEQExpr(EQExpr eqExpr);
    protected abstract Expr caseNEExpr(NEExpr neExpr);
}

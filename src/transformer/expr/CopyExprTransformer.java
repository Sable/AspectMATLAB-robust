package transformer.expr;

import ast.*;

public class CopyExprTransformer extends AbstractExprTransformer {
    @Override
    protected Expr caseRangeExpr(RangeExpr rangeExpr) {
        if (rangeExpr.hasIncr()) {
            Expr lowerExpr = this.transform(rangeExpr.getLower());
            Expr incrExpr  = this.transform(rangeExpr.getIncr());
            Expr upperExpr = this.transform(rangeExpr.getUpper());

            RangeExpr retExpr = rangeExpr.copy();
            retExpr.setLower(lowerExpr);
            retExpr.setIncr(incrExpr);
            retExpr.setUpper(upperExpr);

            return retExpr;
        } else {
            Expr lowerExpr = this.transform(rangeExpr.getLower());
            Expr upperExpr = this.transform(rangeExpr.getUpper());

            RangeExpr retExpr = rangeExpr.copy();
            retExpr.setLower(lowerExpr);
            retExpr.setUpper(upperExpr);

            return retExpr;
        }
    }

    @Override
    protected Expr caseColonExpr(ColonExpr colonExpr) {
        return colonExpr.treeCopy();
    }

    @Override
    protected Expr caseEndExpr(EndExpr endExpr) {
        return endExpr.treeCopy();
    }

    @Override
    protected Expr caseFunctionHandleExpr(FunctionHandleExpr functionHandleExpr) {
        return functionHandleExpr.treeCopy();
    }

    @Override
    protected Expr caseLambdaExpr(LambdaExpr lambdaExpr) {
        return lambdaExpr.treeCopy();
    }

    @Override
    protected Expr caseCellArrayExpr(CellArrayExpr cellArrayExpr) {
        CellArrayExpr retExpr = cellArrayExpr.copy();
        for (int rowIndex = 0; rowIndex < retExpr.getNumRow(); rowIndex++) {
            Row copiedRow = cellArrayExpr.getRow(rowIndex).copy();
            for (int elementIndex = 0; elementIndex < cellArrayExpr.getRow(rowIndex).getNumElement(); elementIndex++) {
                Expr copiedElement = this.transform(cellArrayExpr.getRow(rowIndex).getElement(elementIndex));
                copiedRow.setElement(copiedElement, elementIndex);
            }
            retExpr.setRow(copiedRow, rowIndex);
        }
        return retExpr;
    }

    @Override
    protected Expr caseSuperClassMethodExpr(SuperClassMethodExpr superClassMethodExpr) {
        return superClassMethodExpr.treeCopy();
    }

    @Override
    protected Expr caseNameExpr(NameExpr nameExpr) {
        return nameExpr.treeCopy();
    }

    @Override
    protected Expr caseParameterizedExpr(ParameterizedExpr parameterizedExpr) {
        ParameterizedExpr retExpr = parameterizedExpr.copy();

        Expr copiedTargetExpr = this.transform(parameterizedExpr.getTarget());
        retExpr.setTarget(copiedTargetExpr);
        for (int parameterIndex = 0; parameterIndex < parameterizedExpr.getNumArg(); parameterIndex++) {
            Expr copiedParameter = this.transform(parameterizedExpr.getArg(parameterIndex));
            retExpr.setArg(copiedParameter, parameterIndex);
        }

        return retExpr;
    }

    @Override
    protected Expr caseCellIndexExpr(CellIndexExpr cellIndexExpr) {
        CellIndexExpr retExpr = cellIndexExpr.copy();

        Expr copiedTarget = this.transform(cellIndexExpr.getTarget());
        retExpr.setTarget(copiedTarget);
        for (int parameterIndex = 0; parameterIndex < cellIndexExpr.getNumArg(); parameterIndex++) {
            Expr copiedParameter = this.transform(cellIndexExpr.getArg(parameterIndex));
            retExpr.setArg(copiedParameter, parameterIndex);
        }

        return retExpr;
    }

    @Override
    protected Expr caseDotExpr(DotExpr dotExpr) {
        DotExpr retExpr = dotExpr.copy();

        Expr copiedTarget = this.transform(dotExpr.getTarget());
        retExpr.setTarget(copiedTarget);

        return retExpr;
    }

    @Override
    protected Expr caseMatrixExpr(MatrixExpr matrixExpr) {
        MatrixExpr retExpr = matrixExpr.copy();

        for (int rowIndex = 0; rowIndex < matrixExpr.getNumRow(); rowIndex++) {
            Row copiedRow = retExpr.getRow(rowIndex).copy();
            for (int elementIndex = 0; elementIndex < matrixExpr.getRow(rowIndex).getNumElement(); elementIndex++) {
                Expr copiedElement = this.transform(matrixExpr.getRow(rowIndex).getElement(elementIndex));
                copiedRow.setElement(copiedElement, elementIndex);
            }
            retExpr.setRow(copiedRow, rowIndex);
        }

        return retExpr;
    }

    @Override
    protected Expr caseIntLiteralExpr(IntLiteralExpr intLiteralExpr) {
        return intLiteralExpr.treeCopy();
    }

    @Override
    protected Expr caseFPLiteralExpr(FPLiteralExpr fpLiteralExpr) {
        return fpLiteralExpr.treeCopy();
    }

    @Override
    protected Expr caseStringLiteralExpr(StringLiteralExpr stringLiteralExpr) {
        return stringLiteralExpr.treeCopy();
    }

    private final UnaryExpr unaryOperatorDispatch(UnaryExpr unaryExpr) {
        UnaryExpr retExpr = (UnaryExpr) unaryExpr.copy();
        Expr copiedOperand = this.transform(unaryExpr.getOperand());
        retExpr.setOperand(copiedOperand);
        return retExpr;
    }

    @Override
    protected Expr caseUMinusExpr(UMinusExpr uMinusExpr) {
        return unaryOperatorDispatch(uMinusExpr);
    }

    @Override
    protected Expr caseUPlusExpr(UPlusExpr uPlusExpr) {
        return unaryOperatorDispatch(uPlusExpr);
    }

    @Override
    protected Expr caseNotExpr(NotExpr notExpr) {
        return unaryOperatorDispatch(notExpr);
    }

    @Override
    protected Expr caseMTransposeExpr(MTransposeExpr mTransposeExpr) {
        return unaryOperatorDispatch(mTransposeExpr);
    }

    @Override
    protected Expr caseArrayTransposeExpr(ArrayTransposeExpr arrayTransposeExpr) {
        return unaryOperatorDispatch(arrayTransposeExpr);
    }

    private final BinaryExpr binaryOperatorDispatch(BinaryExpr binaryExpr) {
        BinaryExpr retExpr = (BinaryExpr) binaryExpr.copy();
        Expr copiedLHS = this.transform(binaryExpr.getLHS());
        Expr copiedRHS = this.transform(binaryExpr.getRHS());
        retExpr.setLHS(copiedLHS);
        retExpr.setRHS(copiedRHS);
        return retExpr;
    }

    @Override
    protected Expr casePlusExpr(PlusExpr plusExpr) {
        return binaryOperatorDispatch(plusExpr);
    }

    @Override
    protected Expr caseMinusExpr(MinusExpr minusExpr) {
        return binaryOperatorDispatch(minusExpr);
    }

    @Override
    protected Expr caseMTimesExpr(MTimesExpr mTimesExpr) {
        return binaryOperatorDispatch(mTimesExpr);
    }

    @Override
    protected Expr caseMDivExpr(MDivExpr mDivExpr) {
        return binaryOperatorDispatch(mDivExpr);
    }

    @Override
    protected Expr caseMLDivExpr(MLDivExpr mlDivExpr) {
        return binaryOperatorDispatch(mlDivExpr);
    }

    @Override
    protected Expr caseMPowExpr(MPowExpr mPowExpr) {
        return binaryOperatorDispatch(mPowExpr);
    }

    @Override
    protected Expr caseETimesExpr(ETimesExpr eTimesExpr) {
        return binaryOperatorDispatch(eTimesExpr);
    }

    @Override
    protected Expr caseEDivExpr(EDivExpr eDivExpr) {
        return binaryOperatorDispatch(eDivExpr);
    }

    @Override
    protected Expr caseELDivExpr(ELDivExpr elDivExpr) {
        return binaryOperatorDispatch(elDivExpr);
    }

    @Override
    protected Expr caseEPowExpr(EPowExpr ePowExpr) {
        return binaryOperatorDispatch(ePowExpr);
    }

    @Override
    protected Expr caseAndExpr(AndExpr andExpr) {
        return binaryOperatorDispatch(andExpr);
    }

    @Override
    protected Expr caseOrExpr(OrExpr orExpr) {
        return binaryOperatorDispatch(orExpr);
    }

    @Override
    protected Expr caseShortCircuitAndExpr(ShortCircuitAndExpr shortCircuitAndExpr) {
        return binaryOperatorDispatch(shortCircuitAndExpr);
    }

    @Override
    protected Expr caseShortCircuitOrExpr(ShortCircuitOrExpr shortCircuitOrExpr) {
        return binaryOperatorDispatch(shortCircuitOrExpr);
    }

    @Override
    protected Expr caseLTExpr(LTExpr ltExpr) {
        return binaryOperatorDispatch(ltExpr);
    }

    @Override
    protected Expr caseGTExpr(GTExpr gtExpr) {
        return binaryOperatorDispatch(gtExpr);
    }

    @Override
    protected Expr caseLEExpr(LEExpr leExpr) {
        return binaryOperatorDispatch(leExpr);
    }

    @Override
    protected Expr caseGEExpr(GEExpr geExpr) {
        return binaryOperatorDispatch(geExpr);
    }

    @Override
    protected Expr caseEQExpr(EQExpr eqExpr) {
        return binaryOperatorDispatch(eqExpr);
    }

    @Override
    protected Expr caseNEExpr(NEExpr neExpr) {
        return binaryOperatorDispatch(neExpr);
    }
}

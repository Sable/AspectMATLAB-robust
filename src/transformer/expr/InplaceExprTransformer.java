package transformer.expr;

import ast.*;

import java.util.List;
import java.util.stream.Collectors;

public class InplaceExprTransformer extends AbstractExprTransformer {
    @Override
    protected Expr caseRangeExpr(RangeExpr rangeExpr) {
        if (rangeExpr.hasIncr()) {
            Expr lowerExpr = this.transform(rangeExpr.getLower());
            Expr incrExpr  = this.transform(rangeExpr.getIncr());
            Expr upperExpr = this.transform(rangeExpr.getUpper());

            rangeExpr.setLower(lowerExpr);
            rangeExpr.setIncr(incrExpr);
            rangeExpr.setUpper(upperExpr);

            return rangeExpr;
        } else {
            Expr lowerExpr = this.transform(rangeExpr.getLower());
            Expr upperExpr = this.transform(rangeExpr.getUpper());

            rangeExpr.setLower(lowerExpr);
            rangeExpr.setUpper(upperExpr);

            return rangeExpr;
        }
    }

    @Override
    protected Expr caseColonExpr(ColonExpr colonExpr) {
        return colonExpr;
    }

    @Override
    protected Expr caseEndExpr(EndExpr endExpr) {
        return endExpr;
    }

    @Override
    protected Expr caseFunctionHandleExpr(FunctionHandleExpr functionHandleExpr) {
        return functionHandleExpr;
    }

    @Override
    protected Expr caseLambdaExpr(LambdaExpr lambdaExpr) {
        return lambdaExpr;
    }

    @Override
    protected Expr caseCellArrayExpr(CellArrayExpr cellArrayExpr) {
        List<List<Expr>> transformedList = cellArrayExpr.getRowList().stream()
                .map(row -> row.getElementList().stream().map(this::transform).collect(Collectors.toList()))
                .collect(Collectors.toList());

        for (int rowIndex = 0; rowIndex < cellArrayExpr.getNumRow(); rowIndex++) {
            for (int elementIndex = 0; elementIndex < cellArrayExpr.getRow(rowIndex).getNumElement(); elementIndex++) {
                final Expr elementExpr = transformedList.get(rowIndex).get(elementIndex);
                cellArrayExpr.getRow(rowIndex).setElement(elementExpr, elementIndex);
            }
        }

        return cellArrayExpr;
    }

    @Override
    protected Expr caseSuperClassMethodExpr(SuperClassMethodExpr superClassMethodExpr) {
        return superClassMethodExpr;
    }

    @Override
    protected Expr caseNameExpr(NameExpr nameExpr) {
        return nameExpr;
    }

    @Override
    protected Expr caseParameterizedExpr(ParameterizedExpr parameterizedExpr) {
        Expr targetExpr = this.transform(parameterizedExpr.getTarget());

        List<Expr> parameterList = parameterizedExpr.getArgList().stream()
                .map(this::transform)
                .collect(Collectors.toList());

        parameterizedExpr.setTarget(targetExpr);
        for (int parameterIndex = 0; parameterIndex < parameterizedExpr.getNumArg(); parameterIndex++) {
            final Expr parameterExpr = parameterList.get(parameterIndex);
            parameterizedExpr.setArg(parameterExpr, parameterIndex);
        }

        return parameterizedExpr;
    }

    @Override
    protected Expr caseCellIndexExpr(CellIndexExpr cellIndexExpr) {
        Expr targetExpr = this.transform(cellIndexExpr.getTarget());

        List<Expr> paramterList = cellIndexExpr.getArgList().stream()
                .map(this::transform)
                .collect(Collectors.toList());

        cellIndexExpr.setTarget(targetExpr);
        for (int parameterIndex = 0; parameterIndex < cellIndexExpr.getNumArg(); parameterIndex++) {
            final Expr parameterExpr = paramterList.get(parameterIndex);
            cellIndexExpr.setArg(parameterExpr, parameterIndex);
        }

        return cellIndexExpr;
    }

    @Override
    protected Expr caseDotExpr(DotExpr dotExpr) {
        Expr targetExpr = this.transform(dotExpr.getTarget());
        dotExpr.setTarget(targetExpr);
        return dotExpr;
    }

    @Override
    protected Expr caseMatrixExpr(MatrixExpr matrixExpr) {
        List<List<Expr>> transformedList = matrixExpr.getRowList().stream()
                .map(row -> row.getElementList().stream().map(this::transform).collect(Collectors.toList()))
                .collect(Collectors.toList());

        for (int rowIndex = 0; rowIndex < matrixExpr.getNumRow(); rowIndex++) {
            for (int elementIndex = 0; elementIndex < matrixExpr.getRow(rowIndex).getNumElement(); elementIndex++) {
                final Expr elementExpr = transformedList.get(rowIndex).get(elementIndex);
                matrixExpr.getRow(rowIndex).setElement(elementExpr, elementIndex);
            }
        }

        return matrixExpr;
    }

    @Override
    protected Expr caseIntLiteralExpr(IntLiteralExpr intLiteralExpr) {
        return intLiteralExpr;
    }

    @Override
    protected Expr caseFPLiteralExpr(FPLiteralExpr fpLiteralExpr) {
        return fpLiteralExpr;
    }

    @Override
    protected Expr caseStringLiteralExpr(StringLiteralExpr stringLiteralExpr) {
        return stringLiteralExpr;
    }

    private final UnaryExpr unaryOperatorDispatch(UnaryExpr unaryExpr) {
        Expr operandExpr = this.transform(unaryExpr.getOperand());
        unaryExpr.setOperand(operandExpr);
        return unaryExpr;
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
        final Expr lhsExpr = this.transform(binaryExpr.getLHS());
        final Expr rhsExpr = this.transform(binaryExpr.getRHS());

        binaryExpr.setLHS(lhsExpr);
        binaryExpr.setRHS(rhsExpr);

        return binaryExpr;
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

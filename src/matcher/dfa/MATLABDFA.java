package matcher.dfa;

import ast.*;
import matcher.nfa.MATLABNFA;
import utils.MATLABCodeGenUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

public final class MATLABDFA<T> extends DFA<T> {
    public MATLABDFA(MATLABNFA<T> nfa) {
        super(nfa, IntStream.iterate(1, x -> x + 1).iterator());
    }

    public MatrixExpr toMATLABStateTransferMatrix() {
        MatrixExpr stateMatrix = new MatrixExpr();
        Map<Integer, DFAState<T>> stateMap = new HashMap<>();
        Map<Integer, T> symbolMap = new HashMap<>();
        this.getStateSet().forEach(state -> stateMap.put((Integer) state.getStateName(), state));
        this.getAlphabet().toMap().entrySet().forEach(entry -> symbolMap.put((Integer) entry.getKey(), entry.getValue()));

        for (int stateIter = 1; stateIter <= stateMap.size(); stateIter++) {
            Row stateTransferVector = new Row();
            DFAState<T> currentState = stateMap.get(stateIter);

            DFAState<T> sigmaTransitionState = currentState.getSigmaTransfer();
            int sigmaTransitionStateName = ((Integer) sigmaTransitionState.getStateName());
            stateTransferVector.addElement(MATLABCodeGenUtil.buildIntLiteralExpr(sigmaTransitionStateName));

            for (int symbolCodeIter = 2; symbolCodeIter <= symbolMap.size() + 1; symbolCodeIter++) {
                T symbol = symbolMap.get(symbolCodeIter);
                DFAState<T> targetState = currentState.getStateTransfer(symbol);
                int targetStateName = ((Integer) targetState.getStateName()).intValue();
                stateTransferVector.addElement(MATLABCodeGenUtil.buildIntLiteralExpr(targetStateName));
            }

            stateMatrix.addRow(stateTransferVector);
        }
        return stateMatrix;
    }

    public MatrixExpr toMATABAcceptStateMatrix() {
        MatrixExpr acceptStateMatrix = new MatrixExpr();

        Row acceptStateVector = new Row();
        for (DFAState<T> acceptState : this.getAcceptingStateSet()) {
            int stateName = (Integer) acceptState.getStateName();
            acceptStateVector.addElement(MATLABCodeGenUtil.buildIntLiteralExpr(stateName));
        }
        acceptStateMatrix.addRow(acceptStateVector);

        return acceptStateMatrix;
    }

    public Function toMATLABFunction(
            Function alphabetFunction, java.util.function.Function<Expr, Expr> accessor,
            String functionName, Iterator<String> namespace) {
        if (alphabetFunction == null) throw new NullPointerException();
        if (accessor == null) throw new NullPointerException();
        if (functionName == null) throw new NullPointerException();
        if (namespace == null) throw new NullPointerException();

        Function returnFunction = new Function();
        returnFunction.setName(new Name(functionName));
        String inputParamName = namespace.next();
        String outputParamName = namespace.next();
        returnFunction.addInputParam(new Name(inputParamName));
        returnFunction.addOutputParam(new Name(outputParamName));

        String stateTransferMatrixName = namespace.next();
        {
            Expr lhsExpr = new NameExpr(new Name(stateTransferMatrixName));
            Expr rhsExpr = toMATLABStateTransferMatrix();
            AssignStmt stateTransferMatrixAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);
            returnFunction.addStmt(stateTransferMatrixAssign);
        }

        String acceptingStateMatrixName = namespace.next();
        {
            Expr lhsExpr = new NameExpr(new Name(acceptingStateMatrixName));
            Expr rhsExpr = toMATABAcceptStateMatrix();
            AssignStmt acceptingStateMatrixAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);
            returnFunction.addStmt(acceptingStateMatrixAssign);
        }

        String stateIteratorName = namespace.next();
        {
            Expr lhsExpr = new NameExpr(new Name(stateIteratorName));
            Expr rhsExpr = MATLABCodeGenUtil.buildIntLiteralExpr((Integer) this.getStartingState().getStateName());
            AssignStmt stateIteratorAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);
            returnFunction.addStmt(stateIteratorAssign);
        }

        String symbolIteratorName = namespace.next();
        {
            AssignStmt symbolIteratorAssign = new AssignStmt();
            symbolIteratorAssign.setLHS(new NameExpr(new Name(symbolIteratorName)));
            symbolIteratorAssign.setRHS(accessor.apply(new NameExpr(new Name(inputParamName))));

            ForStmt stateTransferLoop = new ForStmt();
            stateTransferLoop.setAssignStmt(symbolIteratorAssign);

            String symbolCodeName = namespace.next();
            {
                String alphabetFunctionName = alphabetFunction.getName().getID();
                Expr lhsExpr = new NameExpr(new Name(symbolCodeName));
                Expr rhsExpr = MATLABCodeGenUtil.buildParameterizedExpr(
                        alphabetFunctionName, new NameExpr(new Name(symbolIteratorName))
                );
                AssignStmt symbolCodeAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);
                stateTransferLoop.addStmt(symbolCodeAssign);
            }

            {
                Expr lhsExpr = new NameExpr(new Name(stateIteratorName));
                Expr rhsExpr = MATLABCodeGenUtil.buildParameterizedExpr(
                        stateTransferMatrixName,
                        new NameExpr(new Name(stateIteratorName)),
                        new NameExpr(new Name(symbolCodeName))
                );
                AssignStmt stateTransferAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);
                stateTransferLoop.addStmt(stateTransferAssign);
            }
            returnFunction.addStmt(stateTransferLoop);
        }

        {
            Expr lhsExpr = new NameExpr(new Name(outputParamName));
            Expr rhsExpr = MATLABCodeGenUtil.buildParameterizedExpr(
                    "any",
                    new EQExpr(
                            new NameExpr(new Name(acceptingStateMatrixName)),
                            new NameExpr(new Name(stateIteratorName))
                    )
            );
            AssignStmt verificationAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);
            returnFunction.addStmt(verificationAssign);
        }

        return returnFunction;
    }
}

package matcher;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import utils.MATLABCodeGenUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public final class MATLABAlphabet<T> extends Alphabet<Integer, T> {
    public static final int epsilonTransitionCode = 0;
    public static final int sigmaTransitionCode = 1;

    public MATLABAlphabet() {
        super(IntStream.iterate(2, x -> x + 1).iterator(), epsilonTransitionCode, sigmaTransitionCode);
    }

    public Function toMATLABFunction(BiFunction<Expr, T, Expr> matcher, String functionName, Iterator<String> namespace) {
        if (matcher == null) throw new NullPointerException();
        if (functionName == null) throw new NullPointerException();
        if (namespace == null) throw new NullPointerException();

        String inputParamName = namespace.next();
        String outputParamName = namespace.next();

        Function retFunction = new Function();
        retFunction.setName(new Name(functionName));
        retFunction.addInputParam(new Name(inputParamName));
        retFunction.addOutputParam(new Name(outputParamName));

        if (this.toMap().isEmpty()) {
            Expr lhsExpr = new NameExpr(new Name(outputParamName));
            Expr rhsExpr = new IntLiteralExpr(new DecIntNumericLiteralValue(Integer.toString(sigmaTransitionCode)));
            AssignStmt trivialAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);
            retFunction.addStmt(trivialAssign);
        } else {
            Collection<IfBlock> blocks = new HashSet<>();
            for (Map.Entry<Integer, T> entry : this.toMap().entrySet()) {
                IfBlock block = new IfBlock();
                block.setCondition(matcher.apply(new NameExpr(new Name(inputParamName)), entry.getValue()));

                Expr lhsExpr = new NameExpr(new Name(outputParamName));
                Expr rhsExpr = new IntLiteralExpr(new DecIntNumericLiteralValue(Integer.toString(entry.getKey())));
                AssignStmt handleAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);

                block.addStmt(handleAssign);
                blocks.add(block);
            }
            ElseBlock sigmaBlock = new ElseBlock();

            Expr lhsExpr = new NameExpr(new Name(outputParamName));
            Expr rhsExpr = new IntLiteralExpr(new DecIntNumericLiteralValue(Integer.toString(sigmaTransitionCode)));
            AssignStmt sigmaAssign = MATLABCodeGenUtil.buildAssignStmt(lhsExpr, rhsExpr);

            sigmaBlock.addStmt(sigmaAssign);

            IfStmt handIfStmt = new IfStmt();
            handIfStmt.setElseBlock(sigmaBlock);
            blocks.forEach(handIfStmt::addIfBlock);
            retFunction.addStmt(handIfStmt);
        }
        return retFunction;
    }
}

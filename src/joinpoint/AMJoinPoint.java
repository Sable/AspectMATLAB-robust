package joinpoint;

import ast.ASTNode;
import ast.Action;
import utils.CompilationInfo;

import java.util.Optional;

/** an abstract representation of AspectMATLAB join point */
public abstract class AMJoinPoint {
    private final AMSourceCodePos joinPointPosition;
    private final AMSourceCodePos actionPosition;

    public AMJoinPoint(ASTNode joinPoint, Action action, CompilationInfo compilationInfo) {
        joinPointPosition = new AMSourceCodePos(
                Optional.ofNullable(joinPoint).orElseThrow(NullPointerException::new),
                Optional.ofNullable(compilationInfo).orElseThrow(NullPointerException::new));
        actionPosition = new AMSourceCodePos(
                Optional.ofNullable(action).orElseThrow(NullPointerException::new),
                Optional.ofNullable(compilationInfo).orElseThrow(NullPointerException::new));
    }

    public AMSourceCodePos getJoinPointPosition() {
        return joinPointPosition;
    }

    public AMSourceCodePos getActionPosition() {
        return actionPosition;
    }
}

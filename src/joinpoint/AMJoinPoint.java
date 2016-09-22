package joinpoint;

import ast.ASTNode;
import ast.Action;
import utils.CompilationInfo;

/**
 * an abstract representation of AspectMATLAB join point
 */
public abstract class AMJoinPoint {
    protected final AMSourceCodePos joinPointPosition;
    protected final AMSourceCodePos actionPosition;

    public AMJoinPoint(ASTNode joinPoint, Action action, CompilationInfo compilationInfo) {

    }
}

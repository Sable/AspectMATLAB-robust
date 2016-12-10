package joinpoint;

import ast.ASTNode;
import ast.Action;
import utils.CompilationInfo;

public final class AMJoinPointOperator extends AMJoinPoint {


    public AMJoinPointOperator(ASTNode joinPointSite, Action action, CompilationInfo compilationInfo) {
        super(joinPointSite, action, compilationInfo);
    }

}

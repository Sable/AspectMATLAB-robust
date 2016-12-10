package joinpoint;

import ast.ASTNode;
import ast.Action;
import utils.CompilationInfo;

import java.util.Optional;

/** an abstract representation of AspectMATLAB join point */
public abstract class AMJoinPoint {
    private final AMSourceCodePos joinPointSitePosition;
    private final AMSourceCodePos actionPosition;

    protected final ASTNode joinPointSiteASTNode;
    protected final ASTNode actionASTNode;

    public AMJoinPoint(ASTNode joinPointSite, Action action, CompilationInfo compilationInfo) {
        joinPointSitePosition = new AMSourceCodePos(
                Optional.ofNullable(joinPointSite).orElseThrow(NullPointerException::new),
                Optional.ofNullable(compilationInfo).orElseThrow(NullPointerException::new));
        actionPosition = new AMSourceCodePos(
                Optional.ofNullable(action).orElseThrow(NullPointerException::new),
                Optional.ofNullable(compilationInfo).orElseThrow(NullPointerException::new));

        joinPointSiteASTNode = Optional.ofNullable(joinPointSite).orElseThrow(NullPointerException::new);
        actionASTNode = Optional.ofNullable(action).orElseThrow(NullPointerException::new);

    }

    public AMSourceCodePos getJoinPointSitePosition() {
        return joinPointSitePosition;
    }

    public AMSourceCodePos getActionPosition() {
        return actionPosition;
    }

    public ASTNode getJoinPointSiteASTNode() {
        return joinPointSiteASTNode;
    }

    public ASTNode getActionASTNode() {
        return actionASTNode;
    }
}

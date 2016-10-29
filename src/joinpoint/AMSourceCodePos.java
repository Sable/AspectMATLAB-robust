package joinpoint;

import ast.ASTNode;
import utils.CompilationInfo;

import java.util.Optional;

/** an abstraction on source code position */
public final class AMSourceCodePos {
    private final int startLineNumber;
    private final int startColumnNumber;
    private final String enclosingFilepath;

    /**
     * record source code position from a given ast node.
     * @param astNode the given ast node
     * @param compilationInfo additional information from compilation process
     * @throws NullPointerException if {@code astNode} or {@code compilationInfo} is {@code null}
     */
    public AMSourceCodePos(ASTNode astNode, CompilationInfo compilationInfo) {
        startLineNumber = Optional.ofNullable(astNode).orElseThrow(NullPointerException::new).getStartLine();
        startColumnNumber = Optional.ofNullable(astNode).orElseThrow(NullPointerException::new).getStartColumn();
        enclosingFilepath = Optional
                .ofNullable(compilationInfo)
                .orElseThrow(NullPointerException::new)
                .getASTNodeEnclosingFile(astNode);
    }

    /**
     * @param startLineNumber starting line number
     * @param startColumnNumber starting column number
     * @param enclosingFilepath enclosing file path
     * @throws NullPointerException if {@code enclosingFilepath} is {@code null}
     */
    @Deprecated
    public AMSourceCodePos(int startLineNumber, int startColumnNumber, String enclosingFilepath) {
        this.startLineNumber = startLineNumber;
        this.startColumnNumber = startColumnNumber;
        this.enclosingFilepath = Optional.ofNullable(enclosingFilepath).orElseThrow(NullPointerException::new);
    }

    /** @return the starting line number of the recorded AST node */
    public int getStartLineNumber() {
        return startLineNumber;
    }

    /** @return the starting column number of the recorded AST node */
    public int getStartColumnNumber() {
        return startColumnNumber;
    }

    /** @return the enclosing file of the recorded AST node */
    public String getEnclosingFilepath() {
        return enclosingFilepath;
    }

    @Override
    public String toString() {
        return String.format("[%3d:%3d]@%s", startLineNumber, startColumnNumber, enclosingFilepath);
    }
}

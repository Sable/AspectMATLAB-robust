package aspectMATLAB.utils;

import ast.ASTNode;

public interface CompilationInfo {
    String getASTNodeEnclosingFile(ASTNode astNode);
}

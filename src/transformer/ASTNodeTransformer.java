package transformer;

import ast.ASTNode;

public interface ASTNodeTransformer {
    ASTNode ASTNodeHandle(ASTNode operand);
}

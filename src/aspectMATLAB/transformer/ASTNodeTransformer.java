package aspectMATLAB.transformer;

import ast.ASTNode;

public interface ASTNodeTransformer<T extends ASTNode, R> {
    R transform(T astNode);
}

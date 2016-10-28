package transformer;

import ast.ASTNode;

public interface ASTTransformer<T extends ASTNode> {
    boolean hasTransformOnCurrentNode();
    boolean hasFurtherTransform();
    T copyAndTransform();
}

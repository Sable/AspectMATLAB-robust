package transformer;

import ast.ASTNode;
import ast.Expr;

public interface ASTTransformer<T extends ASTNode> {
    boolean hasTransformOnCurrentNode();
    boolean hasFurtherTransform();
    Expr copyAndTransform() throws Exception;
}

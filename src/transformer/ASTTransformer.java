package transformer;

import ast.ASTNode;
import ast.Expr;

public interface ASTTransformer<T extends ASTNode> {
    T transform(T target);
}

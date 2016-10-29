package transformer.expr;

import ast.Expr;
import transformer.ASTTransformer;

public abstract class ExprTrans implements ASTTransformer<Expr> {


    @Override
    public boolean hasTransformOnCurrentNode() {
        return false;
    }
}

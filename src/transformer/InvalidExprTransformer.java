package transformer;

import ast.ASTNode;
import transformer.expr.AbstractExprTransformer;

public final class InvalidExprTransformer extends RuntimeException {
    private final ASTNode site;
    private final AbstractExprTransformer transformer;

    public InvalidExprTransformer(AbstractExprTransformer transformer, ASTNode site) {
        this.site = site;
        this.transformer = transformer;
    }

    public ASTNode getSite() {
        return site;
    }

    public AbstractExprTransformer getTransformer() {
        return transformer;
    }
}

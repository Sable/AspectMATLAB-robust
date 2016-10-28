package transformer.pattern;

import ast.AndExpr;
import ast.Expr;
import ast.NotExpr;
import ast.OrExpr;
import transformer.ASTTransformer;

import java.util.Map;

public abstract class PatternTransformer implements ASTTransformer<Expr> {
    protected Expr pattern;
    protected final Map<String, Expr> predefinedPattern;

    public PatternTransformer(Expr pattern, Map<String, Expr> predefinedPattern) {
        this.pattern = pattern;
        this.predefinedPattern = predefinedPattern;
    }

    public abstract boolean hasTransformOnCurrentNode();
    public abstract boolean hasFurtherTransform();
    public abstract Expr copyAndTransform();

    public static PatternTransformer buildPatternTransformer(Expr pattern, Map<String, Expr> predefinedPattern) {
        if (pattern instanceof OrExpr) {
            return new OrTransformer((OrExpr) pattern, predefinedPattern);
        } else if (pattern instanceof AndExpr) {
            return new AndTransformer((AndExpr) pattern, predefinedPattern);
        } else if (pattern instanceof NotExpr) {
            return new NotTransformer((NotExpr) pattern, predefinedPattern);
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }
}

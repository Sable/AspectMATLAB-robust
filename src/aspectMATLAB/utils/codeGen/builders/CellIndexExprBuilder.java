package aspectMATLAB.utils.codeGen.builders;

import ast.Expr;
import ast.Name;
import ast.NameExpr;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class CellIndexExprBuilder {
    private Expr targetExpr = null;
    private List<Expr> paramList = new LinkedList<>();

    public CellIndexExprBuilder setTarget(Expr expr) {
        targetExpr = Optional.ofNullable(expr).orElseThrow(NullPointerException::new);
        return this;
    }
    public CellIndexExprBuilder setTarget(String name) {
        if (name == null) throw new NullPointerException();
        if (name.isEmpty()) throw new IllegalArgumentException();
        targetExpr = new NameExpr(new Name(name));
        return this;
    }
    public CellIndexExprBuilder addParameter(Expr param) {
        paramList.add(Optional.ofNullable(param).orElseThrow(NullPointerException::new));
        return this;
    }
    public CellIndexExprBuilder addParameter(String name) {
        if (name == null) throw new NullPointerException();
        if (name.isEmpty()) throw new IllegalArgumentException();
        return this.addParameter(new NameExpr(new Name(name)));
    }
    public CellIndexExprBuilder addParameter(int intLiteral) {
        if (intLiteral < 0) throw new IllegalArgumentException();
        return this.addParameter(new IntLiteralExprBuilder().setValue(intLiteral).build());
    }
}

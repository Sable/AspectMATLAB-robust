package utils.MATLABCodeGenUtils;

import ast.Expr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class ParameterizedExprBuilder {
    private Expr targetExpr = null;
    private List<Expr> paramList = new LinkedList<>();

    public ParameterizedExprBuilder setTarget(Expr expr) {
        targetExpr = Optional.ofNullable(expr).orElseThrow(NullPointerException::new);
        return this;
    }
    public ParameterizedExprBuilder setTarget(String name) {
        if (name == null) throw new NullPointerException();
        if (name.isEmpty()) throw new IllegalArgumentException();
        targetExpr = new NameExpr(new Name(name));
        return this;
    }
    public ParameterizedExprBuilder addParameter(Expr param) {
        paramList.add(Optional.ofNullable(param).orElseThrow(NullPointerException::new));
        return this;
    }
    public ParameterizedExprBuilder addParameter(String name) {
        if (name == null) throw new NullPointerException();
        if (name.isEmpty()) throw new IllegalArgumentException();
        return this.addParameter(new NameExpr(new Name(name)));
    }
    public ParameterizedExprBuilder addParameter(int intLiteral) {
        return this.addParameter(new IntLiteralExprBuilder().setValue(intLiteral).build());
    }
    public ParameterizedExpr build() {
        ParameterizedExpr retExpr = new ParameterizedExpr();
        retExpr.setTarget(Optional.ofNullable(this.targetExpr).orElseThrow(NullPointerException::new));
        for (Expr param : paramList) retExpr.addArg(param);
        return retExpr;
    }
}

package aspectMATLAB.utils.codeGen.builders;

import ast.BinaryExpr;
import ast.Expr;
import ast.Name;
import ast.NameExpr;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public final class BinaryExprBuilder<T extends BinaryExpr> {
    private Expr lhs = null;
    private Expr rhs = null;

    public BinaryExprBuilder<T> setLHS(Expr lhs) {
        this.lhs = Optional.ofNullable(lhs).orElseThrow(NullPointerException::new);
        return this;
    }

    public BinaryExprBuilder<T> setLHS(String lhs) {
        if (lhs == null) throw new NullPointerException();
        if (lhs.isEmpty()) throw new IllegalArgumentException();
        this.lhs = new NameExpr(new Name(lhs));
        return this;
    }

    public BinaryExprBuilder<T> setLHS(int lhs) {
        this.lhs = new IntLiteralExprBuilder().setValue(lhs).build();
        return this;
    }

    public BinaryExprBuilder<T> setRHS(Expr rhs) {
        this.rhs = Optional.ofNullable(rhs).orElseThrow(NullPointerException::new);
        return this;
    }

    public BinaryExprBuilder<T> setRHS(String rhs) {
        if (rhs == null) throw new NullPointerException();
        if (rhs.isEmpty()) throw new IllegalArgumentException();
        this.rhs = new NameExpr(new Name(rhs));
        return this;
    }

    public BinaryExprBuilder<T> setRHS(int rhs) {
        this.rhs = new IntLiteralExprBuilder().setValue(rhs).build();
        return this;
    }

    public T build() {
        ParameterizedType superClassType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<T> typeT = (Class<T>) superClassType.getActualTypeArguments()[0];
        try {
            T retExpr = typeT.newInstance();
            retExpr.setLHS(this.lhs);
            retExpr.setRHS(this.rhs);
            return retExpr;
        } catch (InstantiationException exception) {
            throw new RuntimeException(exception.toString());
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception.toString());
        }
    }
}

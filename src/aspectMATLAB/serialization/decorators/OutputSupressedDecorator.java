package aspectMATLAB.serialization.decorators;

import aspectMATLAB.serialization.ASTNodeDecorator;
import ast.ASTNode;
import ast.Stmt;

public final class OutputSupressedDecorator implements ASTNodeDecorator<Boolean> {
    @Override
    public String tag() {
        return "isOutputSuppressed";
    }

    @Override
    public Boolean decorate(ASTNode astNode) {
        if (astNode instanceof Stmt) {
            return ((Stmt) astNode).isOutputSuppressed();
        } else {
            return null;
        }
    }

    @Override
    public void parse(ASTNode astNode, String string) {
        if (astNode instanceof Stmt) {
            ((Stmt) astNode).setOutputSuppressed(Boolean.parseBoolean(string));
        }
    }
}

package aspectMATLAB.serialization.decorators;


import aspectMATLAB.serialization.ASTNodeDecorator;
import ast.ASTNode;

public final class ColumnNumberDecorator implements ASTNodeDecorator<Number> {
    @Override
    public String tag() {
        return "col";
    }

    @Override
    public Number decorate(ASTNode astNode) {
        return astNode.getStartColumn();
    }

    @Override
    public void parse(ASTNode astNode, String string) {
        astNode.setStartColumn(Integer.parseInt(string));
    }
}

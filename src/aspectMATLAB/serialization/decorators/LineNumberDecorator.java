package aspectMATLAB.serialization.decorators;

import aspectMATLAB.serialization.ASTNodeDecorator;
import ast.ASTNode;

public final class LineNumberDecorator implements ASTNodeDecorator<Number> {
    @Override
    public String tag() {
        return "line";
    }

    @Override
    public Number decorate(ASTNode astNode) {
        return astNode.getStartLine();
    }

    @Override
    public void parse(ASTNode astNode, String string) {
        astNode.setStartLine(Integer.parseInt(string));
    }
}

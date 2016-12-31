package aspectMATLAB.serialization.decorators;

import aspectMATLAB.serialization.ASTNodeDecorator;
import ast.ASTNode;

public final class RelativeChildIndexDecorator implements ASTNodeDecorator<Number> {
    @Override
    public String tag() {
        return "relativeChildIndex";
    }

    @Override
    public Number decorate(ASTNode astNode) {
        return astNode.getRelativeChildIndex();
    }

    @Override
    public void parse(ASTNode astNode, String string) {
        astNode.setRelativeChildIndex(Integer.parseInt(string));
    }
}

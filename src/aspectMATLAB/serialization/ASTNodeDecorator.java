package aspectMATLAB.serialization;

import ast.ASTNode;

public interface ASTNodeDecorator<T> {
    String tag();
    T decorate(ASTNode astNode);
    void parse(ASTNode astNode, String string);
}

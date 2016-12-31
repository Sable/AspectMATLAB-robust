package aspectMATLAB.serialization;

import ast.ASTNode;

public interface ASTNodeSerializer<T extends ASTNode, R> {
    R serialize(T astNode);
    String serializeAsString(T astNode);
    ASTNodeSerializer<T, R> appendStringDecorator (ASTNodeDecorator<? extends CharSequence> decorator);
    ASTNodeSerializer<T, R> appendNumberDecorator (ASTNodeDecorator<? extends Number>       decorator);
    ASTNodeSerializer<T, R> appendBooleanDecorator(ASTNodeDecorator<? extends Boolean>      decorator);
}

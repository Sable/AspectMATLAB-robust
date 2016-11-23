package transformer.program;

import ast.*;
import transformer.ASTNodeTransformer;
import transformer.expr.AbstractExprTransformer;
import transformer.stmt.AbstractStmtTransformer;

import java.util.List;

public abstract class AbstractProgramTransformer<TStmt extends AbstractStmtTransformer> implements ASTNodeTransformer {
    protected final TStmt statementTransformer;
    protected final AbstractExprTransformer expressionTransformer;

    public AbstractProgramTransformer(TStmt statementTransformer) {
        this.statementTransformer = statementTransformer;
        this.expressionTransformer = statementTransformer.getExprTransformer();
    }

    @Override
    public abstract ASTNode ASTNodeHandle(ASTNode operand);

    public abstract CompilationUnits transform(CompilationUnits compilationUnits);

    public Program transform(Program program) {
        if (program instanceof Script) {
            return caseScript((Script) program);
        } else if (program instanceof FunctionList) {
            return caseFunctionList(((FunctionList) program));
        } else if (program instanceof ClassDef) {
            return caseClassDef(((ClassDef) program));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    public abstract Program caseScript(Script script);
    public abstract Program caseFunctionList(FunctionList functionList);
    public abstract Program caseClassDef(ClassDef classDef);

    public List<ClassBody> caseClassBody(ClassBody classBody) {
        if (classBody instanceof Properties) {
            return caseProperties(((Properties) classBody));
        } else if (classBody instanceof Methods) {
            return caseMethods(((Methods) classBody));
        } else if (classBody instanceof ClassEvents) {
            return caseClassEvents(((ClassEvents) classBody));
        } else if (classBody instanceof Enumerations) {
            return caseEnumerations(((Enumerations) classBody));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    public abstract List<ClassBody> caseProperties(Properties properties);
    public abstract List<ClassBody> caseMethods(Methods methods);
    public abstract List<ClassBody> caseClassEvents(ClassEvents classEvents);
    public abstract List<ClassBody> caseEnumerations(Enumerations enumerations);

    public abstract List<Property> caseProperty(Property property);

    public abstract List<Function> caseFunction(Function function);
    public abstract List<Signature> caseSignature(Signature signature);
    public abstract List<PropertyAccess> casePropertyAccess(PropertyAccess propertyAccess);
    public abstract List<PropertyAccessSignature> casePropertyAccessSignature(PropertyAccessSignature propertyAccessSignature);

    public abstract List<Event> caseEvent(Event event);
    public abstract List<Enumeration> caseEnumeration(Enumeration enumeration);
}

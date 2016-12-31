package aspectMATLAB.transformer.program;

import ast.*;
import aspectMATLAB.transformer.ASTNodeTransformer;
import aspectMATLAB.transformer.expr.AbstractExprTransformer;
import aspectMATLAB.transformer.pattern.AbstractPatternTransformer;
import aspectMATLAB.transformer.stmt.AbstractStmtTransformer;

import java.util.List;

public abstract class AbstractProgramTransformer
        <TStmt extends AbstractStmtTransformer, TPattern extends AbstractPatternTransformer>
        implements ASTNodeTransformer<Program, Program> {
    protected final TStmt statementTransformer;
    protected final AbstractExprTransformer expressionTransformer;
    protected final TPattern patternTransformer;

    public AbstractProgramTransformer(TStmt statementTransformer, TPattern patternTransformer) {
        this.statementTransformer = statementTransformer;
        this.expressionTransformer = statementTransformer.getExprTransformer();
        this.patternTransformer = patternTransformer;
    }

    public TStmt getStatementTransformer() {
        return statementTransformer;
    }

    public AbstractExprTransformer getExpressionTransformer() {
        return expressionTransformer;
    }

    public TPattern getPatternTransformer() {
        return patternTransformer;
    }


    protected abstract ASTNode ASTNodeHandle(ASTNode operand);

    public abstract CompilationUnits transform(CompilationUnits compilationUnits);

    @Override
    public Program transform(Program program) {
        if (program instanceof Script) {
            return caseScript((Script) program);
        } else if (program instanceof FunctionList) {
            return caseFunctionList(((FunctionList) program));
        } else if (program instanceof ClassDef) {
            return caseClassDef(((ClassDef) program));
        } else if (program instanceof AspectDef) {
            return caseAspectDef(((AspectDef) program));
        } else {
            /* control flow should not reach here */
                throw new AssertionError();
        }
    }

    public abstract Program caseScript(Script script);
    public abstract Program caseFunctionList(FunctionList functionList);
    public abstract Program caseClassDef(ClassDef classDef);
    public abstract Program caseAspectDef(AspectDef aspectDef);

    public List<ClassBody> caseClassBody(ClassBody classBody) {
        if (classBody instanceof Properties) {
            return caseProperties(((Properties) classBody));
        } else if (classBody instanceof Methods) {
            return caseMethods(((Methods) classBody));
        } else if (classBody instanceof ClassEvents) {
            return caseClassEvents(((ClassEvents) classBody));
        } else if (classBody instanceof Enumerations) {
            return caseEnumerations(((Enumerations) classBody));
        } else if (classBody instanceof Patterns) {
            return casePatterns(((Patterns) classBody));
        } else if (classBody instanceof Actions) {
            return caseActions(((Actions) classBody));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    public abstract List<ClassBody> caseProperties(Properties properties);
    public abstract List<ClassBody> caseMethods(Methods methods);
    public abstract List<ClassBody> caseClassEvents(ClassEvents classEvents);
    public abstract List<ClassBody> caseEnumerations(Enumerations enumerations);
    public abstract List<ClassBody> casePatterns(Patterns patterns);
    public abstract List<ClassBody> caseActions(Actions actions);

    public abstract List<Property> caseProperty(Property property);

    public abstract List<Function> caseFunction(Function function);
    public abstract List<Signature> caseSignature(Signature signature);
    public abstract List<PropertyAccess> casePropertyAccess(PropertyAccess propertyAccess);
    public abstract List<PropertyAccessSignature> casePropertyAccessSignature(PropertyAccessSignature propertyAccessSignature);

    public abstract List<Event> caseEvent(Event event);
    public abstract List<Enumeration> caseEnumeration(Enumeration enumeration);

    public abstract List<Pattern> casePattern(Pattern pattern);
    public abstract List<Action> caseAction(Action action);
}

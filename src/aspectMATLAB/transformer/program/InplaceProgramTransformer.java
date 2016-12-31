package aspectMATLAB.transformer.program;

import ast.*;
import ast.Properties;
import aspectMATLAB.transformer.pattern.InplacePatternTransformer;
import aspectMATLAB.transformer.stmt.InplaceStmtTransformer;
import aspectMATLAB.utils.codeGen.collectors.ASTListCollector;
import aspectMATLAB.utils.codeGen.collectors.ASTListMergeCollector;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class InplaceProgramTransformer
        <TStmt extends InplaceStmtTransformer, TPattern extends InplacePatternTransformer>
        extends AbstractProgramTransformer<TStmt, TPattern> {
    public InplaceProgramTransformer(TStmt statementTransformer, TPattern patternTransformer) {
        super(statementTransformer, patternTransformer);
    }

    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompilationUnits transform(CompilationUnits compilationUnits) {
        ast.List<Program> newProgramList = compilationUnits.getProgramList().stream()
                .map(this::transform)
                .collect(new ASTListCollector<>());

        compilationUnits.setProgramList(newProgramList);
        return compilationUnits;
    }

    @Override
    public Program caseScript(Script script) {
        ast.List<Stmt> newStmtList = new ast.List<>();
        script.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);

        script.setStmtList(newStmtList);
        return script;
    }

    @Override
    public Program caseFunctionList(FunctionList functionList) {
        ast.List<Function> newFunctionList = functionList.getFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());

        functionList.setFunctionList(newFunctionList);
        return functionList;
    }

    @Override
    public Program caseClassDef(ClassDef classDef) {
        ast.List<Properties> newPropertiesList = new ast.List<>();
        ast.List<Methods> newMethodsList = new ast.List<>();
        ast.List<ClassEvents> newClassEventsList = new ast.List<>();
        ast.List<Enumerations> newEnumerationsList = new ast.List<>();

        final Consumer<List<ClassBody>> resultDispatcher = returnList -> returnList.forEach(classBody -> {
            if (classBody instanceof Properties) {
                newPropertiesList.add(((Properties) classBody));
            } else if (classBody instanceof Methods) {
                newMethodsList.add(((Methods) classBody));
            } else if (classBody instanceof ClassEvents) {
                newClassEventsList.add(((ClassEvents) classBody));
            } else if (classBody instanceof Enumerations) {
                newEnumerationsList.add(((Enumerations) classBody));
            } else {
                /* control flow should not reach here */
                throw new AssertionError();
            }
        });

        classDef.getPropertyList().stream().map(this::caseProperties).forEachOrdered(resultDispatcher);
        classDef.getMethodList().stream().map(this::caseMethods).forEachOrdered(resultDispatcher);
        classDef.getClassEventList().stream().map(this::caseClassEvents).forEachOrdered(resultDispatcher);
        classDef.getEnumerationList().stream().map(this::caseEnumerations).forEachOrdered(resultDispatcher);

        classDef.setPropertyList(newPropertiesList);
        classDef.setMethodList(newMethodsList);
        classDef.setClassEventList(newClassEventsList);
        classDef.setEnumerationList(newEnumerationsList);

        return classDef;
    }

    @Override
    public Program caseAspectDef(AspectDef aspectDef) {
        ast.List<Properties> newPropertiesList = new ast.List<>();
        ast.List<Methods> newMethodsList = new ast.List<>();
        ast.List<ClassEvents> newClassEventsList = new ast.List<>();
        ast.List<Enumerations> newEnumerationsList = new ast.List<>();
        ast.List<Patterns> newPatternsList = new ast.List<>();
        ast.List<Actions> newActionsList = new ast.List<>();

        final Consumer<List<ClassBody>> resultDispatcher = returnList -> returnList.forEach(classBody -> {
            if (classBody instanceof Properties) {
                newPropertiesList.add(((Properties) classBody));
            } else if (classBody instanceof Methods) {
                newMethodsList.add(((Methods) classBody));
            } else if (classBody instanceof ClassEvents) {
                newClassEventsList.add(((ClassEvents) classBody));
            } else if (classBody instanceof Enumerations) {
                newEnumerationsList.add(((Enumerations) classBody));
            } else if (classBody instanceof Patterns) {
                newPatternsList.add(((Patterns) classBody));
            } else if (classBody instanceof Actions) {
                newActionsList.add(((Actions) classBody));
            } else {
                /* control flow should not reach here */
                throw new AssertionError();
            }
        });

        aspectDef.getPropertyList().stream().map(this::caseProperties).forEachOrdered(resultDispatcher);
        aspectDef.getMethodList().stream().map(this::caseMethods).forEachOrdered(resultDispatcher);
        aspectDef.getClassEventList().stream().map(this::caseClassEvents).forEachOrdered(resultDispatcher);
        aspectDef.getEnumerationList().stream().map(this::caseEnumerations).forEachOrdered(resultDispatcher);
        aspectDef.getPatternList().stream().map(this::casePatterns).forEachOrdered(resultDispatcher);
        aspectDef.getActionList().stream().map(this::caseActions).forEachOrdered(resultDispatcher);

        aspectDef.setPropertyList(newPropertiesList);
        aspectDef.setMethodList(newMethodsList);
        aspectDef.setClassEventList(newClassEventsList);
        aspectDef.setEnumerationList(newEnumerationsList);
        aspectDef.setPatternList(newPatternsList);
        aspectDef.setActionList(newActionsList);

        return aspectDef;
    }

    @Override
    public List<ClassBody> caseProperties(Properties properties) {
        ast.List<Property> newPropertyList = properties.getPropertyList().stream()
                .map(this::caseProperty)
                .collect(new ASTListMergeCollector<>());

        properties.setPropertyList(newPropertyList);
        return Collections.singletonList(properties);
    }

    @Override
    public List<ClassBody> caseMethods(Methods methods) {
        ast.List<Function> newFunctionList = methods.getFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());
        ast.List<PropertyAccess> newPropertyAccessList = methods.getPropAccList().stream()
                .map(this::casePropertyAccess)
                .collect(new ASTListMergeCollector<>());
        ast.List<Signature> newSignatureList = methods.getSignatureList().stream()
                .map(this::caseSignature)
                .collect(new ASTListMergeCollector<>());
        ast.List<PropertyAccessSignature> newPropertyAccessSignatureList = methods.getPropAccSigList().stream()
                .map(this::casePropertyAccessSignature)
                .collect(new ASTListMergeCollector<>());

        methods.setFunctionList(newFunctionList);
        methods.setPropAccList(newPropertyAccessList);
        methods.setSignatureList(newSignatureList);
        methods.setPropAccSigList(newPropertyAccessSignatureList);

        return Collections.singletonList(methods);
    }

    @Override
    public List<ClassBody> caseClassEvents(ClassEvents classEvents) {
        ast.List<Event> newEventList = classEvents.getEventList().stream()
                .map(this::caseEvent)
                .collect(new ASTListMergeCollector<>());

        classEvents.setEventList(newEventList);
        return Collections.singletonList(classEvents);
    }

    @Override
    public List<ClassBody> caseEnumerations(Enumerations enumerations) {
        ast.List<Enumeration> newEnumerationList = enumerations.getEnumerationList().stream()
                .map(this::caseEnumeration)
                .collect(new ASTListMergeCollector<>());

        enumerations.setEnumerationList(newEnumerationList);
        return Collections.singletonList(enumerations);
    }

    @Override
    public List<ClassBody> casePatterns(Patterns patterns) {
        ast.List<Pattern> newPatternList = patterns.getPatternList().stream()
                .map(this::casePattern)
                .collect(new ASTListMergeCollector<>());

        patterns.setPatternList(newPatternList);
        return Collections.singletonList(patterns);
    }

    @Override
    public List<ClassBody> caseActions(Actions actions) {
        ast.List<Action> newActionList = actions.getActionList().stream()
                .map(this::caseAction)
                .collect(new ASTListMergeCollector<>());

        actions.setActionList(newActionList);
        return Collections.singletonList(actions);
    }

    @Override
    public List<Property> caseProperty(Property property) {
        Expr transformedInitExpr = this.expressionTransformer.transform(property.getExpr());

        property.setExpr(transformedInitExpr);
        return Collections.singletonList(property);
    }

    @Override
    public List<Function> caseFunction(Function function) {
        ast.List<Function> newNestedFunctionList = function.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());
        ast.List<Stmt> newStmtList = new ast.List<>();
        function.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);

        function.setNestedFunctionList(newNestedFunctionList);
        function.setStmtList(newStmtList);
        return Collections.singletonList(function);
    }

    @Override
    public List<Signature> caseSignature(Signature signature) {
        return Collections.singletonList(signature);
    }

    @Override
    public List<PropertyAccess> casePropertyAccess(PropertyAccess propertyAccess) {
        ast.List<Function> newNestedFunctionList = propertyAccess.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());
        ast.List<Stmt> newStmtList = new ast.List<>();
        propertyAccess.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);

        propertyAccess.setNestedFunctionList(newNestedFunctionList);
        propertyAccess.setStmtList(newStmtList);
        return Collections.singletonList(propertyAccess);
    }

    @Override
    public List<PropertyAccessSignature> casePropertyAccessSignature(PropertyAccessSignature propertyAccessSignature) {
        return Collections.singletonList(propertyAccessSignature);
    }

    @Override
    public List<Event> caseEvent(Event event) {
        return Collections.singletonList(event);
    }

    @Override
    public List<Enumeration> caseEnumeration(Enumeration enumeration) {
        ast.List<Expr> newExprList = enumeration.getExprList().stream()
                .map(this.expressionTransformer::transform)
                .collect(new ASTListCollector<>());

        enumeration.setExprList(newExprList);
        return Collections.singletonList(enumeration);
    }

    @Override
    public List<Pattern> casePattern(Pattern pattern) {
        Expr transformedPattern = this.patternTransformer.transform(pattern.getExpr());

        pattern.setExpr(transformedPattern);
        return Collections.singletonList(pattern);
    }

    @Override
    public List<Action> caseAction(Action action) {
        Expr transformedPattern = this.patternTransformer.transform(action.getExpr());
        ast.List<Stmt> newStmtList = new ast.List<>();
        action.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);
        ast.List<Function> newNestedFunctionList = action.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());

        action.setExpr(transformedPattern);
        action.setNestedFunctionList(newNestedFunctionList);
        action.setStmtList(newStmtList);
        return Collections.singletonList(action);
    }
}

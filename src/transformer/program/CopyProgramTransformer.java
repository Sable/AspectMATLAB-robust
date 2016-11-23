package transformer.program;

import ast.*;
import transformer.expr.CopyExprTransformer;
import transformer.stmt.CopyStmtTransformer;
import utils.ASTListCollector;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.List;

public class CopyProgramTransformer<TStmt extends CopyStmtTransformer> extends AbstractProgramTransformer<TStmt> {
    public CopyProgramTransformer(TStmt statementTransformer) {
        super(statementTransformer);
    }

    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        return operand.copy();
    }

    @Override
    public CompilationUnits transform(CompilationUnits compilationUnits) {
        ast.List<Program> newProgramList = compilationUnits.getProgramList().stream()
                .map(this::transform)
                .collect(new ASTListCollector<>());
        CompilationUnits copiedCompilationUnits = (CompilationUnits) ASTNodeHandle(compilationUnits);
        copiedCompilationUnits.setProgramList(newProgramList);
        return copiedCompilationUnits;
    }

    @Override
    public Program caseScript(Script script) {
        ast.List<Stmt> newStmtList = new ast.List<>();
        script.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);

        ast.List<HelpComment> newHelpCommentList = script.getHelpCommentList().stream()
                .map(comment -> (HelpComment) ASTNodeHandle(comment))
                .collect(new ASTListCollector<>());

        Script copiedProgram = (Script) ASTNodeHandle(script);
        copiedProgram.setStmtList(newStmtList);
        copiedProgram.setHelpCommentList(newHelpCommentList);

        return copiedProgram;
    }

    @Override
    public Program caseFunctionList(FunctionList functionList) {
        ast.List<Function> newFunctionList = new ast.List<>();
        functionList.getFunctionList().stream()
                .map(this::caseFunction)
                .forEachOrdered(newFunctionList::addAll);

        FunctionList copiedProgram = (FunctionList) ASTNodeHandle(functionList);
        copiedProgram.setFunctionList(newFunctionList);

        return copiedProgram;
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

        ast.List<HelpComment> newHelpCommentList = classDef.getHelpCommentList().stream()
                .map(comment-> (HelpComment) ASTNodeHandle(comment))
                .collect(new ASTListCollector<>());

        ast.List<Attribute> newAttributeList = classDef.getAttributeList().stream()
                .map(attribute -> {
                    Attribute copiedAttribute = (Attribute) ASTNodeHandle(attribute);
                    final CopyExprTransformer copyExprTransformer = new CopyExprTransformer();
                    Expr copiedAttributeExpr = copyExprTransformer.transform(attribute.getExpr());
                    copiedAttribute.setExpr(copiedAttributeExpr);
                    return copiedAttribute;
                })
                .collect(new ASTListCollector<>());

        ast.List<SuperClass> newSuperClassList = classDef.getSuperClassList().stream()
                .map(superClass -> new SuperClass(superClass.getName()))
                .collect(new ASTListCollector<>());

        ClassDef copiedProgram = (ClassDef) ASTNodeHandle(classDef);
        copiedProgram.setPropertyList(newPropertiesList);
        copiedProgram.setMethodList(newMethodsList);
        copiedProgram.setClassEventList(newClassEventsList);
        copiedProgram.setEnumerationList(newEnumerationsList);
        copiedProgram.setHelpCommentList(newHelpCommentList);
        copiedProgram.setSuperClassList(newSuperClassList);
        copiedProgram.setAttributeList(newAttributeList);

        return copiedProgram;
    }

    @Override
    public List<ClassBody> caseProperties(Properties properties) {
        ast.List<Property> newPropertiesList = new ast.List<>();
        properties.getPropertyList().stream()
                .map(this::caseProperty)
                .forEachOrdered(newPropertiesList::addAll);
        ast.List<Attribute> newAttributeList = properties.getAttributeList().stream()
                .map(attribute -> {
                    Attribute copiedAttribute = (Attribute) ASTNodeHandle(attribute);
                    final CopyExprTransformer copyExprTransformer = new CopyExprTransformer();
                    Expr copiedAttributeExpr = copyExprTransformer.transform(attribute.getExpr());
                    copiedAttribute.setExpr(copiedAttributeExpr);
                    return copiedAttribute;
                })
                .collect(new ASTListCollector<>());

        Properties copiedClassBody = (Properties) ASTNodeHandle(properties);
        properties.setPropertyList(newPropertiesList);
        properties.setAttributeList(newAttributeList);

        return Collections.singletonList(copiedClassBody);
    }

    @Override
    public List<ClassBody> caseMethods(Methods methods) {
        ast.List<Function> newFunctionList = new ast.List<>();
        methods.getFunctionList().stream()
                .map(this::caseFunction)
                .forEachOrdered(newFunctionList::addAll);

        ast.List<PropertyAccess> newPropertyAccessList = new ast.List<>();
        methods.getPropAccList().stream()
                .map(this::casePropertyAccess)
                .forEachOrdered(newPropertyAccessList::addAll);

        ast.List<Signature> newSignatureList = new ast.List<>();
        methods.getSignatureList().stream()
                .map(this::caseSignature)
                .forEachOrdered(newSignatureList::addAll);

        ast.List<PropertyAccessSignature> newPropertyAccessSignatureList = new ast.List<>();
        methods.getPropAccSigList().stream()
                .map(this::casePropertyAccessSignature)
                .forEachOrdered(newPropertyAccessSignatureList::addAll);

        ast.List<Attribute> newAttributeList = methods.getAttributeList().stream()
                .map(attribute -> {
                    Attribute copiedAttribute = (Attribute) ASTNodeHandle(attribute);
                    final CopyExprTransformer copyExprTransformer = new CopyExprTransformer();
                    Expr copiedAttributeExpr = copyExprTransformer.transform(attribute.getExpr());
                    copiedAttribute.setExpr(copiedAttributeExpr);
                    return copiedAttribute;
                })
                .collect(new ASTListCollector<>());

        Methods copiedClassBody = (Methods) ASTNodeHandle(methods);
        copiedClassBody.setFunctionList(newFunctionList);
        copiedClassBody.setPropAccList(newPropertyAccessList);
        copiedClassBody.setSignatureList(newSignatureList);
        copiedClassBody.setPropAccSigList(newPropertyAccessSignatureList);
        copiedClassBody.setAttributeList(newAttributeList);

        return Collections.singletonList(copiedClassBody);
    }

    @Override
    public List<ClassBody> caseClassEvents(ClassEvents classEvents) {
        ast.List<Event> newEventList = new ast.List<>();
        classEvents.getEventList().stream()
                .map(this::caseEvent)
                .forEachOrdered(newEventList::addAll);

        ast.List<Attribute> newAttributeList = classEvents.getAttributeList().stream()
                .map(attribute -> {
                    Attribute copiedAttribute = (Attribute) ASTNodeHandle(attribute);
                    final CopyExprTransformer copyExprTransformer = new CopyExprTransformer();
                    Expr copiedAttributeExpr = copyExprTransformer.transform(attribute.getExpr());
                    copiedAttribute.setExpr(copiedAttributeExpr);
                    return copiedAttribute;
                })
                .collect(new ASTListCollector<>());

        ClassEvents copiedClassBody = (ClassEvents) ASTNodeHandle(classEvents);
        copiedClassBody.setAttributeList(newAttributeList);
        copiedClassBody.setEventList(newEventList);

        return Collections.singletonList(copiedClassBody);
    }

    @Override
    public List<ClassBody> caseEnumerations(Enumerations enumerations) {
        ast.List<Enumeration> newEnumerationList = new ast.List<>();
        enumerations.getEnumerationList().stream()
                .map(this::caseEnumeration)
                .forEachOrdered(newEnumerationList::addAll);

        ast.List<Attribute> newAttributeList = enumerations.getAttributeList().stream()
                .map(attribute -> {
                    Attribute copiedAttribute = (Attribute) ASTNodeHandle(attribute);
                    final CopyExprTransformer copyExprTransformer = new CopyExprTransformer();
                    Expr copiedAttributeExpr = copyExprTransformer.transform(attribute.getExpr());
                    copiedAttribute.setExpr(copiedAttributeExpr);
                    return copiedAttribute;
                })
                .collect(new ASTListCollector<>());

        Enumerations copiedClassBody = (Enumerations) ASTNodeHandle(enumerations);
        copiedClassBody.setAttributeList(newAttributeList);
        copiedClassBody.setEnumerationList(newEnumerationList);

        return Collections.singletonList(copiedClassBody);
    }

    @Override
    public List<Property> caseProperty(Property property) {
        Expr copiedInitExpr = this.expressionTransformer.transform(property.getExpr());

        Property copiedProperty = (Property) ASTNodeHandle(property);
        copiedProperty.setExpr(copiedInitExpr);

        return Collections.singletonList(copiedProperty);
    }

    @Override
    public List<Function> caseFunction(Function function) {
        ast.List<Function> newNestedFunctionList = new ast.List<>();
        function.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .forEachOrdered(newNestedFunctionList::addAll);
        ast.List<HelpComment> newHelpCommentList = function.getHelpCommentList().stream()
                .map(comment -> (HelpComment) ASTNodeHandle(comment))
                .collect(new ASTListCollector<>());
        ast.List<Name> newInputParamList = function.getInputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());
        ast.List<Name> newOutputParamList = function.getOutputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());
        ast.List<Stmt> newStmtList = new ast.List<>();
        function.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);

        Function copiedFunction = (Function) ASTNodeHandle(function);
        copiedFunction.setNestedFunctionList(newNestedFunctionList);
        copiedFunction.setHelpCommentList(newHelpCommentList);
        copiedFunction.setInputParamList(newInputParamList);
        copiedFunction.setOutputParamList(newOutputParamList);
        copiedFunction.setStmtList(newStmtList);

        return Collections.singletonList(copiedFunction);
    }

    @Override
    public List<Signature> caseSignature(Signature signature) {
        ast.List<Name> newInputParamList = signature.getInputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());
        ast.List<Name> newOutputParamList = signature.getOutputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());

        Signature copiedSignature = (Signature) ASTNodeHandle(signature);
        copiedSignature.setInputParamList(newInputParamList);
        copiedSignature.setOutputParamList(newOutputParamList);

        return Collections.singletonList(copiedSignature);
    }

    @Override
    public List<PropertyAccess> casePropertyAccess(PropertyAccess propertyAccess) {
        ast.List<Name> newInputParamList = propertyAccess.getInputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());
        ast.List<Name> newOutputParamList = propertyAccess.getOutputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());
        ast.List<HelpComment> newHelpCommentList = propertyAccess.getHelpCommentList().stream()
                .map(helpComment -> (HelpComment) ASTNodeHandle(helpComment))
                .collect(new ASTListCollector<>());
        ast.List<Stmt> newStmtList = new ast.List<>();
        propertyAccess.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);
        ast.List<Function> newNestedFunctionList = new ast.List<>();
        propertyAccess.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .forEachOrdered(newNestedFunctionList::addAll);

        PropertyAccess copiedPropertyAccess = (PropertyAccess) ASTNodeHandle(propertyAccess);
        copiedPropertyAccess.setOutputParamList(newOutputParamList);
        copiedPropertyAccess.setInputParamList(newInputParamList);
        copiedPropertyAccess.setHelpCommentList(newHelpCommentList);
        copiedPropertyAccess.setStmtList(newStmtList);
        copiedPropertyAccess.setNestedFunctionList(newNestedFunctionList);

        return Collections.singletonList(copiedPropertyAccess);
    }

    @Override
    public List<PropertyAccessSignature> casePropertyAccessSignature(PropertyAccessSignature propertyAccessSignature) {
        ast.List<Name> newOutputParamList = propertyAccessSignature.getOutputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());
        ast.List<Name> newInputParamList = propertyAccessSignature.getInputParamList().stream()
                .map(parameter -> new Name(parameter.getID()))
                .collect(new ASTListCollector<>());

        PropertyAccessSignature copiedPropertyAccessSignature = (PropertyAccessSignature) ASTNodeHandle(propertyAccessSignature);
        copiedPropertyAccessSignature.setOutputParamList(newOutputParamList);
        copiedPropertyAccessSignature.setInputParamList(newInputParamList);

        return Collections.singletonList(copiedPropertyAccessSignature);
    }

    @Override
    public List<Event> caseEvent(Event event) {
        return Collections.singletonList((Event) ASTNodeHandle(event));
    }

    @Override
    public List<Enumeration> caseEnumeration(Enumeration enumeration) {
        ast.List<Expr> newExprList = enumeration.getExprList().stream()
                .map(this.expressionTransformer::transform)
                .collect(new ASTListCollector<>());

        Enumeration copiedEnumeration = (Enumeration) ASTNodeHandle(enumeration);
        copiedEnumeration.setExprList(newExprList);

        return Collections.singletonList(copiedEnumeration);
    }
}

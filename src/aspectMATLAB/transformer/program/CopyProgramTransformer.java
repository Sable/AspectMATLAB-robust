package aspectMATLAB.transformer.program;

import ast.*;
import aspectMATLAB.transformer.expr.CopyExprTransformer;
import aspectMATLAB.transformer.pattern.CopyPatternTransformer;
import aspectMATLAB.transformer.stmt.CopyStmtTransformer;
import aspectMATLAB.utils.codeGen.collectors.ASTListCollector;
import aspectMATLAB.utils.codeGen.collectors.ASTListMergeCollector;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.List;

public class CopyProgramTransformer
        <TStmt extends CopyStmtTransformer, TPattern extends CopyPatternTransformer>
        extends AbstractProgramTransformer<TStmt, TPattern> {
    public CopyProgramTransformer(TStmt statementTransformer, TPattern patternTransformer) {
        super(statementTransformer, patternTransformer);
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
        ast.List<Function> newFunctionList = functionList.getFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());

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

        ast.List<HelpComment> newHelpCommentList = aspectDef.getHelpCommentList().stream()
                .map(comment -> (HelpComment) ASTNodeHandle(comment))
                .collect(new ASTListCollector<>());

        AspectDef copiedAspectDef = (AspectDef) ASTNodeHandle(aspectDef);
        copiedAspectDef.setHelpCommentList(newHelpCommentList);
        copiedAspectDef.setPropertyList(newPropertiesList);
        copiedAspectDef.setMethodList(newMethodsList);
        copiedAspectDef.setClassEventList(newClassEventsList);
        copiedAspectDef.setEnumerationList(newEnumerationsList);
        copiedAspectDef.setPatternList(newPatternsList);
        copiedAspectDef.setActionList(newActionsList);

        return copiedAspectDef;
    }

    @Override
    public List<ClassBody> caseProperties(Properties properties) {
        ast.List<Property> newPropertiesList = properties.getPropertyList().stream()
                .map(this::caseProperty)
                .collect(new ASTListMergeCollector<>());
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
        ast.List<Event> newEventList = classEvents.getEventList().stream()
                .map(this::caseEvent)
                .collect(new ASTListMergeCollector<>());

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
        ast.List<Enumeration> newEnumerationList = enumerations.getEnumerationList().stream()
                .map(this::caseEnumeration)
                .collect(new ASTListMergeCollector<>());

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
    public List<ClassBody> casePatterns(Patterns patterns) {
        ast.List<Pattern> newPatternList = patterns.getPatternList().stream()
                .map(this::casePattern)
                .collect(new ASTListMergeCollector<>());

        Patterns copiedClassBody = (Patterns) ASTNodeHandle(patterns);
        copiedClassBody.setPatternList(newPatternList);

        return Collections.singletonList(copiedClassBody);
    }

    @Override
    public List<ClassBody> caseActions(Actions actions) {
        ast.List<Action> newActionList = actions.getActionList().stream()
                .map(this::caseAction)
                .collect(new ASTListMergeCollector<>());

        Actions copiedClassBody = (Actions) ASTNodeHandle(actions);
        copiedClassBody.setActionList(newActionList);

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
        ast.List<Function> newNestedFunctionList = function.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());
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
        ast.List<Function> newNestedFunctionList = propertyAccess.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());

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

    @Override
    public List<Pattern> casePattern(Pattern pattern) {
        Expr transformedPattern = this.patternTransformer.transform(pattern.getExpr());

        Pattern copiedPattern = (Pattern) ASTNodeHandle(pattern);
        copiedPattern.setExpr(transformedPattern);
        return Collections.singletonList(copiedPattern);
    }

    @Override
    public List<Action> caseAction(Action action) {
        Expr transformedPattern = this.patternTransformer.transform(action.getExpr());
        ast.List<Name> newInputParamList = action.getInputParamList().stream()
                .map(parameter -> (Name) ASTNodeHandle(parameter))
                .collect(new ASTListCollector<>());
        ast.List<Stmt> newStmtList = new ast.List<>();
        action.getStmtList().stream()
                .map(this.statementTransformer::transform)
                .forEachOrdered(newStmtList::addAll);
        ast.List<Function> newNestedFunctionList = action.getNestedFunctionList().stream()
                .map(this::caseFunction)
                .collect(new ASTListMergeCollector<>());

        Action copiedAction = (Action) ASTNodeHandle(action);
        copiedAction.setExpr(transformedPattern);
        copiedAction.setInputParamList(newInputParamList);
        copiedAction.setStmtList(newStmtList);
        copiedAction.setNestedFunctionList(newNestedFunctionList);

        return Collections.singletonList(copiedAction);
    }
}

package aspectMATLAB.serialization.serializers;

import aspectMATLAB.serialization.ASTNodeDecorator;
import aspectMATLAB.serialization.ASTNodeSerializer;
import ast.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class JSONSerializer implements ASTNodeSerializer<ASTNode, JSONObject> {
    private Set<ASTNodeDecorator<? extends Number>> numericalDecoratorSet = new HashSet<>();
    private Set<ASTNodeDecorator<? extends CharSequence>> stringDecoratorSet = new HashSet<>();
    private Set<ASTNodeDecorator<? extends Boolean>> booleanDecoratorSet = new HashSet<>();

    @Override
    public JSONSerializer appendNumberDecorator(ASTNodeDecorator<? extends Number> decorator) {
        numericalDecoratorSet.add(decorator);
        return this;
    }

    @Override
    public JSONSerializer appendStringDecorator(ASTNodeDecorator<? extends CharSequence> decorator) {
        stringDecoratorSet.add(decorator);
        return this;
    }

    @Override
    public JSONSerializer appendBooleanDecorator(ASTNodeDecorator<? extends Boolean> decorator) {
        booleanDecoratorSet.add(decorator);
        return this;
    }

    private JSONObject applyDecoration(final ASTNode astNode, final JSONObject target) {
        Stream.concat(numericalDecoratorSet.stream(),
                Stream.concat(stringDecoratorSet.stream(),
                        booleanDecoratorSet.stream())
        ).forEach(decorator -> {
            final String tag = decorator.tag();
            final Object decoration = decorator.decorate(astNode);
            if (target.containsKey(tag)) throw new IllegalArgumentException();
            if (decoration == null) return;
            target.put(tag, decoration);
        });
        return target;
    }

    protected JSONArray serializeAsJSONArray(ast.List<? extends ASTNode> nodes) {
        JSONArray jsonArray = new JSONArray();
        nodes.stream()
                .map(node -> serialize(node))
                .forEachOrdered(jsonArray::add);
        return jsonArray;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject serialize(ASTNode astNode) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("ASTNodeType", astNode.getClass().getSimpleName());

        if (astNode instanceof CompilationUnits) {
            jsonObject.put("Program", serializeAsJSONArray(((CompilationUnits) astNode).getProgramList()));
        } else if (astNode instanceof Script) {
            jsonObject.put("Stmt", serializeAsJSONArray(((Script) astNode).getStmtList()));
        } else if (astNode instanceof FunctionList) {
            jsonObject.put("Function", serializeAsJSONArray(((FunctionList) astNode).getFunctionList()));
        } else if (astNode instanceof Function) {
            jsonObject.put("OutputParam", serializeAsJSONArray(((Function) astNode).getOutputParamList()));
            jsonObject.put("Name", serialize(((Function) astNode).getName()));
            jsonObject.put("InputParam", serializeAsJSONArray(((Function) astNode).getInputParamList()));
            jsonObject.put("Stmt", serializeAsJSONArray(((Function) astNode).getStmtList()));
            jsonObject.put("NestedFunction", serializeAsJSONArray(((Function) astNode).getNestedFunctionList()));
        } else if (astNode instanceof ClassDef) {
            jsonObject.put("Attribute", serializeAsJSONArray(((ClassDef) astNode).getAttributeList()));
            jsonObject.put("Name", ((ClassDef) astNode).getName());
            jsonObject.put("SuperClass", serializeAsJSONArray(((ClassDef) astNode).getSuperClassList()));
            jsonObject.put("Property", serializeAsJSONArray(((ClassDef) astNode).getPropertyList()));
            jsonObject.put("Method", serializeAsJSONArray(((ClassDef) astNode).getMethodList()));
            jsonObject.put("ClassEvent", serializeAsJSONArray(((ClassDef) astNode).getClassEventList()));
            jsonObject.put("Enumeration", serializeAsJSONArray(((ClassDef) astNode).getEnumerationList()));
        } else if (astNode instanceof Attribute) {
            jsonObject.put("Key", ((Attribute) astNode).getKey());
            jsonObject.put("Expr", serialize(((Attribute) astNode).getExpr()));
        } else if (astNode instanceof SuperClass) {
            jsonObject.put("Name", ((SuperClass) astNode).getName());
        } else if (astNode instanceof Properties) {
            jsonObject.put("Attribute", serializeAsJSONArray(((Properties) astNode).getAttributeList()));
            jsonObject.put("Property", serializeAsJSONArray(((Properties) astNode).getPropertyList()));
        } else if (astNode instanceof Methods) {
            jsonObject.put("Attribute", serializeAsJSONArray(((Methods) astNode).getAttributeList()));
            jsonObject.put("Signature", serializeAsJSONArray(((Methods) astNode).getSignatureList()));
            jsonObject.put("PropAcc", serializeAsJSONArray(((Methods) astNode).getPropAccList()));
            jsonObject.put("Function", serializeAsJSONArray(((Methods) astNode).getFunctionList()));
            jsonObject.put("PropAccSig", serializeAsJSONArray(((Methods) astNode).getPropAccSigList()));
        } else if (astNode instanceof ClassEvents) {
            jsonObject.put("Attribute", serializeAsJSONArray(((ClassEvents) astNode).getAttributeList()));
            jsonObject.put("Event", serializeAsJSONArray(((ClassEvents) astNode).getEventList()));
        } else if (astNode instanceof Enumerations) {
            jsonObject.put("Attribute", serializeAsJSONArray(((Enumerations) astNode).getAttributeList()));
            jsonObject.put("Enumeration", serializeAsJSONArray(((Enumerations) astNode).getEnumerationList()));
        } else if (astNode instanceof Property) {
            jsonObject.put("Name", ((Property) astNode).getName());
            jsonObject.put("Expr", serialize(((Property) astNode).getExpr()));
        } else if (astNode instanceof Signature) {
            jsonObject.put("OutputParam", serializeAsJSONArray(((Signature) astNode).getOutputParamList()));
            jsonObject.put("Name", ((Signature) astNode).getName());
            jsonObject.put("InputParam", serializeAsJSONArray(((Signature) astNode).getInputParamList()));
        } else if (astNode instanceof PropertyAccess) {
            jsonObject.put("OutputParam", serializeAsJSONArray(((PropertyAccess) astNode).getOutputParamList()));
            jsonObject.put("Access", ((PropertyAccess) astNode).getAccess());
            jsonObject.put("Name", ((PropertyAccess) astNode).getName());
            jsonObject.put("InputParam", serializeAsJSONArray(((PropertyAccess) astNode).getInputParamList()));
            jsonObject.put("Stmt", serializeAsJSONArray(((PropertyAccess) astNode).getStmtList()));
            jsonObject.put("NestedFunction", serializeAsJSONArray(((PropertyAccess) astNode).getNestedFunctionList()));
        } else if (astNode instanceof PropertyAccessSignature) {
            jsonObject.put("OutputParam", serializeAsJSONArray(((PropertyAccessSignature) astNode).getOutputParamList()));
            jsonObject.put("Access", ((PropertyAccessSignature) astNode).getAccess());
            jsonObject.put("Name", ((PropertyAccessSignature) astNode).getName());
            jsonObject.put("InputParam", serializeAsJSONArray(((PropertyAccessSignature) astNode).getInputParamList()));
        } else if (astNode instanceof Event) {
            jsonObject.put("Name", ((Event) astNode).getName());
        } else if (astNode instanceof Enumeration) {
            jsonObject.put("Name", ((Enumeration) astNode).getName());
            jsonObject.put("Expr", serializeAsJSONArray(((Enumeration) astNode).getExprList()));
        } else if (astNode instanceof ExprStmt) {
            jsonObject.put("Expr", serialize(((ExprStmt) astNode).getExpr()));
        } else if (astNode instanceof AssignStmt) {
            jsonObject.put("LHS", serialize(((AssignStmt) astNode).getLHS()));
            jsonObject.put("RHS", serialize(((AssignStmt) astNode).getRHS()));
        } else if (astNode instanceof GlobalStmt) {
            jsonObject.put("Name", serializeAsJSONArray(((GlobalStmt) astNode).getNameList()));
        } else if (astNode instanceof PersistentStmt) {
            jsonObject.put("Name", serializeAsJSONArray(((PersistentStmt) astNode).getNameList()));
        } else if (astNode instanceof ShellCommandStmt) {
            jsonObject.put("Command", ((ShellCommandStmt) astNode).getCommand());
        } else if (astNode instanceof BreakStmt) {
            /* ignored */
        } else if (astNode instanceof ContinueStmt) {
            /* ignored */
        } else if (astNode instanceof ReturnStmt) {
            /* ignored */
        } else if (astNode instanceof EmptyStmt) {
            /* ignored */
        } else if (astNode instanceof ForStmt) {
            jsonObject.put("AssignStmt", serialize(((ForStmt) astNode).getAssignStmt()));
            jsonObject.put("Stmt", serializeAsJSONArray(((ForStmt) astNode).getStmtList()));
            jsonObject.put("isParfor", ((ForStmt) astNode).getisParfor());
            jsonObject.put("Worker", ((ForStmt) astNode).hasWorker()?serialize(((ForStmt) astNode).getWorker()): null);
        } else if (astNode instanceof WhileStmt) {
            jsonObject.put("Expr", serialize(((WhileStmt) astNode).getExpr()));
            jsonObject.put("Stmt", serializeAsJSONArray(((WhileStmt) astNode).getStmtList()));
        } else if (astNode instanceof TryStmt) {
            jsonObject.put("TryStmt", serializeAsJSONArray(((TryStmt) astNode).getTryStmtList()));
            jsonObject.put("CatchStmt", serializeAsJSONArray(((TryStmt) astNode).getCatchStmtList()));
            jsonObject.put("CatchName", ((TryStmt) astNode).hasCatchName()?serialize(((TryStmt) astNode).getCatchName()): null);
        } else if (astNode instanceof SwitchStmt) {
            jsonObject.put("Expr", serialize(((SwitchStmt) astNode).getExpr()));
            jsonObject.put("SwitchCaseBlock", serializeAsJSONArray(((SwitchStmt) astNode).getSwitchCaseBlockList()));
            jsonObject.put("DefaultCaseBlock", ((SwitchStmt) astNode).hasDefaultCaseBlock()?
                    ((SwitchStmt) astNode).getDefaultCaseBlock():
                    null
            );
        } else if (astNode instanceof SwitchCaseBlock) {
            jsonObject.put("Expr", serialize(((SwitchCaseBlock) astNode).getExpr()));
            jsonObject.put("Stmt", serializeAsJSONArray(((SwitchCaseBlock) astNode).getStmtList()));
        } else if (astNode instanceof DefaultCaseBlock) {
            jsonObject.put("Stmt", serializeAsJSONArray(((DefaultCaseBlock) astNode).getStmtList()));
        } else if (astNode instanceof IfStmt) {
            jsonObject.put("IfBlock", serializeAsJSONArray(((IfStmt) astNode).getIfBlockList()));
            jsonObject.put("ElseBlock", ((IfStmt) astNode).hasElseBlock()? serialize(((IfStmt) astNode).getElseBlock()): null);
        } else if (astNode instanceof IfBlock) {
            jsonObject.put("Condition", serialize(((IfBlock) astNode).getCondition()));
            jsonObject.put("Stmt", serializeAsJSONArray(((IfBlock) astNode).getStmtList()));
        } else if (astNode instanceof ElseBlock) {
            jsonObject.put("Stmt", serializeAsJSONArray(((ElseBlock) astNode).getStmtList()));
        } else if (astNode instanceof SpmdStmt) {
            jsonObject.put("MinWorker", ((SpmdStmt) astNode).hasMinWorker()?serialize(((SpmdStmt) astNode).getMinWorker()): null);
            jsonObject.put("MaxWorker", ((SpmdStmt) astNode).hasMaxWorker()?serialize(((SpmdStmt) astNode).getMaxWorker()): null);
            jsonObject.put("Stmt", serializeAsJSONArray(((SpmdStmt) astNode).getStmtList()));
        } else if (astNode instanceof NameExpr) {
            jsonObject.put("Name", serialize(((NameExpr) astNode).getName()));
        } else if (astNode instanceof ParameterizedExpr) {
            jsonObject.put("Target", serialize(((ParameterizedExpr) astNode).getTarget()));
            jsonObject.put("Arg", serializeAsJSONArray(((ParameterizedExpr) astNode).getArgList()));
        } else if (astNode instanceof CellIndexExpr) {
            jsonObject.put("Target", serialize(((CellIndexExpr) astNode).getTarget()));
            jsonObject.put("Arg", serializeAsJSONArray(((CellIndexExpr) astNode).getArgList()));
        } else if (astNode instanceof DotExpr) {
            jsonObject.put("Target", serialize(((DotExpr) astNode).getTarget()));
            jsonObject.put("Field", serialize(((DotExpr) astNode).getField()));
        } else if (astNode instanceof MatrixExpr) {
            jsonObject.put("Row", serializeAsJSONArray(((MatrixExpr) astNode).getRowList()));
        } else if (astNode instanceof Row) {
            jsonObject.put("Element", serializeAsJSONArray(((Row) astNode).getElementList()));
        } else if (astNode instanceof IntLiteralExpr) {
            jsonObject.put("Value", ((IntLiteralExpr) astNode).getValue().getValue());
        } else if (astNode instanceof FPLiteralExpr) {
            jsonObject.put("Value", ((FPLiteralExpr) astNode).getValue().getValue());
        } else if (astNode instanceof StringLiteralExpr) {
            jsonObject.put("Value", ((StringLiteralExpr) astNode).getValue());
        } else if (astNode instanceof UnaryExpr) {
            jsonObject.put("Operand", serialize(((UnaryExpr) astNode).getOperand()));
        } else if (astNode instanceof BinaryExpr) {
            jsonObject.put("LHS", serialize(((BinaryExpr) astNode).getLHS()));
            jsonObject.put("RHS", serialize(((BinaryExpr) astNode).getRHS()));
        } else if (astNode instanceof RangeExpr) {
            jsonObject.put("Lower", serialize(((RangeExpr) astNode).getLower()));
            jsonObject.put("Incr", ((RangeExpr) astNode).hasIncr()?serialize(((RangeExpr) astNode).getIncr()): null);
            jsonObject.put("Upper", serialize(((RangeExpr) astNode).getUpper()));
        } else if (astNode instanceof ColonExpr) {
            /* ignored */
        } else if (astNode instanceof EndExpr) {
            /* ignored */
        } else if (astNode instanceof FunctionHandleExpr) {
            jsonObject.put("Name", serialize(((FunctionHandleExpr) astNode).getName()));
        } else if (astNode instanceof LambdaExpr) {
            jsonObject.put("InputParam", serializeAsJSONArray(((LambdaExpr) astNode).getInputParamList()));
            jsonObject.put("Body", serialize(((LambdaExpr) astNode).getBody()));
        } else if (astNode instanceof CellArrayExpr) {
            jsonObject.put("Row", serializeAsJSONArray(((CellArrayExpr) astNode).getRowList()));
        } else if (astNode instanceof SuperClassMethodExpr) {
            jsonObject.put("FuncName", serialize(((SuperClassMethodExpr) astNode).getFuncName()));
            jsonObject.put("ClassName", serialize(((SuperClassMethodExpr) astNode).getClassName()));
        } else if (astNode instanceof OneLineHelpComment) {
            throw new UnsupportedOperationException();
        } else if (astNode instanceof MultiLineHelpComment) {
            throw new UnsupportedOperationException();
        } else if (astNode instanceof Name) {
            jsonObject.put("ID", ((Name) astNode).getID());
        } else if (astNode instanceof AspectDef) {
            jsonObject.put("Name", ((AspectDef) astNode).getName());
            jsonObject.put("Property", serializeAsJSONArray(((AspectDef) astNode).getPatternList()));
            jsonObject.put("Method", serializeAsJSONArray(((AspectDef) astNode).getMethodList()));
            jsonObject.put("ClassEvent", serializeAsJSONArray(((AspectDef) astNode).getClassEventList()));
            jsonObject.put("Enumeration", serializeAsJSONArray(((AspectDef) astNode).getEnumerationList()));
            jsonObject.put("Pattern", serializeAsJSONArray(((AspectDef) astNode).getPatternList()));
            jsonObject.put("Action", serializeAsJSONArray(((AspectDef) astNode).getActionList()));
        } else if (astNode instanceof Patterns) {
            jsonObject.put("Pattern", serializeAsJSONArray(((Patterns) astNode).getPatternList()));
        } else if (astNode instanceof Actions) {
            jsonObject.put("Action", serializeAsJSONArray(((Actions) astNode).getActionList()));
        } else if (astNode instanceof Action) {
            jsonObject.put("Name", ((Action) astNode).getName());
            jsonObject.put("Type", ((Action) astNode).getType());
            jsonObject.put("Expr", serialize(((Action) astNode).getExpr()));
            jsonObject.put("InputParam", serializeAsJSONArray(((Action) astNode).getInputParamList()));
            jsonObject.put("Stmt", serializeAsJSONArray(((Action) astNode).getStmtList()));
            jsonObject.put("NestedFunction", serializeAsJSONArray(((Action) astNode).getNestedFunctionList()));
        } else if (astNode instanceof Pattern) {
            jsonObject.put("Name", ((Pattern) astNode).getName());
            jsonObject.put("Expr", serialize(((Pattern) astNode).getExpr()));
        } else if (astNode instanceof TypeSignature) {
            jsonObject.put("Type", serialize(((TypeSignature) astNode).getType()));
        } else if (astNode instanceof DimensionSignature) {
            jsonObject.put("Dimension", serializeAsJSONArray(((DimensionSignature) astNode).getDimensionList()));
        } else if (astNode instanceof FullSignature) {
            jsonObject.put("TypeSignature", ((FullSignature) astNode).hasTypeSignature()?
                    serialize(((FullSignature) astNode).getTypeSignature()):
                    null
            );
            jsonObject.put("DimensionSignature", ((FullSignature) astNode).hasDimensionSignature()?
                    serialize(((FullSignature) astNode).getDimensionSignature()):
                    null
            );
        } else if (astNode instanceof PatternName) {
            jsonObject.put("Name", serialize(((PatternName) astNode).getName()));
        } else if (astNode instanceof PatternGet) {
            jsonObject.put("Identifier", serialize(((PatternGet) astNode).getIdentifier()));
            jsonObject.put("FullSignature", ((PatternGet) astNode).hasFullSignature()? serialize(((PatternGet) astNode).getFullSignature()): null);
        } else if (astNode instanceof PatternSet) {
            jsonObject.put("Identifier", serialize(((PatternSet) astNode).getIdentifier()));
            jsonObject.put("FullSignature", ((PatternSet) astNode).hasFullSignature()? serialize(((PatternSet) astNode).getFullSignature()): null);
        } else if (astNode instanceof Input) {
            jsonObject.put("FullSignature", serializeAsJSONArray(((Input) astNode).getFullSignatureList()));
        } else if (astNode instanceof Output) {
            jsonObject.put("FullSignature", serializeAsJSONArray(((Output) astNode).getFullSignatureList()));
        } else if (astNode instanceof PatternCall) {
            jsonObject.put("Identifier", serialize(((PatternCall) astNode).getIdentifier()));
            jsonObject.put("Input", serialize(((PatternCall) astNode).getInput()));
            jsonObject.put("Output", serialize(((PatternCall) astNode).getOutput()));
        } else if (astNode instanceof PatternExecution) {
            jsonObject.put("Identifier", serialize(((PatternExecution) astNode).getIdentifier()));
            jsonObject.put("Input", serialize(((PatternExecution) astNode).getInput()));
            jsonObject.put("Output", serialize(((PatternExecution) astNode).getOutput()));
        } else if (astNode instanceof PatternMainExecution) {
            /* ignore */
        } else if (astNode instanceof PatternLoop) {
            jsonObject.put("Type", ((PatternLoop) astNode).hasType()? serialize(((PatternLoop) astNode).getType()) : null);
            jsonObject.put("Identifier", serialize(((PatternLoop) astNode).getIdentifier()));
        } else if (astNode instanceof PatternLoopHead) {
            jsonObject.put("Type", ((PatternLoopHead) astNode).hasType()? serialize(((PatternLoopHead) astNode).getType()) : null);
            jsonObject.put("Identifier", serialize(((PatternLoopHead) astNode).getIdentifier()));
        } else if (astNode instanceof PatternLoopBody) {
            jsonObject.put("Type", ((PatternLoopBody) astNode).hasType()? serialize(((PatternLoopBody) astNode).getType()) : null);
            jsonObject.put("Identifier", serialize(((PatternLoopBody) astNode).getIdentifier()));
        } else if (astNode instanceof SelectorSimple) {
            jsonObject.put("Element", serialize(((SelectorSimple) astNode).getElement()));
        } else if (astNode instanceof SelectorCompound) {
            jsonObject.put("Element", serializeAsJSONArray(((SelectorCompound) astNode).getElementList()));
        } else if (astNode instanceof PatternAnnotate) {
            jsonObject.put("Identifier", serialize(((PatternAnnotate) astNode).getIdentifier()));
            jsonObject.put("Selector", serializeAsJSONArray(((PatternAnnotate) astNode).getSelectorList()));
        } else if (astNode instanceof PatternOperator) {
            jsonObject.put("Type", serialize(((PatternOperator) astNode).getType()));
            jsonObject.put("FullSignature", serializeAsJSONArray(((PatternOperator) astNode).getFullSignatureList()));
        } else if (astNode instanceof PatternWithin) {
            jsonObject.put("Type", serialize(((PatternWithin) astNode).getType()));
            jsonObject.put("Identifier", serialize(((PatternWithin) astNode).getIdentifier()));
        } else if (astNode instanceof PatternDimension) {
            jsonObject.put("DimensionSignature", serialize(((PatternDimension) astNode).getDimensionSignature()));
        } else if (astNode instanceof PatternIsType) {
            jsonObject.put("TypeSignature", serialize(((PatternIsType) astNode).getTypeSignature()));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }

        return applyDecoration(astNode, jsonObject);
    }

    @Override
    public String serializeAsString(ASTNode astNode) {
        return serialize(astNode).toJSONString();
    }
}

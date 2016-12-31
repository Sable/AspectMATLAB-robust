package aspectMATLAB.serialization.serializers;

import aspectMATLAB.serialization.ASTNodeDecorator;
import aspectMATLAB.serialization.ASTNodeSerializer;
import ast.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class XMLSerializer implements ASTNodeSerializer<ASTNode, Document> {
    private Set<ASTNodeDecorator<? extends Number>> numericalDecoratorSet = new HashSet<>();
    private Set<ASTNodeDecorator<? extends CharSequence>> stringDecoratorSet = new HashSet<>();
    private Set<ASTNodeDecorator<? extends Boolean>> booleanDecoratorSet = new HashSet<>();

    @Override
    public XMLSerializer appendNumberDecorator(ASTNodeDecorator<? extends Number> decorator) {
        numericalDecoratorSet.add(decorator);
        return this;
    }

    @Override
    public XMLSerializer appendStringDecorator(ASTNodeDecorator<? extends CharSequence> decorator) {
        stringDecoratorSet.add(decorator);
        return this;
    }

    @Override
    public XMLSerializer appendBooleanDecorator(ASTNodeDecorator<? extends Boolean> decorator) {
        booleanDecoratorSet.add(decorator);
        return this;
    }

    private Element applyDecoration(final ASTNode astNode, final Element target) {
        Stream.concat(numericalDecoratorSet.stream(),
                Stream.concat(stringDecoratorSet.stream(),
                        booleanDecoratorSet.stream())
        ).forEach(decorator -> {
            final String tag = decorator.tag();
            final Object decoration = decorator.decorate(astNode);
            if (target.hasAttribute(tag)) throw new IllegalArgumentException();
            if (decoration == null) return;
            target.setAttribute(tag, decoration.toString());
        });
        return target;
    }

    private Element serializeElementList(ast.List<? extends ASTNode> list, String listName, Document document) {
        Element element = document.createElement(listName);
        for (ASTNode elementIteration : list) {
            Element serializedElement = serializeElement(elementIteration, document);
            element.appendChild(serializedElement);
        }
        return element;
    }

    private Element serializeElement(ASTNode astNode, final Document document) {
        Element element = document.createElement(astNode.getClass().getSimpleName());

        if (astNode instanceof CompilationUnits) {
            element.appendChild(serializeElementList(((CompilationUnits) astNode).getProgramList(), "Program", document));
        } else if (astNode instanceof Script) {
            element.appendChild(serializeElementList(((Script) astNode).getStmtList(), "Stmt", document));
        } else if (astNode instanceof FunctionList) {
            element.appendChild(serializeElementList(((FunctionList) astNode).getFunctionList(), "Function", document));
        } else if (astNode instanceof Function) {
            element.appendChild(serializeElementList(((Function) astNode).getOutputParamList(), "OutputParam", document));
            element.appendChild(serializeElement(((Function) astNode).getName(), document));
            element.appendChild(serializeElementList(((Function) astNode).getInputParamList(), "InputParam", document));
            element.appendChild(serializeElementList(((Function) astNode).getStmtList(), "Stmt", document));
            element.appendChild(serializeElementList(((Function) astNode).getNestedFunctionList(), "NestedFunction", document));
        } else if (astNode instanceof ClassDef) {
            element.appendChild(serializeElementList(((ClassDef) astNode).getAttributeList(), "Attribute", document));
            element.setAttribute("Name", ((ClassDef) astNode).getName());
            element.appendChild(serializeElementList(((ClassDef) astNode).getSuperClassList(), "SuperClass", document));
            element.appendChild(serializeElementList(((ClassDef) astNode).getPropertyList(), "Property", document));
            element.appendChild(serializeElementList(((ClassDef) astNode).getMethodList(), "Method", document));
            element.appendChild(serializeElementList(((ClassDef) astNode).getClassEventList(), "ClassEvent", document));
            element.appendChild(serializeElementList(((ClassDef) astNode).getEnumerationList(), "Enumeration", document));
        } else if (astNode instanceof Attribute) {
            element.setAttribute("Key", ((Attribute) astNode).getKey());
            element.appendChild(serializeElement(((Attribute) astNode).getExpr(), document));
        } else if (astNode instanceof SuperClass) {
            element.setAttribute("Name", ((SuperClass) astNode).getName());
        } else if (astNode instanceof Properties) {
            element.appendChild(serializeElementList(((Properties) astNode).getAttributeList(), "Attribute", document));
            element.appendChild(serializeElementList(((Properties) astNode).getPropertyList(), "Property", document));
        } else if (astNode instanceof Methods) {
            element.appendChild(serializeElementList(((Methods) astNode).getAttributeList(), "Attribute", document));
            element.appendChild(serializeElementList(((Methods) astNode).getSignatureList(), "Signature", document));
            element.appendChild(serializeElementList(((Methods) astNode).getPropAccList(), "PropAcc", document));
            element.appendChild(serializeElementList(((Methods) astNode).getFunctionList(), "Function", document));
            element.appendChild(serializeElementList(((Methods) astNode).getPropAccSigList(), "PropAccSig", document));
        } else if (astNode instanceof ClassEvents) {
            element.appendChild(serializeElementList(((ClassEvents) astNode).getAttributeList(), "Attribute", document));
            element.appendChild(serializeElementList(((ClassEvents) astNode).getEventList(), "Event", document));
        } else if (astNode instanceof Enumerations) {
            element.appendChild(serializeElementList(((Enumerations) astNode).getAttributeList(), "Attribute", document));
            element.appendChild(serializeElementList(((Enumerations) astNode).getEnumerationList(), "Enumeration", document));
        } else if (astNode instanceof Property) {
            element.setAttribute("Name", ((Property) astNode).getName());
            element.appendChild(serializeElement(((Property) astNode).getExpr(), document));
        } else if (astNode instanceof Signature) {
            element.appendChild(serializeElementList(((Signature) astNode).getOutputParamList(), "OutputParam", document));
            element.setAttribute("Name", ((Signature) astNode).getName());
            element.appendChild(serializeElementList(((Signature) astNode).getInputParamList(), "InputParam", document));
        } else if (astNode instanceof PropertyAccess) {
            element.appendChild(serializeElementList(((PropertyAccess) astNode).getOutputParamList(), "OutputParam", document));
            element.setAttribute("Access", ((PropertyAccess) astNode).getAccess());
            element.setAttribute("Name", ((PropertyAccess) astNode).getName());
            element.appendChild(serializeElementList(((PropertyAccess) astNode).getInputParamList(), "InputParam", document));
            element.appendChild(serializeElementList(((PropertyAccess) astNode).getStmtList(), "Stmt", document));
            element.appendChild(serializeElementList(((PropertyAccess) astNode).getNestedFunctionList(), "NestedFunction", document));
        } else if (astNode instanceof PropertyAccessSignature) {
            element.appendChild(serializeElementList(((PropertyAccessSignature) astNode).getOutputParamList(), "OutputParam", document));
            element.setAttribute("Access", ((PropertyAccessSignature) astNode).getAccess());
            element.setAttribute("Name", ((PropertyAccessSignature) astNode).getName());
            element.appendChild(serializeElementList(((PropertyAccessSignature) astNode).getInputParamList(), "InputParam", document));
        } else if (astNode instanceof Event) {
            element.setAttribute("Name", ((Event) astNode).getName());
        } else if (astNode instanceof Enumeration) {
            element.setAttribute("Name", ((Enumeration) astNode).getName());
            element.appendChild(serializeElementList(((Enumeration) astNode).getExprList(), "Expr", document));
        } else if (astNode instanceof ExprStmt) {
            element.appendChild(serializeElement(((ExprStmt) astNode).getExpr(), document));
        } else if (astNode instanceof AssignStmt) {
            element.appendChild(serializeElement(((AssignStmt) astNode).getLHS(), document));
            element.appendChild(serializeElement(((AssignStmt) astNode).getRHS(), document));
        } else if (astNode instanceof GlobalStmt) {
            element.appendChild(serializeElementList(((GlobalStmt) astNode).getNameList(), "Name", document));
        } else if (astNode instanceof PersistentStmt) {
            element.appendChild(serializeElementList(((PersistentStmt) astNode).getNameList(), "Name", document));
        } else if (astNode instanceof ShellCommandStmt) {
            element.setAttribute("Command", ((ShellCommandStmt) astNode).getCommand());
        } else if (astNode instanceof BreakStmt) {
            /* ignored */
        } else if (astNode instanceof ContinueStmt) {
            /* ignored */
        } else if (astNode instanceof ReturnStmt) {
            /* ignored */
        } else if (astNode instanceof EmptyStmt) {
            /* ignored */
        } else if (astNode instanceof ForStmt) {
            element.appendChild(serializeElement(((ForStmt) astNode).getAssignStmt(), document));
            element.appendChild(serializeElementList(((ForStmt) astNode).getStmtList(), "Stmt", document));
            element.setAttribute("isParfor", Boolean.toString(((ForStmt) astNode).getisParfor()));
            element.appendChild(((ForStmt) astNode).hasWorker()?
                    serializeElement(((ForStmt) astNode).getWorker(), document):
                    document.createElement("null")
            );
        } else if (astNode instanceof WhileStmt) {
            element.appendChild(serializeElement(((WhileStmt) astNode).getExpr(), document));
            element.appendChild(serializeElementList(((WhileStmt) astNode).getStmtList(), "Stmt", document));
        } else if (astNode instanceof TryStmt) {
            element.appendChild(serializeElementList(((TryStmt) astNode).getTryStmtList(), "TryStmt", document));
            element.appendChild(serializeElementList(((TryStmt) astNode).getCatchStmtList(), "CatchStmt", document));
            element.appendChild(((TryStmt) astNode).hasCatchName()?
                    serializeElement(((TryStmt) astNode).getCatchName(), document):
                    document.createElement("null")
            );
        } else if (astNode instanceof SwitchStmt) {
            element.appendChild(serializeElement(((SwitchStmt) astNode).getExpr(), document));
            element.appendChild(serializeElementList(((SwitchStmt) astNode).getSwitchCaseBlockList(), "SwitchCaseBlock", document));
            element.appendChild(((SwitchStmt) astNode).hasDefaultCaseBlock()?
                    serializeElement(((SwitchStmt) astNode).getDefaultCaseBlock(), document):
                    document.createElement("null")
            );
        } else if (astNode instanceof DefaultCaseBlock) {
            element.appendChild(serializeElementList(((DefaultCaseBlock) astNode).getStmtList(), "Stmt", document));
        } else if (astNode instanceof IfStmt) {
            element.appendChild(serializeElementList(((IfStmt) astNode).getIfBlockList(), "IfBlock", document));
            element.appendChild(((IfStmt) astNode).hasElseBlock()?
                    serializeElement(((IfStmt) astNode).getElseBlock(), document):
                    document.createElement("null")
            );
        } else if (astNode instanceof IfBlock) {
            element.appendChild(serializeElement(((IfBlock) astNode).getCondition(), document));
            element.appendChild(serializeElementList(((IfBlock) astNode).getStmtList(), "Stmt", document));
        } else if (astNode instanceof ElseBlock) {
            element.appendChild(serializeElementList(((ElseBlock) astNode).getStmtList(), "Stmt", document));
        } else if (astNode instanceof SpmdStmt) {
            element.appendChild(((SpmdStmt) astNode).hasMinWorker()?
                    serializeElement(((SpmdStmt) astNode).getMinWorker(), document):
                    document.createElement("null")
            );
            element.appendChild(((SpmdStmt) astNode).hasMaxWorker()?
                    serializeElement(((SpmdStmt) astNode).getMaxWorker(), document):
                    document.createElement("null")
            );
            element.appendChild(serializeElementList(((SpmdStmt) astNode).getStmtList(), "Stmt", document));
        } else if (astNode instanceof NameExpr) {
            element.appendChild(serializeElement(((NameExpr) astNode).getName(), document));
        } else if (astNode instanceof ParameterizedExpr) {
            element.appendChild(serializeElement(((ParameterizedExpr) astNode).getTarget(), document));
            element.appendChild(serializeElementList(((ParameterizedExpr) astNode).getArgList(), "Arg", document));
        } else if (astNode instanceof CellIndexExpr) {
            element.appendChild(serializeElement(((CellIndexExpr) astNode).getTarget(), document));
            element.appendChild(serializeElementList(((CellIndexExpr) astNode).getArgList(), "Arg", document));
        } else if (astNode instanceof DotExpr) {
            element.appendChild(serializeElement(((DotExpr) astNode).getTarget(), document));
            element.appendChild(serializeElement(((DotExpr) astNode).getField(), document));
        } else if (astNode instanceof MatrixExpr) {
            element.appendChild(serializeElementList(((MatrixExpr) astNode).getRowList(), "Row", document));
        } else if (astNode instanceof Row) {
            element.appendChild(serializeElementList(((Row) astNode).getElementList(), "Element", document));
        } else if (astNode instanceof IntLiteralExpr) {
            element.setAttribute("Value", ((IntLiteralExpr) astNode).getValue().getValue().toString());
        } else if (astNode instanceof FPLiteralExpr) {
            element.setAttribute("Value", ((FPLiteralExpr) astNode).getValue().toString());
        } else if (astNode instanceof StringLiteralExpr) {
            element.setAttribute("Value", ((StringLiteralExpr) astNode).getValue());
        } else if (astNode instanceof UnaryExpr) {
            element.appendChild(serializeElement(((UnaryExpr) astNode).getOperand(), document));
        } else if (astNode instanceof BinaryExpr) {
            element.appendChild(serializeElement(((BinaryExpr) astNode).getLHS(), document));
            element.appendChild(serializeElement(((BinaryExpr) astNode).getRHS(), document));
        } else if (astNode instanceof RangeExpr) {
            element.appendChild(serializeElement(((RangeExpr) astNode).getLower(), document));
            element.appendChild(((RangeExpr) astNode).hasIncr()?
                    serializeElement(((RangeExpr) astNode).getIncr(), document):
                    document.createElement("null")
            );
            element.appendChild(serializeElement(((RangeExpr) astNode).getUpper(), document));
        } else if (astNode instanceof ColonExpr) {
            /* ignore */
        } else if (astNode instanceof EndExpr) {
            /* ignore */
        } else if (astNode instanceof FunctionHandleExpr) {
            element.appendChild(serializeElement(((FunctionHandleExpr) astNode).getName(), document));
        } else if (astNode instanceof LambdaExpr) {
            element.appendChild(serializeElementList(((LambdaExpr) astNode).getInputParamList(), "InputParam", document));
            element.appendChild(serializeElement(((LambdaExpr) astNode).getBody(), document));
        } else if (astNode instanceof CellArrayExpr) {
            element.appendChild(serializeElementList(((CellArrayExpr) astNode).getRowList(), "Row", document));
        } else if (astNode instanceof SuperClassMethodExpr) {
            element.appendChild(serializeElement(((SuperClassMethodExpr) astNode).getFuncName(), document));
            element.appendChild(serializeElement(((SuperClassMethodExpr) astNode).getClassName(), document));
        } else if (astNode instanceof OneLineHelpComment) {
            throw new UnsupportedOperationException();
        } else if (astNode instanceof MultiLineHelpComment) {
            throw new UnsupportedOperationException();
        } else if (astNode instanceof Name) {
            element.setAttribute("ID", ((Name) astNode).getID());
        } else if (astNode instanceof AspectDef) {
            element.setAttribute("Name", ((AspectDef) astNode).getName());
            element.appendChild(serializeElementList(((AspectDef) astNode).getPropertyList(), "Property", document));
            element.appendChild(serializeElementList(((AspectDef) astNode).getMethodList(), "Method", document));
            element.appendChild(serializeElementList(((AspectDef) astNode).getClassEventList(), "ClassEvent", document));
            element.appendChild(serializeElementList(((AspectDef) astNode).getEnumerationList(), "Enumeration", document));
            element.appendChild(serializeElementList(((AspectDef) astNode).getPatternList(), "Pattern", document));
            element.appendChild(serializeElementList(((AspectDef) astNode).getActionList(), "Action", document));
        } else if (astNode instanceof Patterns) {
            element.appendChild(serializeElementList(((Patterns) astNode).getPatternList(), "Pattern", document));
        } else if (astNode instanceof Actions) {
            element.appendChild(serializeElementList(((Actions) astNode).getActionList(), "Action", document));
        } else if (astNode instanceof Action) {
            element.setAttribute("Name", ((Action) astNode).getName());
            element.setAttribute("Type", ((Action) astNode).getType());
            element.appendChild(serializeElement(((Action) astNode).getExpr(), document));
            element.appendChild(serializeElementList(((Action) astNode).getInputParamList(), "InputParam", document));
            element.appendChild(serializeElementList(((Action) astNode).getStmtList(), "Stmt", document));
            element.appendChild(serializeElementList(((Action) astNode).getNestedFunctionList(), "NestedFunction", document));
        } else if (astNode instanceof Pattern) {
            element.setAttribute("Name", ((Pattern) astNode).getName());
            element.appendChild(serializeElement(((Pattern) astNode).getExpr(), document));
        } else if (astNode instanceof TypeSignature) {
            element.appendChild(serializeElement(((TypeSignature) astNode).getType(), document));
        } else if (astNode instanceof DimensionSignature) {
            element.appendChild(serializeElementList(((DimensionSignature) astNode).getDimensionList(), "Dimension", document));
        } else if (astNode instanceof FullSignature) {
            element.appendChild(((FullSignature) astNode).hasTypeSignature()?
                    serializeElement(((FullSignature) astNode).getTypeSignature(), document):
                    document.createElement("null")
            );
            element.appendChild(((FullSignature) astNode).hasDimensionSignature()?
                    serializeElement(((FullSignature) astNode).getDimensionSignature(), document):
                    document.createElement("null")
            );
        } else if (astNode instanceof PatternName) {
            element.appendChild(serializeElement(((PatternName) astNode).getName(), document));
        } else if (astNode instanceof PatternGet) {
            element.appendChild(serializeElement(((PatternGet) astNode).getIdentifier(), document));
            element.appendChild(((PatternGet) astNode).hasFullSignature()?
                    serializeElement(((PatternGet) astNode).getFullSignature(), document):
                    document.createElement("null")
            );
        } else if (astNode instanceof PatternSet) {
            element.appendChild(serializeElement(((PatternSet) astNode).getIdentifier(), document));
            element.appendChild(((PatternSet) astNode).hasFullSignature()?
                    serializeElement(((PatternSet) astNode).getFullSignature(), document):
                    document.createElement("null")
            );
        } else if (astNode instanceof Input) {
            element.appendChild(serializeElementList(((Input) astNode).getFullSignatureList(), "FullSignature", document));
        } else if (astNode instanceof Output) {
            element.appendChild(serializeElementList(((Output) astNode).getFullSignatureList(), "FullSignature", document));
        } else if (astNode instanceof PatternCall) {
            element.appendChild(serializeElement(((PatternCall) astNode).getIdentifier(), document));
            element.appendChild(serializeElement(((PatternCall) astNode).getInput(), document));
            element.appendChild(serializeElement(((PatternCall) astNode).getOutput(), document));
        } else if (astNode instanceof PatternExecution) {
            element.appendChild(serializeElement(((PatternExecution) astNode).getIdentifier(), document));
            element.appendChild(serializeElement(((PatternExecution) astNode).getInput(), document));
            element.appendChild(serializeElement(((PatternExecution) astNode).getOutput(), document));
        } else if (astNode instanceof PatternMainExecution) {
            /* ignore */
        } else if (astNode instanceof PatternLoop) {
            element.appendChild(((PatternLoop) astNode).hasType()?
                    element.appendChild(serializeElement(((PatternLoop) astNode).getType(), document)):
                    document.createElement("null")
            );
            element.appendChild(serializeElement(((PatternLoop) astNode).getIdentifier(), document));
        } else if (astNode instanceof PatternLoopHead) {
            element.appendChild(((PatternLoopHead) astNode).hasType()?
                    element.appendChild(serializeElement(((PatternLoopHead) astNode).getType(), document)):
                    document.createElement("null")
            );
            element.appendChild(serializeElement(((PatternLoopHead) astNode).getIdentifier(), document));
        } else if (astNode instanceof PatternLoopBody) {
            element.appendChild(((PatternLoopBody) astNode).hasType()?
                    element.appendChild(serializeElement(((PatternLoopBody) astNode).getType(), document)):
                    document.createElement("null")
            );
            element.appendChild(serializeElement(((PatternLoopBody) astNode).getIdentifier(), document));
        } else if (astNode instanceof SelectorSimple) {
            element.appendChild(serializeElement(((SelectorSimple) astNode).getElement(), document));
        } else if (astNode instanceof SelectorCompound) {
            element.appendChild(serializeElementList(((SelectorCompound) astNode).getElementList(), "Element", document));
        } else if (astNode instanceof PatternAnnotate) {
            element.appendChild(serializeElement(((PatternAnnotate) astNode).getIdentifier(), document));
            element.appendChild(serializeElementList(((PatternAnnotate) astNode).getSelectorList(), "Selector", document));
        } else if (astNode instanceof PatternOperator) {
            element.appendChild(serializeElement(((PatternOperator) astNode).getType(), document));
            element.appendChild(serializeElementList(((PatternOperator) astNode).getFullSignatureList(), "FullSignature", document));
        } else if (astNode instanceof PatternWithin) {
            element.appendChild(serializeElement(((PatternWithin) astNode).getType(), document));
            element.appendChild(serializeElement(((PatternWithin) astNode).getIdentifier(), document));
        } else if (astNode instanceof PatternDimension) {
            element.appendChild(serializeElement(((PatternDimension) astNode).getDimensionSignature(), document));
        } else if (astNode instanceof PatternIsType) {
            element.appendChild(serializeElement(((PatternIsType) astNode).getTypeSignature(), document));
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }

        return applyDecoration(astNode, element);
    }

    @Override
    public Document serialize(ASTNode astNode) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            document.appendChild(serializeElement(astNode, document));
            return document;
        } catch (ParserConfigurationException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String serializeAsString(ASTNode astNode) {
        try {
            DOMSource domSource = new DOMSource(serialize(astNode));
            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(domSource, streamResult);
            return stringWriter.toString();
        } catch (TransformerException exception) {
            throw new RuntimeException(exception);
        }
    }
}

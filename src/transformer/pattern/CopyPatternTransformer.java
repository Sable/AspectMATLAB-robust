package transformer.pattern;

import ast.*;
import utils.codeGen.collectors.ASTListCollector;

public class CopyPatternTransformer extends AbstractPatternTransformer {
    @Override
    public ASTNode ASTNodeHandle(ASTNode operand) {
        return operand.copy();
    }

    @Override
    protected Expr caseAndExpr(AndExpr andExpr) {
        AndExpr copiedNode = (AndExpr) ASTNodeHandle(andExpr);

        Expr copiedLHS = this.transform(andExpr.getLHS());
        Expr copiedRHS = this.transform(andExpr.getRHS());
        copiedNode.setLHS(copiedLHS);
        copiedNode.setRHS(copiedRHS);

        return copiedNode;
    }

    @Override
    protected Expr caseOrExpr(OrExpr orExpr) {
        OrExpr copiedNode = (OrExpr) ASTNodeHandle(orExpr);

        Expr copiedLHS = this.transform(orExpr.getLHS());
        Expr copiedRHS = this.transform(orExpr.getRHS());
        copiedNode.setLHS(copiedLHS);
        copiedNode.setRHS(copiedRHS);

        return copiedNode;
    }

    @Override
    protected Expr caseNotExpr(NotExpr notExpr) {
        NotExpr copiedNode = (NotExpr) ASTNodeHandle(notExpr);

        Expr copiedOperand = this.transform(notExpr.getOperand());
        copiedNode.setOperand(copiedOperand);

        return copiedNode;
    }

    @Override
    protected Expr caseName(PatternName patternName) {
        Name name = (Name) ASTNodeHandle(patternName.getName());
        PatternName copiedPattern = (PatternName) ASTNodeHandle(patternName);
        copiedPattern.setName(name);
        return copiedPattern;
    }

    @Override
    protected Expr caseScope(PatternWithin patternWithin) {
        Name type = (Name) ASTNodeHandle(patternWithin.getType());
        Name identifier = (Name) ASTNodeHandle(patternWithin.getIdentifier());
        PatternWithin copiedPattern = (PatternWithin) ASTNodeHandle(patternWithin);
        copiedPattern.setType(type);
        copiedPattern.setIdentifier(identifier);
        return copiedPattern;
    }

    @Override
    protected Expr caseShape(PatternDimension patternDimension) {
        DimensionSignature dimensionSignature = this.copyDimensionSignature(patternDimension.getDimensionSignature());
        PatternDimension copiedPattern = (PatternDimension) ASTNodeHandle(patternDimension);
        copiedPattern.setDimensionSignature(dimensionSignature);
        return copiedPattern;
    }

    @Override
    protected Expr caseType(PatternIsType patternIsType) {
        TypeSignature typeSignature = this.copyTypeSignature(patternIsType.getTypeSignature());
        PatternIsType copiedPattern = (PatternIsType) ASTNodeHandle(patternIsType);
        copiedPattern.setTypeSignature(typeSignature);
        return copiedPattern;
    }

    @Override
    protected Expr caseAnnotation(PatternAnnotate patternAnnotate) {
        ast.List<Selector> newSelectorList = patternAnnotate.getSelectorList().stream()
                .map(selector -> {
                    if (selector instanceof SelectorSimple) {
                        Name selectorName = (Name) ASTNodeHandle(((SelectorSimple) selector).getElement());
                        SelectorSimple copiedSelector = (SelectorSimple) ASTNodeHandle(selector);
                        copiedSelector.setElement(selectorName);
                        return copiedSelector;
                    } else if (selector instanceof SelectorCompound) {
                        ast.List<Name> selectorList = ((SelectorCompound) selector).getElementList().stream()
                                .map(elementSelector -> (Name) ASTNodeHandle(elementSelector))
                                .collect(new ASTListCollector<>());
                        SelectorCompound copiedSelector = (SelectorCompound) ASTNodeHandle(selector);
                        copiedSelector.setElementList(selectorList);
                        return copiedSelector;
                    } else {
                        /* control flow should not reach here */
                        throw new RuntimeException();
                    }
                })
                .collect(new ASTListCollector<>());
        Name identifier = (Name) ASTNodeHandle(patternAnnotate.getIdentifier());

        PatternAnnotate copiedPattern = (PatternAnnotate) ASTNodeHandle(patternAnnotate);
        copiedPattern.setIdentifier(identifier);
        copiedPattern.setSelectorList(newSelectorList);
        return copiedPattern;
    }

    private FullSignature copyFullSignature(FullSignature fullSignature) {
        if (fullSignature.hasTypeSignature() && fullSignature.hasTypeSignature()) {
            TypeSignature typeSignature = this.copyTypeSignature(fullSignature.getTypeSignature());
            DimensionSignature dimensionSignature = this.copyDimensionSignature(fullSignature.getDimensionSignature());
            FullSignature copiedSignature = (FullSignature) ASTNodeHandle(fullSignature);
            copiedSignature.setTypeSignature(typeSignature);
            copiedSignature.setDimensionSignature(dimensionSignature);
            return copiedSignature;
        } else if (!fullSignature.hasTypeSignature() && fullSignature.hasDimensionSignature()) {
            DimensionSignature dimensionSignature = this.copyDimensionSignature(fullSignature.getDimensionSignature());
            FullSignature copiedSignature = (FullSignature) ASTNodeHandle(fullSignature);
            copiedSignature.setDimensionSignature(dimensionSignature);
            return copiedSignature;
        } else if (fullSignature.hasTypeSignature()  && !fullSignature.hasTypeSignature()) {
            TypeSignature typeSignature = this.copyTypeSignature(fullSignature.getTypeSignature());
            FullSignature copiedSignature = (FullSignature) ASTNodeHandle(fullSignature);
            copiedSignature.setTypeSignature(typeSignature);
            return copiedSignature;
        } else if (!fullSignature.hasTypeSignature() && !fullSignature.hasTypeSignature()) {
            FullSignature copiedSignature = (FullSignature) ASTNodeHandle(fullSignature);
            return copiedSignature;
        } else {
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }

    private TypeSignature copyTypeSignature(TypeSignature typeSignature) {
        Name type = (Name) ASTNodeHandle(typeSignature.getType());
        TypeSignature copiedTypeSignature = (TypeSignature) ASTNodeHandle(typeSignature);
        typeSignature.setType(type);
        return copiedTypeSignature;
    }

    private DimensionSignature copyDimensionSignature(DimensionSignature dimensionSignature) {
        ast.List<Name> newDimensionList = dimensionSignature.getDimensionList().stream()
                .map(dimension -> (Name) ASTNodeHandle(dimension))
                .collect(new ASTListCollector<>());
        DimensionSignature copiedDimensionSignature = (DimensionSignature) ASTNodeHandle(dimensionSignature);
        copiedDimensionSignature.setDimensionList(newDimensionList);
        return copiedDimensionSignature;
    }

    @Override
    protected Expr caseCall(PatternCall patternCall) {
        ast.List<FullSignature> newInputSignatureList = patternCall.getInput().getFullSignatureList().stream()
                .map(this::copyFullSignature)
                .collect(new ASTListCollector<>());
        Input copiedInput = (Input) ASTNodeHandle(patternCall.getInput());
        copiedInput.setFullSignatureList(newInputSignatureList);

        ast.List<FullSignature> newOutputSignatureList = patternCall.getOutput().getFullSignatureList().stream()
                .map(this::copyFullSignature)
                .collect(new ASTListCollector<>());
        Output copiedOutput = (Output) ASTNodeHandle(patternCall.getOutput());
        copiedOutput.setFullSignatureList(newOutputSignatureList);

        Name identifier = (Name) ASTNodeHandle(patternCall.getIdentifier());

        PatternCall copiedPattern = (PatternCall) ASTNodeHandle(patternCall);
        copiedPattern.setIdentifier(identifier);
        copiedPattern.setInput(copiedInput);
        copiedPattern.setOutput(copiedOutput);

        return copiedPattern;
    }

    @Override
    protected Expr caseExecution(PatternExecution patternExecution) {
        ast.List<FullSignature> newInputSignatureList = patternExecution.getInput().getFullSignatureList().stream()
                .map(this::copyFullSignature)
                .collect(new ASTListCollector<>());
        Input copiedInput = (Input) ASTNodeHandle(patternExecution.getInput());
        copiedInput.setFullSignatureList(newInputSignatureList);

        ast.List<FullSignature> newOutputSignatureList = patternExecution.getOutput().getFullSignatureList().stream()
                .map(this::copyFullSignature)
                .collect(new ASTListCollector<>());
        Output copiedOutput = (Output) ASTNodeHandle(patternExecution.getOutput());
        copiedOutput.setFullSignatureList(newOutputSignatureList);

        Name identifier = (Name) ASTNodeHandle(patternExecution.getIdentifier());

        PatternExecution copiedPattern = (PatternExecution) ASTNodeHandle(patternExecution);
        copiedPattern.setIdentifier(identifier);
        copiedPattern.setInput(copiedInput);
        copiedPattern.setOutput(copiedOutput);

        return copiedPattern;
    }

    @Override
    protected Expr caseGet(PatternGet patternGet) {
        if (patternGet.hasFullSignature()) {
            Name identifier = (Name) ASTNodeHandle(patternGet.getIdentifier());
            FullSignature copiedFullSignature = this.copyFullSignature(patternGet.getFullSignature());

            PatternGet copiedPattern = (PatternGet) ASTNodeHandle(patternGet);
            copiedPattern.setIdentifier(identifier);
            copiedPattern.setFullSignature(copiedFullSignature);

            return copiedPattern;
        } else {
            Name identifier = (Name) ASTNodeHandle(patternGet.getIdentifier());

            PatternGet copiedPattern = (PatternGet) ASTNodeHandle(patternGet);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        }
    }

    @Override
    protected Expr caseLoop(PatternLoop patternLoop) {
        if (patternLoop.hasType()) {
            Name type = (Name) ASTNodeHandle(patternLoop.getType());
            Name identifier = (Name) ASTNodeHandle(patternLoop.getIdentifier());

            PatternLoop copiedPattern = (PatternLoop) ASTNodeHandle(patternLoop);
            copiedPattern.setType(type);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        } else {
            Name identifier = (Name) ASTNodeHandle(patternLoop.getIdentifier());

            PatternLoop copiedPattern = (PatternLoop) ASTNodeHandle(patternLoop);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        }
    }

    @Override
    protected Expr caseLoopBody(PatternLoopBody patternLoopBody) {
        if (patternLoopBody.hasType()) {
            Name type = (Name) ASTNodeHandle(patternLoopBody.getType());
            Name identifier = (Name) ASTNodeHandle(patternLoopBody.getIdentifier());

            PatternLoopBody copiedPattern = (PatternLoopBody) ASTNodeHandle(patternLoopBody);
            copiedPattern.setType(type);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        } else {
            Name identifier = (Name) ASTNodeHandle(patternLoopBody.getIdentifier());

            PatternLoopBody copiedPattern = (PatternLoopBody) ASTNodeHandle(patternLoopBody);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        }
    }

    @Override
    protected Expr caseLoopHead(PatternLoopHead patternLoopHead) {
        if (patternLoopHead.hasType()) {
            Name type = (Name) ASTNodeHandle(patternLoopHead.getType());
            Name identifier = (Name) ASTNodeHandle(patternLoopHead.getIdentifier());

            PatternLoopHead copiedPattern = (PatternLoopHead) ASTNodeHandle(patternLoopHead);
            copiedPattern.setType(type);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        } else {
            Name identifier = (Name) ASTNodeHandle(patternLoopHead.getIdentifier());

            PatternLoopHead copiedPattern = (PatternLoopHead) ASTNodeHandle(patternLoopHead);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        }
    }

    @Override
    protected Expr caseMainExecution(PatternMainExecution patternMainExecution) {
        PatternMainExecution copiedSignature = (PatternMainExecution) ASTNodeHandle(patternMainExecution);
        return copiedSignature;
    }

    @Override
    protected Expr caseOperator(PatternOperator patternOperator) {
        Name type = (Name) ASTNodeHandle(patternOperator.getType());

        ast.List<FullSignature> newFullSignatureList = patternOperator.getFullSignatureList().stream()
                .map(this::copyFullSignature)
                .collect(new ASTListCollector<>());

        PatternOperator copiedSignature = (PatternOperator) ASTNodeHandle(patternOperator);
        copiedSignature.setType(type);
        copiedSignature.setFullSignatureList(newFullSignatureList);

        return copiedSignature;
    }

    @Override
    protected Expr caseSet(PatternSet patternSet) {
        if (patternSet.hasFullSignature()) {
            Name identifier = (Name) ASTNodeHandle(patternSet.getIdentifier());
            FullSignature copiedFullSignature = this.copyFullSignature(patternSet.getFullSignature());

            PatternSet copiedPattern = (PatternSet) ASTNodeHandle(patternSet);
            copiedPattern.setIdentifier(identifier);
            copiedPattern.setFullSignature(copiedFullSignature);

            return copiedPattern;
        } else {
            Name identifier = (Name) ASTNodeHandle(patternSet.getIdentifier());

            PatternSet copiedPattern = (PatternSet) ASTNodeHandle(patternSet);
            copiedPattern.setIdentifier(identifier);

            return copiedPattern;
        }
    }
}

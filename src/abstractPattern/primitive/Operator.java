package abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import abstractPattern.signature.FullSignature;
import abstractPattern.utils.OperatorType;
import ast.PatternOperator;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.javatuples.Unit;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/** an abstract representation on the operator patternExpand */
public final class Operator extends Primitive {
    private final OperatorType operatorType;
    private final Tuple operandSignautre;

    /**
     * construct from {@link PatternOperator} AST node. If the operator patternExpand do not provide enough operand
     * signature, it will automatically expands it with empty signature
     * @param patternOperator {@link PatternOperator} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if {@code patternOperator} do not have a operator type signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Operator(PatternOperator patternOperator, String enclosingFilename) {
        super(patternOperator, enclosingFilename);

        assert originalPattern instanceof PatternOperator;
        operatorType = OperatorType.fromString(
                Optional.ofNullable(((PatternOperator) originalPattern).getType())
                        .orElseThrow(IllegalArgumentException::new)
                        .getID()
        );

        if (operatorType.getNumOperands() == 1) {
            ast.FullSignature rawOperandSignature = Optional
                    .ofNullable(((PatternOperator) originalPattern).getFullSignature(0))
                    .orElseGet(ast.FullSignature::new);
            operandSignautre = new Unit<>(new FullSignature(rawOperandSignature, enclosingFilename));
        } else {
            assert ((PatternOperator) originalPattern).getNumFullSignature() == 2;
            ast.FullSignature rawLHSSignature = Optional
                    .ofNullable(((PatternOperator) originalPattern).getFullSignature(0))
                    .orElseGet(ast.FullSignature::new);
            ast.FullSignature rawRHSSignature = Optional
                    .ofNullable(((PatternOperator) originalPattern).getFullSignature(1))
                    .orElseGet(ast.FullSignature::new);
            operandSignautre = new Pair<>(
                    new FullSignature(rawLHSSignature, enclosingFilename),
                    new FullSignature(rawRHSSignature, enclosingFilename)
            );
        }
    }

    /**
     * perform a structural weeding on the operator patternExpand, it will:
     * <ul>
     *     <li>raise error if any of the operand type signature use [..] wildcard </li>
     *     <li>raise error if operator type signature use [..] wildcard</li>
     *     <li>raise error if too much operand signatures are provided</li>
     * </ul>
     * @return structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = new Report();
        assert originalPattern instanceof PatternOperator;
        if ("..".equals(((PatternOperator) originalPattern).getType().getID())) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in operator patternExpand for operator type, use [*] instead"
            );
        }
        for (Object signature : operandSignautre.toList()) {
            assert signature instanceof FullSignature;
            if ("..".equals(((FullSignature) signature).getTypeSignature().getSignature())) {
                report.AddError(
                        enclosingFilename,
                        startLineNumber, startColumnNumber,
                        "wildcard [..] is not a valid matcher in operator patternExpand for operand type, use [*] instead"
                );
            }
        }
        if (((PatternOperator) originalPattern).getNumFullSignature() > operatorType.getNumOperands()) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    String.format(
                            "too much operands signature, expecting %d operands, but %d found",
                            operatorType.getNumOperands(),
                            ((PatternOperator) originalPattern).getNumFullSignature()
                    )
            );
        }
        return report;
    }

    @Override
    public String toString() {
        StringBuilder operandBuffer = new StringBuilder();
        List<FullSignature> operandSignatureList = new LinkedList<>();
        operandSignautre.toList().stream()
                .forEachOrdered(signature -> operandSignatureList.add((FullSignature) signature));
        for (int index = 0; index < operandSignatureList.size(); index++) {
            operandBuffer.append(operandSignatureList.get(index).toString());
            if (index + 1 < operandSignatureList.size()) operandBuffer.append(", ");
        }
        return getModifierToString(
                String.format("op(%s:%s)", operatorType.toString(), operandBuffer.toString())
        );
    }
}

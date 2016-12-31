package aspectMATLAB.abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import aspectMATLAB.abstractPattern.signature.FullSignature;
import ast.PatternExecution;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/** an abstract representation on the execution patternExpand */
public final class Execution extends Primitive {
    private final String identifier;
    private final List<FullSignature> inputSignatureList = new LinkedList<>();
    private final List<FullSignature> outputSignatureList = new LinkedList<>();

    /**
     * construct from {@link PatternExecution} AST node. If the AST node do not have a output signature, it will use a
     * empty signature list as default, which match to exactly zero output values.
     * @param patternExecution {@link PatternExecution} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if the call patternExpand do have a identifier name signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Execution(PatternExecution patternExecution, String enclosingFilename) {
        super(patternExecution, enclosingFilename);

        assert originalPattern instanceof PatternExecution;
        identifier = Optional
                .ofNullable(((PatternExecution) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();

        ((PatternExecution) originalPattern).getInput().getFullSignatureList().stream().forEachOrdered(fullSignature ->
                inputSignatureList.add(new FullSignature(fullSignature, enclosingFilename))
        );
        ((PatternExecution) originalPattern).getOutput().getFullSignatureList().stream().forEachOrdered(fullSignature ->
                outputSignatureList.add(new FullSignature(fullSignature, enclosingFilename))
        );
    }

    /**
     * perform a structural weeding on execution patternExpand, it will:
     * <ul>
     *     <li>merging structural weeding result from input signatures,</li>
     *     <li>merging structural weeding result from output signatures,</li>
     *     <li>raise error if [..] wildcard is used as a identifier signature</li>
     * </ul>
     * @return the structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = new Report();
        inputSignatureList.forEach(inputSignature ->
                report.AddRange(inputSignature.getStructureValidationReport())
        );
        outputSignatureList.forEach(outputSignature ->
                report.AddRange(outputSignature.getStructureValidationReport())
        );
        if ("".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in execution patternExpand for identifier name, use [*] instead"
            );
        }
        return report;
    }

    @Override
    public String toString() {
        StringBuilder inputSignatureBuffer = new StringBuilder();
        for (int index = 0; index < inputSignatureList.size(); index++) {
            inputSignatureBuffer.append(inputSignatureList.get(index).toString());
            if (index + 1 < inputSignatureList.size()) inputSignatureBuffer.append(", ");
        }
        StringBuilder outputSignatureBuffer = new StringBuilder();
        for (int index = 0; index < outputSignatureList.size(); index++) {
            outputSignatureBuffer.append(outputSignatureList.get(index).toString());
            if (index + 1 < outputSignatureList.size()) outputSignatureBuffer.append(", ");
        }
        return getModifierToString(
                String.format("execution(%s(%s):%s)", identifier, inputSignatureBuffer, outputSignatureBuffer)
        );
    }
}

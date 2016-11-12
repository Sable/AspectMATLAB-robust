package abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import abstractPattern.signature.FullSignature;
import ast.*;
import natlab.toolkits.analysis.varorfun.VFDatum;
import org.javatuples.Pair;
import transformer.TransformQueryEnv;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** an abstract representation on the call patternExpand */
public final class Call extends Primitive {
    private final String identifier;
    private final List<FullSignature> inputSignatureList = new LinkedList<>();
    private final List<FullSignature> outputSignatureList = new LinkedList<>();

    /**
     * construct from {@link PatternCall} AST node. If the AST node do not have a output signature, it will use a
     * empty signature list as default, which match to exactly zero output values.
     * @param patternCall {@link PatternCall} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if the call patternExpand do have a identifier name signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Call(PatternCall patternCall, String enclosingFilename) {
        super(patternCall, enclosingFilename);

        assert originalPattern instanceof PatternCall;

        identifier = Optional
                .ofNullable(((PatternCall) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();

        ((PatternCall) originalPattern).getInput().getFullSignatureList().stream().forEachOrdered(fullSignature ->
            inputSignatureList.add(new FullSignature(fullSignature, enclosingFilename))
        );
        ((PatternCall) originalPattern).getOutput().getFullSignatureList().stream().forEachOrdered(fullSignature ->
            outputSignatureList.add(new FullSignature(fullSignature, enclosingFilename))
        );
    }

    /**
     * perform a structural weeding on call patternExpand, it will:
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
        if ("..".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in call patternExpand for identifier name, use [*] instead"
            );
        }
        return report;
    }

    private boolean isPossibleJoinPointNameExpr(NameExpr nameExpr, TransformQueryEnv transformQueryEnv) {
        Name name = Optional.ofNullable(nameExpr).orElseThrow(NullPointerException::new).getName();

        VFDatum kindAnalysisResult = transformQueryEnv.kindAnalysis.getResult(name);
        if (kindAnalysisResult.isFunction()) {

        }
        if (kindAnalysisResult.isVariable()) {

        }
        if (kindAnalysisResult.isID()) {

        }
        return false;
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
                String.format("call(%s(%s):%s)", identifier, inputSignatureBuffer, outputSignatureBuffer)
        );
    }
}

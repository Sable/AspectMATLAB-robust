package aspectMATLAB.abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import aspectMATLAB.abstractPattern.utils.LoopType;
import ast.Name;
import ast.PatternLoop;
import ast.PatternLoopBody;

import java.util.Optional;

/** an abstract representation on the loop body patternExpand */
public final class LoopBody extends Primitive {
    private final LoopType loopType;
    private final String identifier;

    /**
     * construct from {@link PatternLoopBody} AST node. If the loop patternExpand do not provide a loop type signature, the
     * patternExpand will automatically resolve it as a star[*] wildcard.
     * @param patternLoopBody {@link PatternLoopBody} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if {@code patternLoopBody} do not have a loop type signature
     * @throws IllegalArgumentException if {@code patternLoopBody} do not have a loop name signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public LoopBody(PatternLoopBody patternLoopBody, String enclosingFilename) {
        super(patternLoopBody, enclosingFilename);

        assert originalPattern instanceof PatternLoopBody;
        identifier = Optional
                .ofNullable(((PatternLoop) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();
        loopType = LoopType.fromString(Optional
                .ofNullable(((PatternLoop) originalPattern).getType())
                .orElse(new Name("*"))
                .getID());
    }

    /**
     * perform structural weeding on the loop body patternExpand, it will:
     * <ul>
     *     <li>raise error if the patternExpand type signature use {@code [..]} wildcard,</li>
     *     <li>raise error if the patternExpand name signature use {@code [..]} wildcard</li>
     * </ul>
     * @return structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = new Report();
        assert originalPattern instanceof PatternLoopBody;
        if ("".equals(((PatternLoopBody) originalPattern).getType().getID())) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in loop body patternExpand for loop type, use [*] instead"
            );
        }
        if ("".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in loop body patternExpand for loop name, use [*] instead"
            );
        }
        return report;
    }

    @Override
    public String toString() {
        return getModifierToString(String.format("loopbody(%s:%s)", loopType.toString(), identifier));
    }
}

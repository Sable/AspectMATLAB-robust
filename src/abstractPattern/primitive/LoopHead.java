package abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import abstractPattern.utils.LoopType;
import ast.Name;
import ast.PatternLoopHead;

import java.util.Optional;

/** an abstract representation on the loop head patternExpand */
public final class LoopHead extends Primitive {
    private final LoopType loopType;
    private final String identifier;

    /**
     * construct from {@link PatternLoopHead} AST node. If the loop patternExpand do not provide a loop type signature, the
     * patternExpand will automatically resolve it as a star[*] wildcard.
     * @param patternLoopHead {@link PatternLoopHead} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if {@code patternLoopHead} do not have a loop name signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public LoopHead(PatternLoopHead patternLoopHead, String enclosingFilename) {
        super(patternLoopHead, enclosingFilename);

        assert originalPattern instanceof PatternLoopHead;
        identifier = Optional
                .ofNullable(((PatternLoopHead) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();
        loopType = LoopType.fromString(Optional
                .ofNullable(((PatternLoopHead) originalPattern).getType())
                .orElse(new Name("*"))
                .getID());
    }

    /**
     * perform structural weeding on the loop head patternExpand, it will:
     * <ul>
     *     <li>raise error if the patternExpand type signature use {@code [..]} wildcard,</li>
     *     <li>raise error if the patternExpand name signature use {@code [..]} wildcard</li>
     * </ul>
     * @return structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = new Report();
        assert originalPattern instanceof PatternLoopHead;
        if ("..".equals(((PatternLoopHead) originalPattern).getType().getID())) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in loop head patternExpand for loop type, use [*] instead"
            );
        }
        if ("..".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in loop head patternExpand for loop name, use [*] instead"
            );
        }
        return report;
    }

    @Override
    public String toString() {
        return getModifierToString(String.format("loophead(%s:%s)", loopType.toString(), identifier));
    }
}

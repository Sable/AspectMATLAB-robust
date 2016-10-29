package abstractPattern.primitive;

import Matlab.Utils.IReport;
import abstractPattern.signature.FullSignature;
import ast.PatternSet;

import java.util.Optional;

/** an abstract representation on the get patternExpand */
public final class Set extends Primitive {
    private final FullSignature fullSignature;
    private final String identifier;

    /**
     * construct from {@link PatternSet} AST node. If the patternExpand do not provide a full signature, will create a full
     * signature with empty shape patternExpand and empty type patternExpand.
     * @param patternSet {@link PatternSet} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if set patternExpand do not have a identifier name signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Set(PatternSet patternSet, String enclosingFilename) {
        super(patternSet, enclosingFilename);

        assert originalPattern instanceof PatternSet;
        identifier = Optional
                .ofNullable(((PatternSet) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();
        fullSignature = new FullSignature(
                Optional.ofNullable(((PatternSet) originalPattern).getFullSignature())
                        .orElseGet(ast.FullSignature::new),
                enclosingFilename
        );
    }

    /**
     * perform a structural weeding on the set patternExpand, it will:
     * <ul>
     *     <li>raises error if identifier using [..] wildcard,</li>
     *     <li>raises error if type using [..] wildcard,</li>
     *     <li>merge the validation report from full signature</li>
     * </ul>
     * @return structural weeding report on set patternExpand
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = fullSignature.getStructureValidationReport();
        if ("..".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in set patternExpand for identifier name, use [*] instead"
            );
        }
        if ("..".equals(fullSignature.getTypeSignature().getSignature())) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in set patternExpand for type name, use [*] instead"
            );
        }
        return report;
    }

    @Override
    public String toString() {
        return getModifierToString(String.format("set(%s:%s)", identifier, fullSignature.toString()));
    }
}

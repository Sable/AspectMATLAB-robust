package aspectMATLAB.abstractPattern.modifier;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import aspectMATLAB.abstractPattern.utils.ScopeType;
import ast.Name;
import ast.PatternWithin;
import aspectMATLAB.utils.MergableHashSet;

import java.util.Optional;

/** a abstract representation on the scope patternExpand */
public final class Scope extends Modifier {
    private final ScopeType scopeType;
    private final String scopeName;

    /**
     * construct from a {@link PatternWithin} AST node. If the patternExpand do not specific a scope type, use {@code [*]}
     * wildcard instead.
     * @param patternWithin {@link PatternWithin} AST node
     * @param enclosingFilename enclosing file path
     * @throws IllegalArgumentException if {@code patternWithin} do not specific a valid scope name
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Scope(PatternWithin patternWithin, String enclosingFilename) {
        super(patternWithin, enclosingFilename);

        assert originalPattern instanceof PatternWithin;
        scopeType = ScopeType.valueOf(Optional
                .ofNullable(((PatternWithin) originalPattern).getType())
                .orElse(new Name("*"))
        );
        scopeName = Optional
                .ofNullable(((PatternWithin) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();
    }

    /**
     * perform a structural weeding on the scope patternExpand, it will:
     * <ul>
     *     <li>raise errors if {@code [..]} wildcard is used as a scope name,</li>
     *     <li>raise errors if {@code [..]} wildcard is used as a identifier name</li>
     * </ul>
     * @return the structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = new Report();
        assert originalPattern instanceof PatternWithin;
        assert ((PatternWithin) originalPattern).getType() != null;
        assert ((PatternWithin) originalPattern).getIdentifier() != null;
        if ("".equals(((PatternWithin) originalPattern).getType().getID())) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in scope patternExpand for scope type, use [*] instead"
            );
        }
        if ("".equals(scopeName)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in scope patternExpand for scope name, use [*] instead"
            );
        }
        return report;
    }

    /**
     * @see Modifier#getModifierTypeSet()
     * @return a set contains the java class signature for the current modifier patternExpand
     */
    @Override
    public MergableHashSet<Class<? extends Modifier>> getModifierTypeSet() {
        MergableHashSet<Class<? extends Modifier>> retSet = new MergableHashSet<>();
        retSet.add(Scope.class);
        return retSet;
    }

    @Override
    public String toString() {
        return String.format("within(%s:%s)", scopeType.toString(), scopeName);
    }
}

package aspectMATLAB.abstractPattern.modifier;

import Matlab.Utils.IReport;
import aspectMATLAB.abstractPattern.signature.TypeSignature;
import ast.PatternIsType;
import aspectMATLAB.utils.MergableHashSet;

import java.util.Optional;

/** a abstract representation on the type patternExpand */
public final class Type extends Modifier {
    private final TypeSignature typeSignature;

    /**
     * construct from a {@link PatternIsType} AST Node
     * @param patternType {@link PatternIsType} AST Node
     * @param enclosingFile enclosing aspect file path
     * @throws IllegalArgumentException if {@code patternType} do not have a type signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Type(PatternIsType patternType, String enclosingFile) {
        super(patternType, enclosingFile);

        assert originalPattern instanceof PatternIsType;
        typeSignature = new TypeSignature(
                Optional.ofNullable(((PatternIsType) originalPattern).getTypeSignature())
                        .orElseThrow(IllegalArgumentException::new),
                enclosingFile
        );
    }

    /**
     * preform a structural weeding on the type patternExpand, it will:
     * <ul>
     *     <li>raise errors if the type patternExpand uses {@code [..]} wildcards</li>
     * </ul>
     * @return the structural wedding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport retReport = typeSignature.getStructureValidationReport();
        if ("".equals(typeSignature.getSignature())) {
            retReport.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in type patternExpand for type name, use [*] instead"
            );
        }
        return retReport;
    }

    /**
     * @see Modifier#getModifierTypeSet()
     * @return a set contains the java class signature for the current modifier patternExpand
     */
    @Override
    public MergableHashSet<Class<? extends Modifier>> getModifierTypeSet() {
        MergableHashSet<Class<? extends Modifier>> retSet = new MergableHashSet<>();
        retSet.add(Type.class);
        return retSet;
    }

    @Override
    public String toString() {
        return String.format("istype(%s)", typeSignature.toString());
    }
}

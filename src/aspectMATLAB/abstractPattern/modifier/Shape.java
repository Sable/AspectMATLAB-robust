package aspectMATLAB.abstractPattern.modifier;

import Matlab.Utils.IReport;
import aspectMATLAB.abstractPattern.signature.ShapeSignature;
import ast.PatternDimension;
import aspectMATLAB.utils.MergableHashSet;

import java.util.Optional;

/** a abstract representation on the shape patternExpand */
public final class Shape extends Modifier {
    private final ShapeSignature shapeSignature;

    /**
     * construct from a {@link PatternDimension} AST node.
     * @param patternDimension {@link PatternDimension} node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if {@code patternDimension} do not have a dimension signature list
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Shape(PatternDimension patternDimension, String enclosingFilename) {
        super(patternDimension, enclosingFilename);

        assert originalPattern instanceof PatternDimension;
        shapeSignature = new ShapeSignature(
                Optional.ofNullable(((PatternDimension) originalPattern).getDimensionSignature())
                        .orElseThrow(IllegalArgumentException::new),
                enclosingFilename
        );
    }

    /**
     * the structural weeding report on the shape patternExpand, it will simply return the weeding report on the shape
     * signature.
     * @return  the structural weeding report */
    @Override
    public IReport getStructureValidationReport() {
        return shapeSignature.getStructureValidationReport();
    }

    /**
     * @see Modifier#getModifierTypeSet()
     * @return a set contains the java class signature for the current modifier patternExpand
     */
    @Override
    public MergableHashSet<Class<? extends Modifier>> getModifierTypeSet() {
        MergableHashSet<Class<? extends Modifier>> retSet = new MergableHashSet<>();
        retSet.add(Shape.class);
        return retSet;
    }

    @Override
    public String toString() {
        return String.format("dimension(%s)", shapeSignature.toString());
    }
}

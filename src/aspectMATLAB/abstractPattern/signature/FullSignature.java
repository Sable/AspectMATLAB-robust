package aspectMATLAB.abstractPattern.signature;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import aspectMATLAB.abstractPattern.Pattern;
import ast.DimensionSignature;

/** an abstract representation on the full signature */
public final class FullSignature extends Pattern {
    private final ShapeSignature shapeSignature;
    private final TypeSignature typeSignature;

    /**
     * construct from {@link FullSignature} AST node. If the full signature do not have a dimension signature, it
     * will insert a empty signature, and if the full signature do not have a type signature, it will also a insert
     * a empty signature.
     * @param fullSignature {@link FullSignature} AST node
     * @param enclosingFilename enclosing aspect file path
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public FullSignature(ast.FullSignature fullSignature, String enclosingFilename) {
        super(fullSignature, enclosingFilename);
        assert originalPattern instanceof ast.FullSignature;
        if (!((ast.FullSignature) originalPattern).hasDimensionSignature()) {
            ((ast.FullSignature) originalPattern).setDimensionSignature(new DimensionSignature());
        }
        if (!((ast.FullSignature) originalPattern).hasTypeSignature()) {
            ((ast.FullSignature) originalPattern).setTypeSignature(new ast.TypeSignature());
        }
        shapeSignature = new ShapeSignature(((ast.FullSignature) originalPattern).getDimensionSignature(),
                enclosingFilename);
        typeSignature = new TypeSignature(((ast.FullSignature) originalPattern).getTypeSignature(), enclosingFilename);
    }

    /** @return the shape part of this full signature */
    public ShapeSignature getShapeSignature() {
        return shapeSignature;
    }

    /** @return the type part of this full signature */
    public TypeSignature getTypeSignature() {
        return typeSignature;
    }

    /** @return the structural validation report on full signature by concatenating shape part and type part */
    @Override
    public IReport getStructureValidationReport() {
        IReport typeReport  = getTypeSignature().getStructureValidationReport();
        IReport shapeReport = getShapeSignature().getStructureValidationReport();

        IReport ratReport = new Report();
        ratReport.AddRange(typeReport);
        ratReport.AddRange(shapeReport);
        return ratReport;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getTypeSignature().toString())
                .append(getShapeSignature().toString())
                .toString();
    }
}

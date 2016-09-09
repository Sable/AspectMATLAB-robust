package abstractPattern.signature;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import abstractPattern.Pattern;
import ast.Name;

/** an abstract representation on the type signature */
public final class TypeSignature extends Pattern {
    /**
     * construct from {@link TypeSignature} AST node.
     * @param typeSignature  {@link TypeSignature} AST node
     * @param enclosingFilename enclosing aspect file path
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public TypeSignature(ast.TypeSignature typeSignature, String enclosingFilename) {
        super(typeSignature, enclosingFilename);
    }

    /**
     * get the type signature from the type pattern, if the type pattern is empty, it will return trivial pattern {@code
     * [*]}
     * @return the type signature
     */
    public String getSignature() {
        assert originalPattern instanceof ast.TypeSignature;
        if (((ast.TypeSignature) originalPattern).getType() == null) {
            return "*";
        }
        Name signatureName = ((ast.TypeSignature) originalPattern).getType();
        return signatureName.getID();
    }

    /**
     * determine if such pattern is a trivial pattern, in this case, the [*] wildcard will be consider as a trivial
     * pattern.
     * @return {@code true} if the pattern is a trivial pattern, otherwise {@code false}
     */
    public boolean isTrivial() {
        String signature = getSignature();
        return signature.equals("*");
    }

    /**
     * we leave the type weeding to the type pattern, and get/set pattern, this method will always return a empty
     * report
     * @return the structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        return new Report();
    }

    @Override
    public String toString() {
        return getSignature().toString();
    }
}

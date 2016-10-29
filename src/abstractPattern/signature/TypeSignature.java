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
     * get the type signature from the type patternExpand, if the type patternExpand is empty, it will return trivial patternExpand {@code
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
     * determine if such patternExpand is a trivial patternExpand, in this case, the [*] wildcard will be consider as a trivial
     * patternExpand.
     * @return {@code true} if the patternExpand is a trivial patternExpand, otherwise {@code false}
     */
    public boolean isTrivial() {
        String signature = getSignature();
        return signature.equals("*");
    }

    /**
     * we leave the type weeding to the type patternExpand, and get/set patternExpand, this method will always return a empty
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

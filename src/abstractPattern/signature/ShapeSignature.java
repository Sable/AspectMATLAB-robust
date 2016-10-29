package abstractPattern.signature;

import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Report;
import Matlab.Utils.Severity;
import abstractPattern.Pattern;
import ast.DimensionSignature;
import ast.Name;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** an abstract representation on the shape signature */
public final class ShapeSignature extends Pattern implements Iterable<String> {

    /**
     * construct from {@link DimensionSignature} AST node.
     * @param dimensionSignature  {@link DimensionSignature} AST node
     * @param enclosingFilename enclosing aspect file path
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public ShapeSignature(DimensionSignature dimensionSignature, String enclosingFilename) {
        super(dimensionSignature, enclosingFilename);
    }

    /**
     * if such shape signature is a empty signature, i.e. {@code []}, it will return {@code [..]} instread
     * @return shape signature
     * */
    public List<String> getSignature() {
        assert originalPattern instanceof DimensionSignature;
        List<String> signature = new LinkedList<>();
        for (Name shapeName : ((DimensionSignature) originalPattern).getDimensionList()) {
            String shapeString = shapeName.getID();
            signature.add(shapeString);
        }
        if (signature.isEmpty()) signature.add("..");
        return signature;
    }

    /**
     * determine if a shape signature is trivial (i.e. no additional runtime checking is required, the patternExpand will
     * match to any matrix shape).
     * @return {@code true} if the shape signature is trivial, otherwise return {@code false}
     */
    public boolean isTrivial() {
        return getSignature().stream().allMatch(shape -> shape.equals(".."));
    }

    /**
     * perform a structure weeding on the shape patternExpand, it will:
     * <ul>
     *     <li> raise warning on redundant patternExpand such as {@code [.., ..]}</li>
     * </ul>
     * @return the structure validation report
     */
    @Override
    public IReport getStructureValidationReport() {
        assert originalPattern instanceof DimensionSignature;
        IReport report = new Report();
        for (int signatureIndex = 0; signatureIndex < ((DimensionSignature) originalPattern).getNumDimension();
             signatureIndex++) {
            Name signatureName = ((DimensionSignature) originalPattern).getDimension(signatureIndex);
            String signature = signatureName.getID();
            if (signature.equals("..") && signatureIndex+1 < ((DimensionSignature) originalPattern).getNumDimension()) {
                Name nextSignatureName = ((DimensionSignature) originalPattern).getDimension(signatureIndex + 1);
                String nextSignature = nextSignatureName.getID();
                if (nextSignature.equals("..")) {
                    Message redundantWarning = new Message(
                            Severity.Warning,
                            enclosingFilename,
                            signatureName.getStartLine(),
                            signatureName.getEndColumn(),
                            "redundant patternExpand, using [..] instead of [.., ..]"
                    );
                    report.Add(redundantWarning);
                }
            }
        }
        return report;
    }

    /**
     * @return iterator over the signature
     */
    @Override
    public Iterator<String> iterator() {
        return getSignature().iterator();
    }

    @Override
    public String toString() {
        return getSignature().toString();
    }
}

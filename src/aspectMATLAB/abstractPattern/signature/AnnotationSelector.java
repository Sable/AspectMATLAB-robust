package aspectMATLAB.abstractPattern.signature;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import aspectMATLAB.abstractPattern.Pattern;
import ast.Selector;
import ast.SelectorCompound;
import ast.SelectorSimple;

/** an abstract representation on the annotation selector signature */
public abstract class AnnotationSelector extends Pattern {

    /**
     * construct from {@link Selector} AST node
     * @param selector {@link Selector} AST node
     * @param enclosingFilename enclosing aspect file path
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public AnnotationSelector(Selector selector, String enclosingFilename) {
        super(selector, enclosingFilename);
    }

    /**
     * simply return an empty structural weeding report
     * @return empty structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        return new Report();
    }

    /**
     * construct abstract annotation selector from {@link Selector} AST node
     * @param selector {@link Selector} AST node
     * @param enclosingFilename enclosing aspect file path
     * @return constructed {@link AnnotationSelector}
     */
    @SuppressWarnings("deprecation")
    public static AnnotationSelector buildAnnotationSelector(Selector selector, String enclosingFilename) {
        if (selector instanceof SelectorCompound)
            return new AnnotationCompoundSelector((SelectorCompound) selector, enclosingFilename);
        if (selector instanceof SelectorSimple)
            return new AnnotationSimpleSelector((SelectorSimple) selector, enclosingFilename);
        /* control flow should not reach here */
        throw new AssertionError();
    }
}

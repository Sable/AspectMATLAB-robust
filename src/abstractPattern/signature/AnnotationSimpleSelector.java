package abstractPattern.signature;

import abstractPattern.utils.AnnotationSelectorType;
import ast.SelectorSimple;

import java.util.Optional;

/** an abstract representation on the annotation simple selector signature */
public final class AnnotationSimpleSelector extends AnnotationSelector {
    private final AnnotationSelectorType selectorType;

    /**
     * construct from {@link SelectorSimple} AST node
     * @param selectorSimple {@link SelectorSimple} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws NullPointerException if {@link SelectorSimple} do not have a selector signature.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public AnnotationSimpleSelector(SelectorSimple selectorSimple, String enclosingFilename) {
        super(selectorSimple, enclosingFilename);

        assert originalPattern instanceof SelectorSimple;
        selectorType = AnnotationSelectorType.valueOf(
                Optional.ofNullable(((SelectorSimple) originalPattern).getElement())
                        .orElseThrow(NullPointerException::new)
        );
    }

    @Override
    public String toString() {
        return selectorType.toString();
    }
}

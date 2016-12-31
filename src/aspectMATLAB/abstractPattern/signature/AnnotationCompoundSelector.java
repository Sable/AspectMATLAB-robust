package aspectMATLAB.abstractPattern.signature;

import aspectMATLAB.abstractPattern.utils.AnnotationSelectorType;
import ast.Name;
import ast.SelectorCompound;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/** an abstract representation on the annotation compound selector signature */
public final class AnnotationCompoundSelector extends AnnotationSelector {
    private final List<AnnotationSelectorType> selectorTypeList = new LinkedList<>();

    /**
     * construct from {@link SelectorCompound} AST node
     * @param selectorCompound {@link SelectorCompound} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if {@code selectorCompound} do not have a selector signature list
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public AnnotationCompoundSelector(SelectorCompound selectorCompound, String enclosingFilename) {
        super(selectorCompound, enclosingFilename);

        assert originalPattern instanceof SelectorCompound;
        for (Name selector : Optional
                .ofNullable(selectorCompound.getElementList())
                .orElseThrow(IllegalArgumentException::new)) {
            AnnotationSelectorType selectorType = AnnotationSelectorType.valueOf(selector);
            selectorTypeList.add(selectorType);
        }
    }

    @Override
    public String toString() {
        return selectorTypeList.toString();
    }
}

package abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import abstractPattern.signature.AnnotationSelector;
import abstractPattern.signature.AnnotationSimpleSelector;
import ast.PatternAnnotate;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/** an abstract representation on the annotation pattern */
public final class Annotation extends Primitive {
    private final List<AnnotationSelector> selectorList = new LinkedList<>();
    private final String identifier;

    /**
     * construct from {@link PatternAnnotate} AST node
     * @param patternAnnotate {@link PatternAnnotate} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if {@code patternAnnotate} do not have a identifier signature
     * @throws IllegalArgumentException if {@code patternAnnotate} do not have a selector signature list
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Annotation(PatternAnnotate patternAnnotate, String enclosingFilename) {
        super(patternAnnotate, enclosingFilename);

        assert originalPattern instanceof PatternAnnotate;
        identifier = Optional
                .ofNullable(((PatternAnnotate) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();

        Optional.ofNullable(((PatternAnnotate) originalPattern).getSelectorList())
                .orElseThrow(IllegalArgumentException::new)
                .stream().forEachOrdered(selector ->
                selectorList.add(AnnotationSimpleSelector.buildAnnotationSelector(selector, enclosingFilename))
        );
    }

    /**
     * perform a structural weeding on annotation pattern, it will:
     * <ul>
     *     <li>merge structural weeding report from selectors,</li>
     *     <li>raise error if annotation identifier signature use [..] wildcard</li>
     * </ul>
     * @return structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = new Report();
        selectorList.stream().forEachOrdered(selector -> report.AddRange(selector.getStructureValidationReport()));
        if ("..".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in annotation pattern for annotation name, use [*] instead"
            );
        }
        return report;
    }

    @Override
    public String toString() {
        StringBuilder selectorBuffer = new StringBuilder();
        for (int index = 0; index < selectorList.size(); index++) {
            selectorBuffer.append(selectorList.get(index).toString());
            if (index + 1 < selectorList.size()) selectorBuffer.append(", ");
        }
        return getModifierToString(String.format("annotate(%s(%s))", identifier, selectorBuffer.toString()));
    }
}

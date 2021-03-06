package aspectMATLAB.abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Report;
import ast.PatternMainExecution;

/** an abstract representation on the main execution patternExpand */
public final class MainExecution extends Primitive {
    /**
     * construct from {@link PatternMainExecution} AST node.
     * @param patternMainExecution {@link PatternMainExecution} AST node
     * @param enclosingFilename enclosing aspect file path
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public MainExecution(PatternMainExecution patternMainExecution, String enclosingFilename) {
        super(patternMainExecution, enclosingFilename);
    }

    /**
     * perform structural weeding on main execution patternExpand, it will also return a report saying such patternExpand is valid
     * @return structural weeding reprot
     */
    @Override
    public IReport getStructureValidationReport() {
        return new Report();
    }

    @Override
    public String toString() {
        return getModifierToString("mainexecution()");
    }
}

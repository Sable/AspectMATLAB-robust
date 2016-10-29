package abstractPattern.modifier;

import Matlab.Utils.IReport;
import ast.Expr;
import ast.NotExpr;
import utils.MergableHashSet;

import java.util.Optional;

/** an abstract representation of not operation on a modifier patternExpand */
public final class ModifierNot extends Modifier {
    private final Modifier operandModifier;

    /**
     * construct not modifier by specific operand operator, the constructor will construct will its own
     * {@link NotExpr} AST node
     * @param operand operand modifier
     * @param enclosingFilename enclosing aspect file path
     * @throws NullPointerException if {@code operand} is {@code null}
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public ModifierNot(Modifier operand, String enclosingFilename) {
        super(
                new NotExpr((Expr) Optional.ofNullable(operand).orElseThrow(NullPointerException::new).getPatternAST()),
                enclosingFilename
        );
        operandModifier = operand;
    }

    /**
     * simply return the operand structural weeding report
     * @return the structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        return operandModifier.getStructureValidationReport();
    }

    /**
     * simply return the operand modifier class set
     * @return the modifier class et
     */
    @Override
    public MergableHashSet<Class<? extends Modifier>> getModifierTypeSet() {
        return operandModifier.getModifierTypeSet();
    }

    /** @return operand modifier */
    public Modifier getOperandModifier() {
        return operandModifier;
    }

    @Override
    public String toString() {
        return String.format("~ %s", operandModifier.toString());
    }
}

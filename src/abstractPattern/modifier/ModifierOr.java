package abstractPattern.modifier;

import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Report;
import ast.Expr;
import ast.OrExpr;
import utils.MergableHashSet;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** an abstract representation on or operation between to modifier patternExpand */
public final class ModifierOr extends Modifier {
    private final Modifier lhsModifier;
    private final Modifier rhsModifier;

    /**
     * construct or modifier by specific left hand side and right hand side modifier patternExpand, the constructor will
     * construct its own {@link OrExpr} AST node.
     * @param lhs left hand side modifier
     * @param rhs right hand side modifier
     * @param enclosingFilename aspect file containing the patternExpand
     * @throws NullPointerException if {@code lhs} or {@code rhs} is {@code null}
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public ModifierOr(Modifier lhs, Modifier rhs, String enclosingFilename) {
        super(
                new OrExpr(
                        (Expr) Optional.ofNullable(lhs).orElseThrow(NullPointerException::new).getPatternAST(),
                        (Expr) Optional.ofNullable(rhs).orElseThrow(NullPointerException::new).getPatternAST()
                ),
                enclosingFilename
        );
        lhsModifier = lhs;
        rhsModifier = rhs;
    }

    /**
     * simply merge the left hand side structural weeding report with the right hand side one
     * @return the merged structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport lhsReport = lhsModifier.getStructureValidationReport();
        IReport rhsReport = rhsModifier.getStructureValidationReport();
        Stream<Message> lhsStream = StreamSupport.stream(lhsReport.spliterator(), true);
        Stream<Message> rhsStream = StreamSupport.stream(rhsReport.spliterator(), true);

        IReport retReport = new Report();
        retReport.AddRange(Stream.concat(lhsStream, rhsStream).collect(Collectors.toList()));
        return retReport;
    }

    /**
     * simply merge the left hand side modifier set with the right hand side modifier set
     * @return the merged modifier set
     */
    @Override
    public MergableHashSet<Class<? extends Modifier>> getModifierTypeSet() {
        MergableHashSet<Class<? extends Modifier>> lhsSet = lhsModifier.getModifierTypeSet();
        MergableHashSet<Class<? extends Modifier>> rhsSet = rhsModifier.getModifierTypeSet();

        return lhsSet.union(rhsSet);
    }

    /** @return left hand side modifier */
    public Modifier getLHSModifier() {
        return lhsModifier;
    }

    /** @return right hand side modifier */
    public Modifier getRHSModifier() {
        return rhsModifier;
    }

    @Override
    public String toString() {
        return String.format("(%s | %s)", lhsModifier.toString(), rhsModifier.toString());
    }
}

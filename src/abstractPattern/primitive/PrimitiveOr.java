package abstractPattern.primitive;

import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Report;
import abstractPattern.modifier.Modifier;
import ast.Expr;
import ast.OrExpr;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** an abstract representation on or operation between to primitive patternExpand */
public final class PrimitiveOr extends Primitive {
    private final Primitive lhsPrimitive;
    private final Primitive rhsPrimitive;

    /**
     * construct or modifier by specific left hand side and right hand side primitive patternExpand, the constructor will
     * construct its own {@link OrExpr} AST node.
     * @param lhs left hand side {@link Primitive} patternExpand
     * @param rhs right hand side {@link Primitive} patternExpand
     * @param enclosingFilename enclosing aspect file path
     * @throws NullPointerException if {@code lhs} or {@code rhs} is {@code null}
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public PrimitiveOr(Primitive lhs, Primitive rhs, String enclosingFilename) {
        super(
                new OrExpr(
                        (Expr) Optional.ofNullable(lhs).orElseThrow(NullPointerException::new).getPatternAST(),
                        (Expr) Optional.ofNullable(rhs).orElseThrow(NullPointerException::new).getPatternAST()
                ),
                enclosingFilename
        );
        lhsPrimitive = lhs;
        rhsPrimitive = rhs;
    }

    /**
     * simply merge the left hand side structural weeding report with the right hand side one
     * @return the merged structural weeding report
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport lhsReport = lhsPrimitive.getStructureValidationReport();
        IReport rhsReport = lhsPrimitive.getStructureValidationReport();
        Stream<Message> lhsStream = StreamSupport.stream(lhsReport.spliterator(), true);
        Stream<Message> rhsStream = StreamSupport.stream(rhsReport.spliterator(), true);

        IReport retReport = new Report();
        retReport.AddRange(Stream.concat(lhsStream, rhsStream).collect(Collectors.toList()));
        return retReport;
    }

    /**
     * apply modifier to both side of the or primitive patternExpand
     * @param modifier {@link Modifier} modifier patterns
     * @return an reference to this or patternExpand
     * @throws NullPointerException if {@code modifier} is {@code null}
     */
    @Override
    public Primitive addModifier(Modifier modifier) {
        lhsPrimitive.addModifier(Optional.ofNullable(modifier).orElseThrow(NullPointerException::new));
        rhsPrimitive.addModifier(Optional.ofNullable(modifier).orElseThrow(NullPointerException::new));
        return this;
    }

    @Override
    public String toString() {
        return getModifierToString(String.format("(%s | %s)", lhsPrimitive.toString(), rhsPrimitive.toString()));
    }
}

package abstractPattern;

import Matlab.Utils.IReport;
import abstractPattern.analysis.PatternType;
import abstractPattern.analysis.PatternTypeAnalysis;
import abstractPattern.modifier.Modifier;
import abstractPattern.primitive.Primitive;
import ast.ASTNode;
import ast.Expr;

import java.util.Optional;

/** a abstract representation on the pattern */
public abstract class Pattern{
    protected final int startColumnNumber;
    protected final int startLineNumber;
    protected final String enclosingFilename;

    protected final ASTNode originalPattern;

    /**
     * build {@code abstractPattern.Pattern} from raw parsed ASTNode
     * @param astNode raw ASTNode from parser
     * @param enclosingFilename enclosing aspect file name
     * @throws NullPointerException if {@code astNode} is {@code null}
     * @throws NullPointerException if {@code enclosingFilename} is {@code null}
     */
    @Deprecated
    public Pattern(ASTNode astNode, String enclosingFilename) {
        originalPattern = Optional.ofNullable(astNode).orElseThrow(NullPointerException::new);
        startLineNumber = originalPattern.getStartLine();
        startColumnNumber = originalPattern.getStartColumn();

        this.enclosingFilename = Optional.ofNullable(enclosingFilename).orElseThrow(NullPointerException::new);
    }

    /** @return the start column number for the pattern */
    public int getStartColumnNumber() {
        return this.startColumnNumber;
    }

    /** @return the start line number for the pattern */
    public int getStartLineNumber() {
        return this.startLineNumber;
    }

    /** @return the enclosing filename for the pattern */
    public String getEnclosingFilename() {
        return this.enclosingFilename;
    }

    /** @return report of checking the structure of the pattern */
    public abstract IReport getStructureValidationReport();

    /** @return the AST representation of the pattern */
    @Deprecated
    public ASTNode getPatternAST() {
        return this.originalPattern;
    }

    /**
     * construct abstract pattern from pattern expression
     * @param patternExpression pattern expression
     * @param enclosingFilename enclosing aspect file path
     * @return constructed abstract pattern
     * @throws NullPointerException if {@code patternExpression} is {@code null}
     * @throws IllegalArgumentException if {@code patternExpression} is not a valid pattern expression, see:
     *                                  {@link abstractPattern.analysis.PatternType#isPatternExpression(ASTNode)}
     * @throws IllegalArgumentException if {@code patternExpression} resolve as
     *                                  {@link abstractPattern.analysis.PatternTypeAnalysis#Invalid} from
     *                                  {@link abstractPattern.analysis.PatternTypeAnalysis#analyze(Expr)}
     */
    public static Pattern buildAbstractPattern(Expr patternExpression, String enclosingFilename) {
        if (patternExpression == null) throw new NullPointerException();
        if (!PatternType.isPatternExpression(patternExpression)) throw new IllegalArgumentException();
        PatternTypeAnalysis analysisResult = PatternTypeAnalysis.analyze(patternExpression);
        if (analysisResult == PatternTypeAnalysis.Modifier)
            return Modifier.buildAbstractModifier(patternExpression, enclosingFilename);
        if (analysisResult == PatternTypeAnalysis.Primitive)
            return Primitive.buildAbstractPrimitive(patternExpression, enclosingFilename);
        assert analysisResult == PatternTypeAnalysis.Invalid;
        throw new IllegalArgumentException();
    }
}

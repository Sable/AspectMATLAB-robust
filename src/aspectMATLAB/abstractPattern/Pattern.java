package aspectMATLAB.abstractPattern;

import Matlab.Utils.IReport;
import aspectMATLAB.abstractPattern.analysis.PatternType;
import aspectMATLAB.abstractPattern.analysis.PatternTypeAnalysis;
import aspectMATLAB.abstractPattern.modifier.Modifier;
import aspectMATLAB.abstractPattern.primitive.Primitive;
import ast.ASTNode;
import ast.Expr;
import aspectMATLAB.joinpoint.AMSourceCodePos;

import java.util.Optional;

/** a abstract representation on the patternExpand */
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

    /** @return the start column number for the patternExpand */
    public int getStartColumnNumber() {
        return this.startColumnNumber;
    }

    /** @return the start line number for the patternExpand */
    public int getStartLineNumber() {
        return this.startLineNumber;
    }

    /** @return the enclosing filename for the patternExpand */
    public String getEnclosingFilename() {
        return this.enclosingFilename;
    }

    /** @return report of checking the structure of the patternExpand */
    public abstract IReport getStructureValidationReport();

    /** @return the AST representation of the patternExpand */
    @Deprecated
    public ASTNode getPatternAST() {
        return this.originalPattern;
    }

    /** @return an abstraction on the postion of such patternExpand in source code. */
    @SuppressWarnings("deprecation")
    public AMSourceCodePos getSourceCodePosition() {
        return new AMSourceCodePos(startLineNumber, startColumnNumber, enclosingFilename);
    }

    /**
     * construct abstract patternExpand from patternExpand expression
     * @param patternExpression patternExpand expression
     * @param enclosingFilename enclosing aspect file path
     * @return constructed abstract patternExpand
     * @throws NullPointerException if {@code patternExpression} is {@code null}
     * @throws IllegalArgumentException if {@code patternExpression} is not a valid patternExpand expression, see:
     *                                  {@link PatternType#isPatternExpression(ASTNode)}
     * @throws IllegalArgumentException if {@code patternExpression} resolve as
     *                                  {@link PatternTypeAnalysis#Invalid} from
     *                                  {@link PatternTypeAnalysis#analyze(Expr)}
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

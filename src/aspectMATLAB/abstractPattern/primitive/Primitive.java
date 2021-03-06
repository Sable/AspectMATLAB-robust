package aspectMATLAB.abstractPattern.primitive;

import aspectMATLAB.abstractPattern.Pattern;
import aspectMATLAB.abstractPattern.analysis.PatternType;
import aspectMATLAB.abstractPattern.analysis.PatternTypeAnalysis;
import aspectMATLAB.abstractPattern.modifier.Modifier;
import aspectMATLAB.abstractPattern.modifier.ModifierAnd;
import ast.*;
import aspectMATLAB.transformer.TransformQueryEnv;

import java.util.*;

/** an abstract representation on the primitive patternExpand */
public abstract class Primitive extends Pattern {
    protected final java.util.List<Modifier> modifierList = new LinkedList<>();

    /**
     * @param primitiveExpr primitive patternExpand expression from parser
     * @param enclosingFilename enclosing aspect file name
     * @throws IllegalArgumentException if {@code primitiveExpr} is not a primitive patternExpand */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Primitive(Expr primitiveExpr, String enclosingFilename) {
        super(primitiveExpr, enclosingFilename);
    }

    /**
     * applying modifier patternExpand to this primitive patternExpand
     * @param modifier {@link Modifier} modifier patterns
     * @return reference to this primitive patternExpand
     * @throws NullPointerException if {@code modifier} is {@code null}
     */
    public Primitive addModifier(Modifier modifier) {
        if (modifier == null) throw new NullPointerException();
        if (modifier instanceof ModifierAnd) {
            addModifier(((ModifierAnd) modifier).getLHSModifier());
            addModifier(((ModifierAnd) modifier).getRHSModifier());
        } else {
            modifierList.add(modifier);
        }
        return this;
    }

    /**
     * wrap the pretty printed modifier list
     * @param primitivePatternString pretty printed primitive patternExpand
     * @return wrapped pretty printed result
     * @throws NullPointerException if {@code primitivePatternString} is {@code null}
     */
    protected String getModifierToString(String primitivePatternString) {
        if (primitivePatternString == null) throw new NullPointerException();
        if (modifierList.isEmpty()) return primitivePatternString;

        StringBuilder modifierBuffer = new StringBuilder();
        for (int modifierIndex = 0; modifierIndex < modifierList.size(); modifierIndex++) {
            modifierBuffer.append(modifierList.get(modifierIndex).toString());
            if (modifierIndex + 1 < modifierList.size()) modifierBuffer.append(" & ");
        }
        return String.format("(%s & %s)", primitivePatternString, modifierBuffer.toString());
    }

    /**
     * build abstract primitive patternExpand from patternExpand expression
     * @param patternExpression patternExpand expression
     * @param enclosingFilename enclosing aspect file path
     * @return constructed abstract primitive patternExpand
     * @throws NullPointerException if {@code patternExpression} is {@code null}
     * @throws IllegalArgumentException if {@code patternExpression} is not a patternExpand expression
     * @throws IllegalArgumentException if {@code patternExpression} do not resolve as {@code Primitive} from
     *                                  {@link PatternTypeAnalysis#analyze(Expr)}
     */
    @SuppressWarnings("deprecation")
    public static Primitive buildAbstractPrimitive(Expr patternExpression, String enclosingFilename) {
        if (patternExpression == null) throw new NullPointerException();
        if (!PatternType.isPatternExpression(patternExpression)) throw new IllegalArgumentException();
        if (PatternTypeAnalysis.analyze(patternExpression) != PatternTypeAnalysis.Primitive) {
            throw new IllegalArgumentException();
        }

        if (patternExpression instanceof PatternAnnotate)
            return new Annotation((PatternAnnotate) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternCall)
            return new Call((PatternCall) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternExecution)
            return new Execution((PatternExecution) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternGet)
            return new Get((PatternGet) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternLoop)
            return new Loop((PatternLoop) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternLoopBody)
            return new LoopBody((PatternLoopBody) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternLoopHead)
            return new LoopHead((PatternLoopHead) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternMainExecution)
            return new MainExecution((PatternMainExecution) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternOperator)
            return new Operator((PatternOperator) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternSet)
            return new Set((PatternSet) patternExpression, enclosingFilename);

        if (patternExpression instanceof AndExpr) {
            PatternTypeAnalysis lhsAnalysisResult = PatternTypeAnalysis.analyze(((AndExpr) patternExpression).getLHS());
            PatternTypeAnalysis rhsAnalysisResult = PatternTypeAnalysis.analyze(((AndExpr) patternExpression).getRHS());
            if (lhsAnalysisResult == PatternTypeAnalysis.Primitive) {
                if (rhsAnalysisResult == PatternTypeAnalysis.Primitive) {
                    Primitive lhs = buildAbstractPrimitive(((AndExpr) patternExpression).getLHS(), enclosingFilename);
                    Primitive rhs = buildAbstractPrimitive(((AndExpr) patternExpression).getRHS(), enclosingFilename);
                    return new PrimitiveAnd(lhs, rhs, enclosingFilename);
                } else {
                    assert rhsAnalysisResult == PatternTypeAnalysis.Modifier;
                    Primitive lhs = buildAbstractPrimitive(((AndExpr) patternExpression).getLHS(), enclosingFilename);
                    Modifier rhs = Modifier.buildAbstractModifier(
                            ((AndExpr) patternExpression).getRHS(),
                            enclosingFilename
                    ).simplifyModifier();
                    return lhs.addModifier(rhs);
                }
            } else {
                assert lhsAnalysisResult == PatternTypeAnalysis.Modifier;
                if (rhsAnalysisResult == PatternTypeAnalysis.Primitive) {
                    Modifier lhs = Modifier.buildAbstractModifier(
                            ((AndExpr) patternExpression).getLHS(),
                            enclosingFilename
                    ).simplifyModifier();
                    Primitive rhs = buildAbstractPrimitive(((AndExpr) patternExpression).getRHS(), enclosingFilename);
                    return rhs.addModifier(lhs);
                } else {
                    assert rhsAnalysisResult == PatternTypeAnalysis.Modifier;
                    /* control flow should not reach here */
                    throw new AssertionError();
                }
            }
        }
        if (patternExpression instanceof OrExpr) {
            assert PatternTypeAnalysis.analyze(((OrExpr) patternExpression).getLHS()) == PatternTypeAnalysis.Primitive;
            assert PatternTypeAnalysis.analyze(((OrExpr) patternExpression).getRHS()) == PatternTypeAnalysis.Primitive;
            Primitive lhsPrimitive = buildAbstractPrimitive(((OrExpr) patternExpression).getLHS(), enclosingFilename);
            Primitive rhsPrimitive = buildAbstractPrimitive(((OrExpr) patternExpression).getRHS(), enclosingFilename);
            return new PrimitiveOr(lhsPrimitive, rhsPrimitive, enclosingFilename);
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    /**
     * determine if a given AST node is possible a join point for current pattern.
     * @param astNode           The given AST node
     * @param transformQueryEnv Transforming environment
     * @return {@code true} if AST node is possible a join point, otherwise {@code false}.
     */
    public boolean isPossibleJoinPoint(ASTNode astNode, TransformQueryEnv transformQueryEnv) {
        return false;
    }
}

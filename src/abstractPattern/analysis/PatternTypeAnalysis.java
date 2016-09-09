package abstractPattern.analysis;

import ast.*;
import utils.LiteralBuilder;

import java.util.Set;

/** pattern type analysis */
public enum PatternTypeAnalysis {
    /**
     * primitive pattern (patterns will provide at least on join point during code transformation and weaving)
     */
    Primitive,
    /**
     * modifier pattern (pattern do not provide join point during code transformation and weaving, instead will pose
     * restriction on the primitive pattern they bound to)
     */
    Modifier,
    /**
     * invalid patterns (will reject)
     */
    Invalid;

    /** a set containing all primitive pattern AST node class */
    public static final Set<PatternType> primitivePatternSet = new LiteralBuilder<PatternType>()
            .put(PatternType.Annotation)
            .put(PatternType.Call)
            .put(PatternType.Execution)
            .put(PatternType.Get)
            .put(PatternType.Loop)
            .put(PatternType.LoopBody)
            .put(PatternType.LoopHead)
            .put(PatternType.MainExecution)
            .put(PatternType.Operator)
            .put(PatternType.Set)
            .asSet();

    /** a set containing all modifier pattern AST node class */
    public static final Set<PatternType> modifierPatternSet = new LiteralBuilder<PatternType>()
            .put(PatternType.Shape)
            .put(PatternType.Type)
            .put(PatternType.Scope)
            .asSet();

    /**
     * And case handler in pattern type analysis, use following merge strategy: <br>
     * <pre><code>
     * AND       | Primitive | Modifier  | Invalid
     * ----------+-----------+-----------+--------
     * Primitive | Primitive | Primitive | Invalid
     * Modifier  | Primitive | Modifier  | Invalid
     * Invalid   | Invalid   | Invalid   | Invalid
     * </code></pre>
     * @param lhs and left hand side pattern type analysis result
     * @param rhs and right hand side pattern type analysis result
     * @return and expression pattern type analysis result */
    public static PatternTypeAnalysis andMerge(PatternTypeAnalysis lhs, PatternTypeAnalysis rhs) {
        switch (lhs) {
            case Primitive: {
                switch (rhs) {
                    case Primitive: return Primitive;
                    case Modifier:  return Primitive;
                    case Invalid:   return Invalid;
                }
            }
            case Modifier: {
                switch (rhs) {
                    case Primitive: return Primitive;
                    case Modifier:  return Modifier;
                    case Invalid:   return Invalid;
                }
            }
            case Invalid: return Invalid;
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    /**
     * Or case handler in pattern type analysis, use following merge strategy: <br>
     * <pre><code>
     * Or        | Primitive | Modifier | Invalid
     * ----------+-----------+----------+--------
     * Primitive | Primitive | Invalid  | Invalid
     * Modifier  | Invalid   | Modifier | Invalid
     * Invalid   | Invalid   | Invalid  | Invalid
     * </code></pre>
     * @param lhs or left hand side pattern type analysis result
     * @param rhs or right hand side pattern type analysis result
     * @return or expression type analysis result */
    public static PatternTypeAnalysis orMerge(PatternTypeAnalysis lhs, PatternTypeAnalysis rhs) {
        switch (lhs) {
            case Primitive: {
                switch (rhs) {
                    case Primitive: return Primitive;
                    case Modifier:  return Invalid;
                    case Invalid:   return Invalid;
                }
            }
            case Modifier: {
                switch (rhs) {
                    case Primitive: return Invalid;
                    case Modifier:  return Modifier;
                    case Invalid:   return Invalid;
                }
            }
            case Invalid: return Invalid;
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    /**
     * Not case handler in pattern type analysis, use following merge strategy: <br>
     * <pre><code>
     * Not | Primitive | Modifier | Invalid
     * ----+-----------+----------+--------
     *     | Invalid   | Modifier | Invalid
     * </code></pre>
     * @param operand not operand pattern type analysis result
     * @return not expression type analysis result*/
    public static PatternTypeAnalysis notMerge(PatternTypeAnalysis operand) {
        switch (operand) {
            case Primitive: return Invalid;
            case Modifier:  return Modifier;
            case Invalid:   return Invalid;
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    /**
     * perform pattern type analysis to a given pattern, it will determine if the pattern is a primitive pattern,
     * modifier pattern or invalid pattern.
     * @param pattern pattern to be determined
     * @return the pattern type of the pattern
     * @throws IllegalArgumentException if the input argument is not a pattern expression
     */
    public static PatternTypeAnalysis analyze(Expr pattern) {
        if (!PatternType.isPatternExpression(pattern)) throw new IllegalArgumentException();
        if (PatternType.isBasicPatternExpression(pattern)) {
            PatternType patternType = PatternType.fromASTNodes(pattern.getClass());
            if (primitivePatternSet.contains(patternType))  return Primitive;
            if (modifierPatternSet.contains(patternType))   return Modifier;
            /* control flow should not reach here */
            throw new AssertionError();
        } else {
            if (pattern instanceof AndExpr) {
                PatternTypeAnalysis lhs = analyze(((AndExpr) pattern).getLHS());
                PatternTypeAnalysis rhs = analyze(((AndExpr) pattern).getRHS());
                return andMerge(lhs, rhs);
            }
            if (pattern instanceof OrExpr) {
                PatternTypeAnalysis lhs = analyze(((OrExpr) pattern).getLHS());
                PatternTypeAnalysis rhs = analyze(((OrExpr) pattern).getRHS());
                return orMerge(lhs, rhs);
            }
            if (pattern instanceof NotExpr) {
                PatternTypeAnalysis operand = analyze(((NotExpr) pattern).getOperand());
                return notMerge(operand);
            }
            /* control flow should not reach here */
            throw new AssertionError();
        }
    }
}

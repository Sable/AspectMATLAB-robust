package abstractPattern.analysis;

import ast.*;
import utils.LiteralBuilder;

import java.util.Collections;
import java.util.Set;

/** patternExpand type analysis */
public enum PatternTypeAnalysis {
    /**
     * primitive patternExpand (patterns will provide at least on join point during code transformation and weaving)
     */
    Primitive,
    /**
     * modifier patternExpand (patternExpand do not provide join point during code transformation and weaving, instead will pose
     * restriction on the primitive patternExpand they bound to)
     */
    Modifier,
    /**
     * invalid patterns (will reject)
     */
    Invalid;

    /** a set containing all primitive patternExpand AST node class */
    public static final Set<PatternType> primitivePatternSet = Collections.unmodifiableSet(
            new LiteralBuilder<PatternType>()
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
                    .asSet()
    );


    /** a set containing all modifier patternExpand AST node class */
    public static final Set<PatternType> modifierPatternSet = Collections.unmodifiableSet(
            new LiteralBuilder<PatternType>()
                    .put(PatternType.Shape)
                    .put(PatternType.Type)
                    .put(PatternType.Scope)
                    .asSet()
    );

    /**
     * And case handler in patternExpand type analysis, use following merge strategy: <br>
     * <pre><code>
     * AND       | Primitive | Modifier  | Invalid
     * ----------+-----------+-----------+--------
     * Primitive | Primitive | Primitive | Invalid
     * Modifier  | Primitive | Modifier  | Invalid
     * Invalid   | Invalid   | Invalid   | Invalid
     * </code></pre>
     * @param lhs and left hand side patternExpand type analysis result
     * @param rhs and right hand side patternExpand type analysis result
     * @return and expression patternExpand type analysis result */
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
     * PatternOrTrans case handler in patternExpand type analysis, use following merge strategy: <br>
     * <pre><code>
     * PatternOrTrans        | Primitive | Modifier | Invalid
     * ----------+-----------+----------+--------
     * Primitive | Primitive | Invalid  | Invalid
     * Modifier  | Invalid   | Modifier | Invalid
     * Invalid   | Invalid   | Invalid  | Invalid
     * </code></pre>
     * @param lhs or left hand side patternExpand type analysis result
     * @param rhs or right hand side patternExpand type analysis result
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
     * Not case handler in patternExpand type analysis, use following merge strategy: <br>
     * <pre><code>
     * Not | Primitive | Modifier | Invalid
     * ----+-----------+----------+--------
     *     | Invalid   | Modifier | Invalid
     * </code></pre>
     * @param operand not operand patternExpand type analysis result
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
     * perform patternExpand type analysis to a given patternExpand, it will determine if the patternExpand is a primitive patternExpand,
     * modifier patternExpand or invalid patternExpand.
     * @param pattern patternExpand to be determined
     * @return the patternExpand type of the patternExpand
     * @throws IllegalArgumentException if the input argument is not a patternExpand expression
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

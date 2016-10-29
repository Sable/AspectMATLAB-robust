package abstractPattern.modifier;

import abstractPattern.Pattern;
import abstractPattern.analysis.PatternType;
import abstractPattern.analysis.PatternTypeAnalysis;
import ast.*;
import utils.MergableHashSet;

import java.util.function.Function;

/** an abstract representation on the modifier patternExpand */
public abstract class Modifier extends Pattern {

    /**
     * @param modifierPattern modifier patternExpand expression from parser
     * @param enclosingFilename enclosing aspect file name
     * @throws IllegalArgumentException if {@code primitiveExpr} is not a modifier patternExpand */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Modifier(Expr modifierPattern, String enclosingFilename) {
        super(modifierPattern, enclosingFilename);
    }

    /**
     * collect all the modifiers type on the modifier patternExpand, this method will be helpful during the modifier
     * validation
     * @return a set contains all the class type of the modifiers
     */
    public abstract MergableHashSet<Class<? extends Modifier>> getModifierTypeSet();

    /**
     * build abstract modifier patternExpand from patten expression
     * @param patternExpression patternExpand expression
     * @param enclosingFilename enclosing aspect file path
     * @return the constructed abstract modifier patternExpand
     * @throws NullPointerException if {@code patternExpression} is {@code null}
     * @throws IllegalArgumentException if {@code patternExpression} is not a patternExpand expression
     * @throws IllegalArgumentException if {@code patternExpression} do not resolve as {@code Modifier} from
     *                                  {@link abstractPattern.analysis.PatternTypeAnalysis#analyze(Expr)}
     */
    @SuppressWarnings("deprecation")
    public static Modifier buildAbstractModifier(Expr patternExpression, String enclosingFilename) {
        if (patternExpression == null) throw new NullPointerException();
        if (!PatternType.isPatternExpression(patternExpression)) throw new IllegalArgumentException();
        if (PatternTypeAnalysis.analyze(patternExpression) != PatternTypeAnalysis.Modifier) {
            throw new IllegalArgumentException();
        }

        if (patternExpression instanceof PatternDimension)
            return new Shape((PatternDimension) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternIsType)
            return new Type((PatternIsType) patternExpression, enclosingFilename);
        if (patternExpression instanceof PatternWithin)
            return new Scope((PatternWithin) patternExpression, enclosingFilename);
        if (patternExpression instanceof AndExpr) {
            Modifier lhsModifier = buildAbstractModifier(((AndExpr) patternExpression).getLHS(), enclosingFilename);
            Modifier rhsModifier = buildAbstractModifier(((AndExpr) patternExpression).getRHS(), enclosingFilename);
            return new ModifierAnd(lhsModifier, rhsModifier, enclosingFilename);
        }
        if (patternExpression instanceof OrExpr) {
            Modifier lhsModifier = buildAbstractModifier(((OrExpr) patternExpression).getLHS(), enclosingFilename);
            Modifier rhsModifier = buildAbstractModifier(((OrExpr) patternExpression).getRHS(), enclosingFilename);
            return new ModifierOr(lhsModifier, rhsModifier, enclosingFilename);
        }
        if (patternExpression instanceof NotExpr) {
            Modifier operandModifier = buildAbstractModifier(((NotExpr) patternExpression).getOperand(),
                    enclosingFilename);
            return new ModifierNot(operandModifier, enclosingFilename);
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    /**
     * it will return an simplified reference of the current modifier, using the following rule:
     * <ul>
     *     <li>\(\neg (A \land B) \Rightarrow \neg A \lor \neg B\)</li>
     *     <li>\(\neg (A \lor B)  \Rightarrow \neg A \land \neg B \)</li>
     *     <li>\(\neg \neg A      \Rightarrow A \)</li>
     * </ul>
     * Note that such method does not create a copy of the nodes.
     * @return simplified reference of the current modifier
     */
    @SuppressWarnings("deprecation")
    public Modifier simplifyModifier() {
        // case 1 : ~(A & B) <=> ~A | ~B
        // case 2 : ~(A | B) <=> ~A & ~B
        // case 3 : ~~A      <=> A
        Function<Modifier, Boolean> case1Recognizer = modifier -> {
            if (!(modifier instanceof ModifierNot)) return false;
            return ((ModifierNot) modifier).getOperandModifier() instanceof ModifierAnd;
        };
        Function<Modifier, Boolean> case2Recognizer = modifier -> {
            if (!(modifier instanceof ModifierNot)) return false;
            return ((ModifierNot) modifier).getOperandModifier() instanceof ModifierOr;
        };
        Function<Modifier, Boolean> case3Recognizer = modifier -> {
            if (!(modifier instanceof ModifierNot)) return false;
            return ((ModifierNot) modifier).getOperandModifier() instanceof ModifierNot;
        };

        if (case1Recognizer.apply(this)) {
            assert this instanceof ModifierNot;
            assert ((ModifierNot) this).getOperandModifier() instanceof ModifierAnd;
            Modifier modifierOrLHS =
                    new ModifierNot(
                            ((ModifierAnd) ((ModifierNot) this).getOperandModifier()).getLHSModifier(),
                            enclosingFilename
                    ).simplifyModifier();
            Modifier modifierOrRHS =
                    new ModifierNot(
                            ((ModifierAnd) ((ModifierNot) this).getOperandModifier()).getRHSModifier(),
                            enclosingFilename
                    ).simplifyModifier();
            return new ModifierOr(modifierOrLHS, modifierOrRHS, enclosingFilename);
        }
        if (case2Recognizer.apply(this)) {
            assert this instanceof ModifierNot;
            assert ((ModifierNot) this).getOperandModifier() instanceof ModifierOr;
            Modifier modifierAndLHS =
                    new ModifierNot(
                            ((ModifierOr) ((ModifierNot) this).getOperandModifier()).getLHSModifier(),
                            enclosingFilename
                    ).simplifyModifier();
            Modifier modifierAndRHS =
                    new ModifierNot(
                            ((ModifierOr) ((ModifierNot) this).getOperandModifier()).getRHSModifier(),
                            enclosingFilename
                    ).simplifyModifier();
            return new ModifierAnd(modifierAndLHS, modifierAndRHS, enclosingFilename);
        }
        if (case3Recognizer.apply(this)) {
            assert this instanceof ModifierNot;
            assert ((ModifierNot) this).getOperandModifier() instanceof ModifierNot;
            Modifier modifierOperand = ((ModifierNot) ((ModifierNot) this).getOperandModifier())
                    .getOperandModifier()
                    .simplifyModifier();
            return modifierOperand;
        }

        if (this instanceof ModifierAnd) {
            return new ModifierAnd(
                    ((ModifierAnd) this).getLHSModifier().simplifyModifier(),
                    ((ModifierAnd) this).getRHSModifier().simplifyModifier(),
                    enclosingFilename
            );
        }
        if (this instanceof ModifierOr) {
            return new ModifierOr(
                    ((ModifierOr) this).getLHSModifier().simplifyModifier(),
                    ((ModifierOr) this).getRHSModifier().simplifyModifier(),
                    enclosingFilename
            );
        }
        if (this instanceof ModifierNot) {
            return new ModifierNot(
                    ((ModifierNot) this).getOperandModifier().simplifyModifier(),
                    enclosingFilename
            );
        }
        return this;
    }
}

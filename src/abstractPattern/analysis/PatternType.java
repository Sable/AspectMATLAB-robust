package abstractPattern.analysis;

import ast.*;

/** a abstract representation on the pattern type */
public enum PatternType {
    /** annotation pattern */           Annotation,
    /** subroutine call pattern */      Call,
    /** shape pattern */                Shape,
    /** subroutine execution pattern */ Execution,
    /** variable get pattern */         Get,
    /** type pattern */                 Type,
    /** loop pattern */                 Loop,
    /** loop body pattern */            LoopBody,
    /** loop head pattern */            LoopHead,
    /** main execution pattern */       MainExecution,
    /** operator pattern */             Operator,
    /** variable set pattern */         Set,
    /** scope pattern */                Scope;

    /**
     * get pattern type from a given AST node class, throws {@code IllegalArgumentException} if such AST node is not
     * a valid aspectMATLAB pattern expression.
     * <ul>
     *     <li>{@code PatternAnnotate} -&gt; {@code PatternType.Annotation}</li>
     *     <li>{@code PatternCall} -&gt; {@code PatternType.Call}</li>
     *     <li>{@code PatternDimension} -&gt; {@code PatternType.ShapeSignature}</li>
     *     <li>{@code PatternExecution.class} -&gt; {@code PatternType.Execution}</li>
     *     <li>{@code PatternGet.class} -&gt; {@code PatternType.Get}</li>
     *     <li>{@code PatternIsType.class} -&gt; {@code PatternType.Type}</li>
     *     <li>{@code PatternLoop.class} -&gt; {@code PatternType.Loop}</li>
     *     <li>{@code PatternLoopBody.class} -&gt; {@code PatternType.LoopBody}</li>
     *     <li>{@code PatternLoopHead.class} -&gt; {@code PatternType.LoopHead}</li>
     *     <li>{@code PatternMainExecution.class} -&gt; {@code PatternType.MainExecution}</li>
     *     <li>{@code PatternOperator.class} -&gt; {@code PatternType.Operator}</li>
     *     <li>{@code PatternSet.class} -&gt; {@code PatternType.Set}</li>
     *     <li>{@code PatternWithin.class} -&gt; {@code PatternType.Scope}</li>
     * </ul>
     * @param nodeClass AST node class
     * @return corresponding PatternType enumeration
     * @throws IllegalArgumentException input class is not a valid aspectMATLAB pattern expression
     */
    public static PatternType fromASTNodes(Class<? extends ASTNode> nodeClass) {
        if (nodeClass.equals(PatternAnnotate.class)) return PatternType.Annotation;
        if (nodeClass.equals(PatternCall.class)) return PatternType.Call;
        if (nodeClass.equals(PatternDimension.class)) return PatternType.Shape;
        if (nodeClass.equals(PatternExecution.class)) return PatternType.Execution;
        if (nodeClass.equals(PatternGet.class)) return PatternType.Get;
        if (nodeClass.equals(PatternIsType.class)) return PatternType.Type;
        if (nodeClass.equals(PatternLoop.class)) return PatternType.Loop;
        if (nodeClass.equals(PatternLoopBody.class)) return PatternType.LoopBody;
        if (nodeClass.equals(PatternLoopHead.class)) return PatternType.LoopHead;
        if (nodeClass.equals(PatternMainExecution.class)) return PatternType.MainExecution;
        if (nodeClass.equals(PatternOperator.class)) return PatternType.Operator;
        if (nodeClass.equals(PatternSet.class)) return PatternType.Set;
        if (nodeClass.equals(PatternWithin.class)) return PatternType.Scope;
        throw new IllegalArgumentException();
    }

    /**
     * determine if a AST node is a pattern expression
     * @param astNode AST node to be determined
     * @return {@code true} if such node is indeed a pattern expression, otherwise return {@code false}
     */
    public static boolean isPatternExpression(ASTNode astNode) {
        if (astNode instanceof AndExpr) {
            boolean lhsResult = isPatternExpression(((AndExpr) astNode).getLHS());
            boolean rhsResult = isPatternExpression(((AndExpr) astNode).getRHS());
            return lhsResult && rhsResult;
        }
        if (astNode instanceof OrExpr) {
            boolean lhsResult = isPatternExpression(((OrExpr) astNode).getLHS());
            boolean rhsResult = isPatternExpression(((OrExpr) astNode).getRHS());
            return lhsResult && rhsResult;
        }
        if (astNode instanceof NotExpr) {
            boolean operandResult = isPatternExpression(((NotExpr) astNode).getOperand());
            return operandResult;
        }
        if (isBasicPatternExpression(astNode)) return true;
        return false;
    }

    /**
     * determine if a given AST node is a basic pattern expression
     * @param astNode AST node to be determined
     * @return {@code true} if such node is indeed a basic pattern expression, otherwise reutrn {@code false}
     */
    public static boolean isBasicPatternExpression(ASTNode astNode) {
        if (astNode instanceof PatternAnnotate)         return true;
        if (astNode instanceof PatternCall)             return true;
        if (astNode instanceof PatternDimension)        return true;
        if (astNode instanceof PatternExecution)        return true;
        if (astNode instanceof PatternGet)              return true;
        if (astNode instanceof PatternIsType)           return true;
        if (astNode instanceof PatternLoop)             return true;
        if (astNode instanceof PatternLoopBody)         return true;
        if (astNode instanceof PatternLoopHead)         return true;
        if (astNode instanceof PatternMainExecution)    return true;
        if (astNode instanceof PatternOperator)         return true;
        if (astNode instanceof PatternSet)              return true;
        if (astNode instanceof PatternWithin)           return true;
        return false;
    }
}

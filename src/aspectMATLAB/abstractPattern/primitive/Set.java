package aspectMATLAB.abstractPattern.primitive;

import Matlab.Utils.IReport;
import aspectMATLAB.abstractPattern.signature.FullSignature;
import ast.*;
import natlab.toolkits.analysis.varorfun.VFDatum;
import aspectMATLAB.transformer.TransformQueryEnv;

import java.util.Optional;

/** an abstract representation on the get patternExpand */
public final class Set extends Primitive {
    private final FullSignature fullSignature;
    private final String identifier;

    /**
     * construct from {@link PatternSet} AST node. If the patternExpand do not provide a full signature, will create a full
     * signature with empty shape patternExpand and empty type patternExpand.
     * @param patternSet {@link PatternSet} AST node
     * @param enclosingFilename enclosing aspect file path
     * @throws IllegalArgumentException if set patternExpand do not have a identifier name signature
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public Set(PatternSet patternSet, String enclosingFilename) {
        super(patternSet, enclosingFilename);

        assert originalPattern instanceof PatternSet;
        identifier = Optional
                .ofNullable(((PatternSet) originalPattern).getIdentifier())
                .orElseThrow(IllegalArgumentException::new)
                .getID();
        fullSignature = new FullSignature(
                Optional.ofNullable(((PatternSet) originalPattern).getFullSignature())
                        .orElseGet(ast.FullSignature::new),
                enclosingFilename
        );
    }

    /**
     * perform a structural weeding on the set patternExpand, it will:
     * <ul>
     *     <li>raises error if identifier using [..] wildcard,</li>
     *     <li>raises error if type using [..] wildcard,</li>
     *     <li>merge the validation report from full signature</li>
     * </ul>
     * @return structural weeding report on set patternExpand
     */
    @Override
    public IReport getStructureValidationReport() {
        IReport report = fullSignature.getStructureValidationReport();
        if ("".equals(identifier)) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in set patternExpand for identifier name, use [*] instead"
            );
        }
        if ("".equals(fullSignature.getTypeSignature().getSignature())) {
            report.AddError(
                    enclosingFilename,
                    startLineNumber, startColumnNumber,
                    "wildcard [..] is not a valid matcher in set patternExpand for type name, use [*] instead"
            );
        }
        return report;
    }

    private boolean isPossibleJoinPointNameExpr(NameExpr nameExpr, TransformQueryEnv transformQueryEnv) {
        Name name = Optional.ofNullable(nameExpr).orElseThrow(NullPointerException::new).getName();

        if (name == null) throw new NullPointerException();
        VFDatum kindAnalysisResult = transformQueryEnv.kindAnalysis.getResult(name);
        if (kindAnalysisResult.isFunction()) return false;
        if (kindAnalysisResult.isVariable()) {
            if (identifier.equals("*")) {
                return true;
            } else {
                return identifier.equals(Optional.ofNullable(name.getID()).orElseThrow(NullPointerException::new));
            }
        }
        if (kindAnalysisResult.isID()) {
            if (identifier.equals("*")) {
                return true;
            } else {
                return identifier.equals(Optional.ofNullable(name.getID()).orElseThrow(NullPointerException::new));
            }
        }
        /* control flow should not reach here */
        throw new AssertionError();
    }

    private boolean isPossibleJoinPointParameterExpr(ParameterizedExpr parameterizedExpr,
                                                     TransformQueryEnv transformQueryEnv) {
        Expr targetExpr = Optional.ofNullable(parameterizedExpr).orElseThrow(NullPointerException::new).getTarget();
        if (targetExpr instanceof NameExpr) {
            return isPossibleJoinPointNameExpr((NameExpr) targetExpr, transformQueryEnv);
        } else {
            return false;
        }
    }

    private boolean isPossibleJoinPointCellIndexExpr(CellIndexExpr cellIndexExpr, TransformQueryEnv transformQueryEnv) {
        Expr targetExpr = Optional.ofNullable(cellIndexExpr).orElseThrow(NullPointerException::new).getTarget();
        if (targetExpr instanceof NameExpr) {
            return isPossibleJoinPointNameExpr((NameExpr) targetExpr, transformQueryEnv);
        } else {
            return false;
        }
    }

    private boolean isPossibleJoinPointDotExpr(DotExpr dotExpr, TransformQueryEnv transformQueryEnv) {
        Expr targetExpr = Optional.ofNullable(dotExpr).orElseThrow(NullPointerException::new).getTarget();
        if (targetExpr instanceof NameExpr) {
            return isPossibleJoinPointNameExpr((NameExpr) targetExpr, transformQueryEnv);
        } else {
            return false;
        }
    }

    @Override
    public boolean isPossibleJoinPoint(ASTNode astNode, TransformQueryEnv transformQueryEnv) {
        if (astNode instanceof NameExpr) {
            return isPossibleJoinPointNameExpr((NameExpr) astNode, transformQueryEnv);
        } else if (astNode instanceof ParameterizedExpr) {
            return isPossibleJoinPointParameterExpr((ParameterizedExpr) astNode, transformQueryEnv);
        } else if (astNode instanceof CellIndexExpr) {
            return isPossibleJoinPointCellIndexExpr((CellIndexExpr) astNode, transformQueryEnv);
        } else if (astNode instanceof DotExpr) {
            return isPossibleJoinPointDotExpr((DotExpr) astNode, transformQueryEnv);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return getModifierToString(String.format("set(%s:%s)", identifier, fullSignature.toString()));
    }
}

package utils;

import ast.ASTNode;
import ast.Name;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;

import java.util.HashMap;
import java.util.Optional;

/** an extension of the kind analysis providing overriding features
 * {@link natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis}
 */
public class OverrideVFAnalysis extends VFFlowInsensitiveAnalysis {
    private HashMap<Name, VFDatum> overridingMap = new HashMap<>();

    /**
     * initiate a flow insensitive kind analysis to AST. (node invoke {@code analyze} before retrieve result
     * @param node AST to perform kind analysis
     * @param query function or script query handler
     * @throws NullPointerException if {@code node} is {@code null}
     * @throws NullPointerException if {@code query} is {@code null}
     */
    public OverrideVFAnalysis(ASTNode node, FunctionOrScriptQuery query) {
        super(
                Optional.ofNullable(node).orElseThrow(NullPointerException::new),
                Optional.ofNullable(query).orElseThrow(NullPointerException::new)
        );
    }

    /** initiate a flow insensitive kind analysis to AST, using default function or script query handler
     * @param node AST to perform kind analysis */
    @Deprecated
    @SuppressWarnings("deprecation")
    public OverrideVFAnalysis(ASTNode node) {
        super(node);
    }

    /**
     * override result in kind analysis
     * @param name AST node of the name to override result
     * @param vfDatum new kind analysis result
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws NullPointerException if {@code vfDatum} is {@code null}
     */
    public void override(Name name, VFDatum vfDatum) {
        overridingMap.put(
                Optional.ofNullable(name).orElseThrow(NullPointerException::new),
                Optional.ofNullable(vfDatum).orElseThrow(NullPointerException::new)
        );
    }

    /**
     * access result from kind analysis, if there existing a overriding result, the overriding result will be
     * returned, otherwise it will retrieve result from analysis
     * @param name AST node of the name to get analysis result
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws IllegalArgumentException if kind analysis do not contain result for {@code name}
     */
    @Override
    public VFDatum getResult(Name name) {
        if (name == null) throw new NullPointerException();
        try {
            if (overridingMap.containsKey(name)) {
                return overridingMap.get(name);
            } else {
                return super.getResult(name);
            }
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException();
        }
    }

    /** clear the result override map */
    public void clearOverride() {
        overridingMap.clear();
    }

    /**
     * remove result override for a specific name node
     * @param name AST node of the name to remove override result
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public void removeOverride(Name name) {
        overridingMap.remove(Optional.ofNullable(name).orElseThrow(NullPointerException::new));
    }
}

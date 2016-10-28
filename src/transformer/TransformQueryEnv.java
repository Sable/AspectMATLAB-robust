package transformer;

import abstractPattern.utils.ScopeType;
import natlab.toolkits.analysis.varorfun.VFAnalysis;
import org.javatuples.Pair;

import java.util.Stack;

public final class TransformQueryEnv implements Cloneable {
    public Stack<Pair<ScopeType, String>> staticScope = new Stack<>();
    public VFAnalysis kindAnalysis = null;

    @Override
    protected Object clone() {
        TransformQueryEnv retEnvironment = new TransformQueryEnv();
        retEnvironment.staticScope.addAll(staticScope);
        retEnvironment.kindAnalysis = kindAnalysis;

        return retEnvironment;
    }
}

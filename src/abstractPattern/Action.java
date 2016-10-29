package abstractPattern;

import abstractPattern.utils.ContentExposureType;
import abstractPattern.utils.WeaveType;
import ast.*;
import joinpoint.AMSourceCodePos;
import transformer.UnboundedIdentifier;
import transformer.patternExpand.PatternTrans;
import transformer.patternExpand.PatternTypeTrans;
import utils.CompilationInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** a abstract representation on the patternExpand */
public final class Action {
    private final Pattern pattern;
    private final int startLineNumber;
    private final int startColumnNumber;
    private final String enclosingFilename;

    private final Set<Function> nestedFunctionSet = new HashSet<>();
    private final List<Stmt> statementList = new List<>();
    private final Set<ContentExposureType> contentExposures = new HashSet<>();
    private final WeaveType weaveType;
    private final String name;

    public Action(ast.Action action, HashMap<String, Expr> predefinedPattern, CompilationInfo compilationInfo)
            throws UnboundedIdentifier{
        startLineNumber = Optional.ofNullable(action).orElseThrow(NullPointerException::new).getStartLine();
        startColumnNumber = Optional.ofNullable(action).orElseThrow(NullPointerException::new).getStartColumn();
        enclosingFilename = Optional
                .ofNullable(compilationInfo)
                .orElseThrow(NullPointerException::new)
                .getASTNodeEnclosingFile(action);
        Expr pattenExpression = Optional.ofNullable(action.getExpr()).orElseThrow(IllegalArgumentException::new);

        PatternTrans patternTrans = PatternTypeTrans.buildPatternTransformer(pattenExpression, predefinedPattern);
        pattenExpression = patternTrans.copyAndTransform();

        pattern = Pattern.buildAbstractPattern(pattenExpression, enclosingFilename);

        Optional.ofNullable(action.getNestedFunctionList()).orElseGet(List::new).forEach(nestedFunctionSet::add);
        Optional.ofNullable(action.getStmtList()).orElseGet(List::new).forEach(statementList::add);
        Optional.ofNullable(action.getInputParamList())
                .orElseGet(List::new)
                .forEach(selector -> contentExposures.add(ContentExposureType.valueOf(selector)));

        weaveType = WeaveType.fromString(action.getType());
        name = action.getName();
    }

    @SuppressWarnings("deprecation")
    public AMSourceCodePos getSourceCodePosition() {
        return new AMSourceCodePos(startLineNumber, startColumnNumber, enclosingFilename);
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append(name).append(" : ")
                .append(weaveType.toString()).append(' ')
                .append(pattern.toString()).append(" : ")
                .append(contentExposures.toString()).append('\n');
        for (Function function : nestedFunctionSet) {
            stringBuffer.append(function.getPrettyPrinted()).append('\n');
        }
        for (Stmt stmt : statementList) {
            stringBuffer.append(stmt.getPrettyPrinted()).append('\n');
        }
        stringBuffer.append("end");
        return stringBuffer.toString();
    }
}

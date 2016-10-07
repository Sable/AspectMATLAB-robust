package abstractPattern;

import abstractPattern.utils.ContentExposureType;
import abstractPattern.utils.WeaveType;
import ast.Expr;
import ast.Function;
import ast.List;
import ast.Stmt;
import joinpoint.AMSourceCodePos;
import utils.CompilationInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** a abstract representation on the pattern */
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

    public Action(ast.Action action, CompilationInfo compilationInfo) {
        startLineNumber = Optional.ofNullable(action).orElseThrow(NullPointerException::new).getStartLine();
        startColumnNumber = Optional.ofNullable(action).orElseThrow(NullPointerException::new).getStartColumn();
        enclosingFilename = Optional
                .ofNullable(compilationInfo)
                .orElseThrow(NullPointerException::new)
                .getASTNodeFile(action);
        Expr pattenExpression = Optional.ofNullable(action.getExpr()).orElseThrow(IllegalArgumentException::new);
        pattern = Pattern.buildAbstractPattern(pattenExpression, enclosingFilename);

        Optional.ofNullable(action.getNestedFunctionList()).orElseGet(List::new).forEach(nestedFunctionSet::add);
        Optional.ofNullable(action.getStmtList()).orElseGet(List::new).forEach(statementList::add);
        Optional.ofNullable(action.getInputParamList())
                .orElseGet(List::new)
                .forEach(selector -> contentExposures.add(ContentExposureType.valueOf(selector)));

        //TODO
        weaveType = null;
        name =null;
    }

    @SuppressWarnings("deprecation")
    public AMSourceCodePos getSourceCodePosition() {
        return new AMSourceCodePos(startLineNumber, startColumnNumber, enclosingFilename);
    }

    @Override
    public String toString() {
        return super.toString(); //TODO
    }
}

package abstractPattern;

import abstractPattern.utils.ContentExposureType;
import ast.Expr;
import ast.Function;
import ast.List;
import ast.Stmt;
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
    }
}

package matcher;

import ast.*;
import utils.LiteralBuilder;
import utils.UniqueMap;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * an implementation of alphabet in the extend algorithm matching algorithm
 * @param <K> base class of symbol code
 * @param <V> base class of symbol
 */
public class Alphabet<K, V> {
    private final UniqueMap<K, V> alphabetMap = new UniqueMap<>();
    private final Iterator<K> keyGenerator;
    public final K epsilonTransitionCode;
    public final K sigmaTransitionCode;

    /**
     * construct a alphabet by giving a symbol code generator, an epsilon transition code, and a sigma transition
     * code. By contract, symbol code generator should always generate a new code other than epsilon and sigma
     * transition code, when {@link Iterator#next()} is invoked
     * @param keyGenerator symbol code generator
     * @param epsilonTransitionCode epsilon transition code
     * @param sigmaTransitionCode sigma transition code
     * @throws NullPointerException if any of {@code keyGenerator}, {@code epsilonTransitionCode} or
     *                              {@code sigmaTransitionCode} is {@code null}.
     */
    public Alphabet(Iterator<K> keyGenerator, K epsilonTransitionCode, K sigmaTransitionCode) {
        this.keyGenerator = Optional.ofNullable(keyGenerator).orElseThrow(NullPointerException::new);
        this.epsilonTransitionCode = Optional.ofNullable(epsilonTransitionCode).orElseThrow(NullPointerException::new);
        this.sigmaTransitionCode = Optional.ofNullable(sigmaTransitionCode).orElseThrow(NullPointerException::new);
    }

    /**
     * add a symbol to the alphabet, the alphabet will assign a new code to the symbol, if the symbol does not
     * contained in the alphabet.
     * @param symbol symbol to be added
     * @return assigned code to such symbol
     * @throws NullPointerException if {@code symbol} is {@code null}
     * @throws IllegalStateException if duplicate symbol code is provided by the symbol code generator
     */
    public K addSymbol(V symbol) {
        if (symbol == null) throw new NullPointerException();
        if (alphabetMap.values().contains(symbol)) return alphabetMap.getInverse(symbol);
        K associatedCode = keyGenerator.next();
        if (alphabetMap.keySet().contains(associatedCode)) throw new IllegalStateException();
        alphabetMap.put(associatedCode, symbol);
        return associatedCode;
    }

    /**
     * get the symbol by providing the code of such symbol. If the given code is epsilon transition code or sigma
     * transition code, {@code null} will be provided.
     * @param code provided symbol code
     * @return symbol associate with such code. {@code null} if {@code code} is epsilon transition code or sigma
     * transition code.
     * @throws NullPointerException if {@code code} is {@code null}
     */
    public V getSymbolByCode(K code) {
        if (code == null) throw new NullPointerException();
        if (epsilonTransitionCode.equals(code) || sigmaTransitionCode.equals(code)) return null;
        if (!alphabetMap.keySet().contains(code)) throw new IllegalArgumentException();
        return alphabetMap.get(code);
    }

    /**
     * get the symbol code by providing the symbol associated to such symbol. If the given symbol is {@code null},
     * epsilon transition code is returned. If the given symbol is not contained in the alphabet, sigma transition
     * code will be returned.
     * @param symbol given symbol
     * @return symbol code associated to the given symbol
     */
    public K getCodeBySymbol(V symbol) {
        if (symbol == null) return epsilonTransitionCode;
        if (!alphabetMap.values().contains(symbol)) return sigmaTransitionCode;
        return alphabetMap.getInverse(symbol);
    }

    /**
     * convert the alphabet in to a equivalent map. This map does not contain epsilon transition code and sigma
     * transition code.
     * @return map constructed from the alphabet
     */
    public Map<K, V> toMap() {
        return alphabetMap.clone();
    }

    /**
     * generate a MATLAB function corresponding to the current alphabet. <br>
     * if alphabet is non-trivial (i.e. containing at least of symbol other than sigma transition or epsilon
     * transition). It will generate function as following:
     * <pre><code>
     *  function {outvar = variableNamespace.next()} = {functionName}({invar = variableNamespace.next()})
     *      if {matcher.accept(invar, symbol)}
     *          {outvar} = {handler.accept(symbolCode)};
     *      elseif ...
     *      else
     *          {outvar} = {handler.accept(sigmaTransitionCode)};
     *      end
     *  end
     * </code></pre>
     * if alphabet is trivial. It will simply generate function as:
     * <pre><code>
     *  function {outvar = variableNamespace.next()} = {functionName}({invar = variableNamespace.next()})
     *      {outvar} = {handler.accept(sigmaTransitionCode)};
     *  end
     * </code></pre>
     * @param matcher A function takes a input variable name expr and a symbol in the alphabet, and return a MATLAB
     *                expression determine if the input variable match to the symbol.
     * @param handler A function takes a input of symbol code and return a MATLAB expression that represent symbol
     *                code in MATLAB
     * @param functionName function name of the matcher function
     * @param variableNamespace pool of variable names
     * @return a MATLAB function corresponding to current alphabet
     * @throws NullPointerException if any of {@code mathcer}, {@code handler}, {@code functionName} or
     *                              {@code variableNamespace} is {@code null}.
     * @throws IllegalArgumentException if {@code matcher} or {@code handler} returns {@code null}.
     */
    public ast.Function toMATLABFunction(BiFunction<Expr, V, Expr> matcher, Function<K, Expr> handler,
                                         String functionName, Iterator<String> variableNamespace) {
        String outputVarName = Optional.ofNullable(variableNamespace.next()).orElseThrow(NullPointerException::new);
        String inputVarName = Optional.ofNullable(variableNamespace.next()).orElseThrow(NullPointerException::new);

        ast.Function returnFunction = new ast.Function();
        returnFunction.setName(new Name(Optional.ofNullable(functionName).orElseThrow(NullPointerException::new)));
        returnFunction.addInputParam(new Name(inputVarName));
        returnFunction.addOutputParam(new Name(outputVarName));

        Set<IfBlock> classifyBlockSet = new HashSet<>();
        for (V symbol : alphabetMap.values()) {
            Expr matcherExpr = Optional
                    .ofNullable(matcher.apply(new NameExpr(new Name(inputVarName)), symbol))
                    .orElseThrow(IllegalArgumentException::new);
            Expr handlerExpr = Optional
                    .ofNullable(handler.apply(alphabetMap.getInverse(symbol)))
                    .orElseThrow(IllegalArgumentException::new);
            IfBlock appendingClassifyBlock = new IfBlock();
            appendingClassifyBlock.setCondition(matcherExpr);

            AssignStmt handlerAssign = new AssignStmt();
            handlerAssign.setLHS(new NameExpr(new Name(outputVarName)));
            handlerAssign.setRHS(handlerExpr);
            handlerAssign.setOutputSuppressed(true);
            appendingClassifyBlock.addStmt(handlerAssign);

            classifyBlockSet.add(appendingClassifyBlock);
        }
        Expr sigmaHandler = Optional
                .ofNullable(handler.apply(sigmaTransitionCode))
                .orElseThrow(IllegalArgumentException::new);
        if (classifyBlockSet.isEmpty()) {
            AssignStmt sigmaAssign = new AssignStmt();
            sigmaAssign.setLHS(new NameExpr(new Name(outputVarName)));
            sigmaAssign.setRHS(sigmaHandler);
            sigmaAssign.setOutputSuppressed(true);
            returnFunction.addStmt(sigmaAssign);
        } else {
            ElseBlock sigmaHandlingBlock = new ElseBlock();
            AssignStmt sigmaAssign = new AssignStmt();
            sigmaAssign.setLHS(new NameExpr(new Name(outputVarName)));
            sigmaAssign.setRHS(sigmaHandler);
            sigmaAssign.setOutputSuppressed(true);
            sigmaHandlingBlock.addStmt(sigmaAssign);

            IfStmt classifyStmt = new IfStmt();
            classifyBlockSet.forEach(classifyStmt::addIfBlock);
            classifyStmt.setElseBlock(sigmaHandlingBlock);
            returnFunction.addStmt(classifyStmt);
        }
        return returnFunction;
    }

    @Override
    public String toString() {
        Set<String> contentSet = new LiteralBuilder<String>()
                .putAll(alphabetMap.entrySet().stream()
                        .map(entry -> String.format("%s=%s", entry.getKey().toString(), entry.getValue().toString()))
                        .collect(Collectors.toSet())
                )
                .put(String.format("%s=epsilon", epsilonTransitionCode.toString()))
                .put(String.format("%s=sigma",sigmaTransitionCode.toString()))
                .asSet();
        return contentSet.toString();
    }
}

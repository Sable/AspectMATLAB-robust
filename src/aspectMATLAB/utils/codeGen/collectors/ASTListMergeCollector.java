package aspectMATLAB.utils.codeGen.collectors;

import ast.ASTNode;
import ast.List;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ASTListMergeCollector<T extends ASTNode> implements Collector<Collection<T>, List<T>, List<T>> {
    @Override
    public Supplier<List<T>> supplier() {
        return ast.List<T>::new;
    }

    @Override
    public BiConsumer<List<T>, Collection<T>> accumulator() {
        return (accumulatingList, operand) -> accumulatingList.addAll(operand);
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return (collectedList) -> collectedList;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) -> {
            ast.List<T> retList = new ast.List<>();
            list1.forEach(retList::add);
            list2.forEach(retList::add);
            return retList;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.singleton(Characteristics.IDENTITY_FINISH);
    }
}

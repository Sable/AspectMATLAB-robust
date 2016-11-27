package utils.codeGen.collectors;

import ast.ASTNode;
import ast.List;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ASTListCollector<T extends ASTNode> implements Collector<T, ast.List<T>, ast.List<T>> {
    @Override
    public Supplier<List<T>> supplier() {
        return ast.List<T>::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return (list, operand) -> list.add(operand);
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return list -> list;
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

package utils.codeGen;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;

public final class AMTempVarGenerator implements Iterator<String> {
    private final String prefix;
    private Iterator<Integer> indexStream = IntStream.iterate(0, x -> x + 1).iterator();

    public AMTempVarGenerator(String prefix) {
        this.prefix = Optional.ofNullable(prefix).orElseThrow(NullPointerException::new);
        if (this.prefix.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String next() {
        return String.format("%s%d", prefix, indexStream.next());
    }

    @Override
    public boolean hasNext() {
        return indexStream.hasNext();
    }
}

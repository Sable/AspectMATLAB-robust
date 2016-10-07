package matcher.nfa;

import matcher.MATLABAlphabet;

import java.util.stream.IntStream;

public final class MATLABNFA<T> extends NFA<T>{
    public MATLABNFA(MATLABAlphabet<T> alphabet) {
        super(IntStream.iterate(1, x->x+1).iterator(), alphabet);
    }
}

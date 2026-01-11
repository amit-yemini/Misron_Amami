package msa;

import com.github.oxo42.stateless4j.delegates.Func2;

public class Transition<S, T, Arg> {
    public T trigger;
    public Func2<Arg, S> destinationStateSelector;

    public Transition(T trigger, S state) {
        this.trigger = trigger;
        this.destinationStateSelector = (arg) -> state;
    }
}

package msa;

import com.github.oxo42.stateless4j.delegates.Func2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;

public class Transition<S, T, Arg> {
    public TriggerWithParameters1<Arg, T> trigger;
    public Func2<Arg, S> destinationStateSelector;

    public Transition(TriggerWithParameters1<Arg, T> trigger, S state) {
        this.trigger = trigger;
        this.destinationStateSelector = (arg) -> state;
    }
}

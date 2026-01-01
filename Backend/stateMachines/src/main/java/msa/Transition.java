package msa;

import com.github.oxo42.stateless4j.delegates.Func3;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;

public class Transition<S, T, Arg> {
    public TriggerWithParameters2<Arg, S, T> trigger;
    public Func3<Arg,S, S> destinationStateSelector;

    public Transition(TriggerWithParameters2<Arg, S, T> trigger, Func3<Arg,S, S> destinationStateSelector) {
        this.trigger = trigger;
        this.destinationStateSelector = destinationStateSelector;
    }
}

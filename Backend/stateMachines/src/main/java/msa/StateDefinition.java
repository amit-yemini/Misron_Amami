package msa;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;

import java.util.List;

public interface StateDefinition<S, T, Arg> {
    S getState();
    Action2<Arg, S> getAction();
    List<Transition<S, T, Arg>> getTransitions();
    TriggerWithParameters2<Arg, S, T> getEntryTrigger();
    List<T> ignoreTriggers();
}

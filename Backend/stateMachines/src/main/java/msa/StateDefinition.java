package msa;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;

import java.util.List;

public interface StateDefinition<S, T, Arg> {
    S getState();
    Action1<Arg> getAction();
    List<Transition<S, T, Arg>> getTransitions();
    TriggerWithParameters1<Arg, T> getEntryTrigger();
    List<T> ignoreTriggers();
}

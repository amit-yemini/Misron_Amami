package msa;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;

import java.util.List;

public interface StateDefinition<S, T, Arg> extends BaseStateDefinition<S, T, Arg> {
    Action1<Arg> getAction();
    TriggerWithParameters1<Arg, T> getEntryTrigger();
}

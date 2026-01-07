package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import msa.*;
import msa.CacheServices.AlertStateCacheService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BaseAlertState implements StateDefinition<State, Trigger, Alert>{
    @Autowired
    private AlertStateCacheService alertStateCacheService;

    @Override
    public abstract State getState();

    @Override
    public Action2<Alert, State> getAction() {
        return (alert, state) -> {
            alertStateCacheService.updateState(alert, getState());
            execute(alert, state);
        };
    }

    public abstract void execute(Alert alert, State state);

    @Override
    public abstract List<Transition<State, Trigger, Alert>> getTransitions();

    @Override
    public abstract TriggerWithParameters2<Alert, State, Trigger> getEntryTrigger();

    @Override
    public abstract List<Trigger> ignoreTriggers();
}

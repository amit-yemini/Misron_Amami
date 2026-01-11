package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import msa.*;

import java.util.List;

public abstract class BaseAlertState implements StateDefinition<State, Trigger, Alert>{

    @Override
    public abstract State getState();

    @Override
    public Action1<Alert> getAction() {
        return this::execute;
    }

    public abstract void execute(Alert alert);

    @Override
    public abstract List<Transition<State, Trigger, Alert>> getTransitions();

    @Override
    public abstract TriggerWithParameters1<Alert, Trigger> getEntryTrigger();

    @Override
    public abstract List<Trigger> ignoreTriggers();
}

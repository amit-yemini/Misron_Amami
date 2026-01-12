package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action1;
import msa.Alert;
import msa.State;
import msa.StateDefinition;
import msa.Trigger;

public abstract class BaseAlertState implements StateDefinition<State, Trigger, Alert>{
    @Override
    public abstract State getState();

    @Override
    public Action1<Alert> getAction() {
        return this::execute;
    }

    public abstract void execute(Alert alert);
}

package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import msa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvalidatedState implements StateDefinition<State, Trigger, Alert> {
    @Autowired
    private AlertTriggers alertTriggers;

    @Override
    public State getState() {
        return State.INVALIDATED;
    }

    @Override
    public Action2<Alert, State> getAction() {
        return null;
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getPermissions() {
        return List.of();
    }

    @Override
    public TriggerWithParameters2<Alert, State, Trigger> getEntryTrigger() {
        return alertTriggers.getInvalidateTrigger();
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        // TODO: change triggers to trigger with parameters from alertTriggers
        return List.of(Trigger.NEXT,
                Trigger.INVALID,
                Trigger.START_AUTO,
                Trigger.START_MANUAL);
    }
}

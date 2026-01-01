package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import msa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitialState implements StateDefinition<State, Trigger, Alert> {
    @Autowired
    private AlertTriggers alertTriggers;

    @Override
    public State getState() {
        return State.INITIAL;
    }

    @Override
    public Action2<Alert, State> getAction() {
        return null;
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getPermissions() {
        return List.of(
                new Transition<>(
                        alertTriggers.getStartAutoTrigger(),
                        (alert, state) -> State.SANITY_CHECK
                ),
                new Transition<>(
                        alertTriggers.getStartManualTrigger(),
                        (alert, state) -> State.DISTRIBUTION
                )
        );
    }

    @Override
    public TriggerWithParameters2<Alert, State, Trigger> getEntryTrigger() {
        return null;
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of(Trigger.NEXT, Trigger.INVALID);
    }
}


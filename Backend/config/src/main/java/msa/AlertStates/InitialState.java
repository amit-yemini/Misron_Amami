package msa.AlertStates;

import msa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitialState extends ActionlessBaseAlertState {
    @Autowired
    private AlertTriggers alertTriggers;

    @Override
    public State getState() {
        return State.INITIAL;
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getTransitions() {
        return List.of(
                new Transition<>(
                        alertTriggers.get(Trigger.START_AUTO),
                        State.SANITY_CHECK
                ),
                new Transition<>(
                        alertTriggers.get(Trigger.START_MANUAL),
                        State.DISTRIBUTION
                )
        );
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of(Trigger.NEXT, Trigger.INVALID);
    }
}


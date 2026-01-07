package msa.AlertStates;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import msa.*;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvalidatedState extends BaseAlertState {
    @Autowired
    private AlertTriggers alertTriggers;
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;

    @Override
    public State getState() {
        return State.INVALIDATED;
    }

    @Override
    public void execute(Alert alert) {
        incomingAlertStateMachineCacheService.removeStateMachine(alert);
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getTransitions() {
        return List.of();
    }

    @Override
    public TriggerWithParameters1<Alert, Trigger> getEntryTrigger() {
        return alertTriggers.get(Trigger.INVALID);
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of(Trigger.NEXT,
                Trigger.INVALID,
                Trigger.START_AUTO,
                Trigger.START_MANUAL);
    }
}

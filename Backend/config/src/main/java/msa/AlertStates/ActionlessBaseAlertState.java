package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import msa.Alert;
import msa.CacheServices.AlertStateCacheService;
import msa.State;
import msa.StateDefinition;
import msa.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ActionlessBaseAlertState implements StateDefinition<State, Trigger, Alert> {
    @Autowired
    private AlertStateCacheService alertStateCacheService;

    @Override
    public TriggerWithParameters1<Alert, Trigger> getEntryTrigger() {
        return null;
    }

    @Override
    public Action1<Alert> getAction() {
        return (alert) -> {
            alertStateCacheService.updateState(alert, getState());
        };
    }
}

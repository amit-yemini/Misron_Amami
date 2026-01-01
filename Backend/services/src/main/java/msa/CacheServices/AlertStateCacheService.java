package msa.CacheServices;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import lombok.extern.slf4j.Slf4j;
import msa.Alert;
import msa.AlertDiscreditedException;
import msa.State;
import msa.Trigger;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertStateCacheService{
    @Autowired
    private Cache<Integer, StateMachine<State, Trigger>> alertStateMachineCache;

    public int getKey(Alert alert) {
        return alert.getIncidentId();
    }

    public void addAlertStateMachine(int key, StateMachine<State, Trigger> alertStateMachine) {
        if (alertStateMachineCache.containsKey(key)) {
            alertStateMachineCache.get(key).fire(Trigger.INVALID);
        }
        alertStateMachineCache.put(key, alertStateMachine);
    }

    public void checkAlertRelevance(int incidentId) {
        if (alertStateMachineCache.containsKey(incidentId)) {
            if (alertStateMachineCache.get(incidentId).getState() == State.INVALIDATED) {
                throw new AlertDiscreditedException(incidentId);
            }
        }
    }

    public StateMachine<State, Trigger> getAlertStateMachine(int incidentId) {
        return alertStateMachineCache.get(incidentId);
    }

    public void fire(TriggerWithParameters2<Alert, State, Trigger> trigger, Alert alert, State state) {
        log.info("firing trigger {} for alert {}", trigger.getTrigger(), getKey(alert));
        alertStateMachineCache.get(this.getKey(alert)).fire(trigger, alert, state);
    }
}

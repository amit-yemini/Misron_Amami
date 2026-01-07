package msa.CacheServices;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import lombok.extern.slf4j.Slf4j;
import msa.Alert;
import msa.State;
import msa.Trigger;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IncomingAlertStateMachineCacheService {
    @Autowired
    private Cache<String, StateMachine<State, Trigger>> incomingAlertStateMachineCache;

    public String getKey(Alert alert) {
        return alert.getIncidentId() + "_" + alert.getIdentifier();
    }

    public void addIncomingAlert(Alert alert, StateMachine<State, Trigger> stateMachine) {
        incomingAlertStateMachineCache.put(this.getKey(alert), stateMachine);
    }

    public void fire(TriggerWithParameters1<Alert, Trigger> trigger, Alert alert, State state) {
        log.info("firing trigger {} for alert {}", trigger.getTrigger(), getKey(alert));
        incomingAlertStateMachineCache.get(this.getKey(alert)).fire(trigger, alert);
    }

    public StateMachine<State, Trigger> getIncomingAlertStateMachine(Alert alert) {
        return incomingAlertStateMachineCache.get(this.getKey(alert));
    }

    public void removeStateMachine(Alert alert) {
        incomingAlertStateMachineCache.remove(this.getKey(alert));
    }
}

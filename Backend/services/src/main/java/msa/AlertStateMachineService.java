package msa;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.extern.slf4j.Slf4j;
import msa.CacheServices.AlertStateCacheService;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AlertStateMachineService {
    @Autowired
    private Cache<String, StateMachine<State, Trigger>> alertStateMachineCache;
    @Autowired
    private AlertTriggers alertTriggers;
    @Autowired
    @Lazy
    private StateMachineConfig<State, Trigger> stateMachineConfig;
    @Autowired
    @Lazy
    private AlertStateCacheService alertStateCacheService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public String getKey(Alert alert) {
        return alert.getIncidentId() + "_" + alert.getIdentifier();
    }

    public void addIncomingAlert(Alert alert) {
        StateMachine<State, Trigger> stateMachine =
                new StateMachine<>(State.INITIAL, stateMachineConfig);
        alertStateMachineCache.put(this.getKey(alert), stateMachine);
        stateMachine.fire(alertTriggers.get(Trigger.START_AUTO), alert);
    }

    public void fire(Trigger trigger, Alert alert) {
        log.info("firing trigger {} for alert {}", trigger, getKey(alert));
        alertStateMachineCache.get(this.getKey(alert)).fire(alertTriggers.get(trigger), alert);
    }

    public void fireWithDelay(Trigger trigger, Alert alert, int delaySeconds, Runnable task) {
        log.info("firing trigger {} for alert {} with delay of {} seconds", trigger, getKey(alert), delaySeconds);
        scheduler.schedule(() -> {
                    task.run();
                    fire(trigger, alert);
                },
                delaySeconds,
                TimeUnit.SECONDS
        );
    }

    public StateMachine<State, Trigger> getIncomingAlertStateMachine(Alert alert) {
        return alertStateMachineCache.get(this.getKey(alert));
    }

    public void removeStateMachine(Alert alert) {
        alertStateMachineCache.remove(this.getKey(alert));
    }

    public void handleErrorInStateMachine(Alert alert) {
        log.info("firing trigger INVALID for alert {} because of an error in state machine", getKey(alert));
        alertStateMachineCache.get(getKey(alert)).fire(alertTriggers.get(Trigger.INVALID), alert);
    }

    public void cancelAlert(int incidentId) {
        Alert alert = alertStateCacheService.getAlert(incidentId);
        fire(Trigger.INVALID, alert);
    }
}

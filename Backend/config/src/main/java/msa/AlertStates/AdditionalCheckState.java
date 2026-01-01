package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertStateCacheService;
import msa.CacheServices.AlertTypeCacheService;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class AdditionalCheckState implements StateDefinition<State, Trigger, Alert> {
    @Autowired
    private AlertStateCacheService alertStateCacheService;
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;
    @Autowired
    private AlertTriggers alertTriggers;

    @Override
    public State getState() {
        return State.ADDITIONAL_CHECK;
    }

    @Override
    public Action2<Alert, State> getAction() {
        return (alert, state) -> {
            additionalCheck(alert);
            alertStateCacheService.fire(alertTriggers.getNextTrigger(), alert, state);
        };
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getPermissions() {
        return List.of(new Transition<>(
                alertTriggers.getNextTrigger(),
                (alert, state) -> {
                    int time = calculateInterventionTime(alert.getImpact().getTime(), alert.getAlertTypeId());
                    return (time <= 0) ? State.DISTRIBUTION : State.WAITING;
                }
        ));
    }

    @Override
    public TriggerWithParameters2<Alert, State, Trigger> getEntryTrigger() {
        return alertTriggers.getNextTrigger();
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of();
    }

    public void additionalCheck(Alert alert) throws AlertDiscreditedException {
        log.info("starting additional check for alert: incident: {}, identifier: {}",
                alert.getIncidentId(), alert.getIdentifier());
        checkSendTime(alert.getTimeSent());
        checkImpactTime(alert.getImpact().getTime());
        checkAlertRelevance(alert.getIncidentId());
        addAlertStateMachineFromIncomingCache(alert);
    }

    private void checkSendTime(long sendTime) {
        if (!Instant.ofEpochSecond(sendTime).isBefore(Instant.now())) {
            throw new DateTimeException("Send time must be in the past");
        }
    }

    private void checkImpactTime(long impactTime) {
        if (!Instant.ofEpochSecond(impactTime).isAfter(Instant.now())) {
            throw new DateTimeException("msa.Impact time must be in the future");
        }
    }

    private void checkAlertRelevance(int incidentId) throws AlertDiscreditedException {
        alertStateCacheService.checkAlertRelevance(incidentId);
    }

    public int calculateInterventionTime(long impactTime, int alertTypeId) {
        int distributionTime = alertTypeCacheService.getDistributionTime(alertTypeId);

        return Math.toIntExact(impactTime - Instant.now().getEpochSecond() - distributionTime);
    }

    private void addAlertStateMachineFromIncomingCache(Alert alert) {
        alertStateCacheService
                .addAlertStateMachine(alertStateCacheService.getKey(alert),
                        incomingAlertStateMachineCacheService.getIncomingAlertStateMachine(alert));
    }
}

package msa.AlertStates;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertStateCacheService;
import msa.CacheServices.AlertTypeCacheService;
import msa.mappers.AlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class AdditionalCheckState extends BaseAlertState {
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;
    @Autowired
    private AlertStateMachineService alertStateMachineService;
    @Autowired
    private AlertStateCacheService alertStateCacheService;
    @Autowired
    private AlertTriggers alertTriggers;
    @Autowired
    private SocketIOSender socketIOSender;
    @Autowired
    private AlertMapper alertMapper;

    @Override
    public State getState() {
        return State.ADDITIONAL_CHECK;
    }

    @Override
    public void execute(Alert alert) {
        log.info("starting additional check for alert: incident: {}, identifier: {}",
                alert.getIncidentId(), alert.getIdentifier());
        checkSendTime(alert.getTimeSent(), alert);
        checkImpactTime(alert.getImpact().getTime(), alert);
        alertStateCacheService.checkAlertRelevance(alert.getIncidentId(), alert);
        alertStateCacheService.addAlertContext(alert, getState());
        int interventionTime = calculateInterventionTime(alert.getImpact().getTime(), alert.getAlertTypeId());

        if (interventionTime <= 0) {
            alertStateMachineService.fire(Trigger.NEXT, alert);
        } else {
            sendAlertToClients(alertMapper.toDistribution(alert));
            log.info("scheduling wait period of {} seconds for alert: incident: {}, identifier: {}",
                    interventionTime, alert.getIncidentId(), alert.getIdentifier());
            alertStateMachineService.fireWithDelay(Trigger.NEXT, alert, interventionTime, () -> {
                socketIOSender.sendCancellationToAll(alert.getIncidentId());
            });
        }
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getTransitions() {
        return List.of(new Transition<>(
                        Trigger.NEXT,
                        State.DISTRIBUTION
                ),
                new Transition<>(
                        Trigger.INVALID,
                        State.INVALIDATED)
        );
    }

    @Override
    public TriggerWithParameters1<Alert, Trigger> getEntryTrigger() {
        return alertTriggers.get(Trigger.NEXT);
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of();
    }

    private void checkSendTime(long sendTime, Alert alert) {
        if (!Instant.ofEpochSecond(sendTime).isBefore(Instant.now())) {
            throw new TimeException("Send time must be in the past", alert);
        }
    }

    private void checkImpactTime(long impactTime, Alert alert) {
        if (!Instant.ofEpochSecond(impactTime).isAfter(Instant.now())) {
            throw new TimeException("Impact time must be in the future", alert);
        }
    }

    public int calculateInterventionTime(long impactTime, int alertTypeId) {
        int distributionTime = alertTypeCacheService.getDistributionTime(alertTypeId);

        return Math.toIntExact(impactTime - Instant.now().getEpochSecond() - distributionTime);
    }

    public void sendAlertToClients(AlertDistribution alertDistribution) {
        log.info("sending alert to clients");
        socketIOSender.sendAlertToAll(alertDistribution);
    }
}

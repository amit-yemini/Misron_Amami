package msa.AlertStates;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertStateCacheService;
import msa.CacheServices.AlertTypeCacheService;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import msa.mappers.AlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AdditionalCheckState extends BaseAlertState {
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;
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
        checkAlertRelevance(alert.getIncidentId(), alert);
        addAlertStateMachineFromIncomingCache(alert);
        int delayTime = calculateInterventionTime(alert.getImpact().getTime(), alert.getAlertTypeId());
        if (delayTime <= 0) {
            incomingAlertStateMachineCacheService.fire(alertTriggers.get(Trigger.NEXT), alert);
        } else {
            sendAlertToClients(alertMapper.toDistribution(alert));
            scheduleWaitExpired(alert, delayTime);
        }
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getTransitions() {
        return List.of(new Transition<>(
                alertTriggers.get(Trigger.NEXT),
                State.DISTRIBUTION
        ),
                new Transition<>(
                        alertTriggers.get(Trigger.INVALID),
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

    private void checkAlertRelevance(int incidentId, Alert alert) throws AlertDiscreditedException {
        alertStateCacheService.checkAlertRelevance(incidentId, alert);
    }

    public int calculateInterventionTime(long impactTime, int alertTypeId) {
        int distributionTime = alertTypeCacheService.getDistributionTime(alertTypeId);

        return Math.toIntExact(impactTime - Instant.now().getEpochSecond() - distributionTime);
    }

    private void addAlertStateMachineFromIncomingCache(Alert alert) {
        alertStateCacheService.addAlertContext(alert, getState());
    }

    public void sendAlertToClients(AlertDistribution alertDistribution) {
        log.info("sending alert to clients");
        socketIOSender.sendAlertToAll(alertDistribution);
    }

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2);

    public void scheduleWaitExpired(Alert alert, int delaySeconds) {
        log.info("scheduling wait period of {} seconds for alert: incident: {}, identifier: {}",
                delaySeconds, alert.getIncidentId(), alert.getIdentifier());
        scheduler.schedule(
                () -> onWaitExpired(alert), delaySeconds, TimeUnit.SECONDS
        );
    }

    private void onWaitExpired(Alert alert) {
        log.info("sending cancellation to clients");
        socketIOSender.sendCancellationToAll(alert.getIncidentId());
        incomingAlertStateMachineCacheService.fire(alertTriggers.get(Trigger.NEXT), alert);
    }
}

package msa.AlertStates;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertTypeCacheService;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import msa.mappers.AlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WaitingState extends BaseAlertState {
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;
    @Autowired
    private SocketIOSender socketIOSender;
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;
    @Autowired
    private AlertTriggers alertTriggers;
    @Autowired
    private AlertMapper alertMapper;

    @Override
    public State getState() {
        return State.WAITING;
    }

    @Override
    public void execute(Alert alert, State state) {
        sendAlertToClients(alertMapper.toDistribution(alert));
        int delaySeconds = calculateInterventionTime(
                alert.getImpact().getTime(),
                alert.getAlertTypeId()
        );

        scheduleWaitExpired(alert, delaySeconds);
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getTransitions() {
        return List.of(
                new Transition<>(
                        alertTriggers.getNextTrigger(),
                        (alert, state) -> State.DISTRIBUTION
                ),
                new Transition<>(
                        alertTriggers.getInvalidateTrigger(),
                        (alert, state) -> State.INVALIDATED
                )
        );
    }

    @Override
    public TriggerWithParameters2<Alert, State, Trigger> getEntryTrigger() {
        return alertTriggers.getNextTrigger();
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of();
    }

    public int calculateInterventionTime(long impactTime, int alertTypeId) {
        int distributionTime = alertTypeCacheService.getDistributionTime(alertTypeId);

        return Math.toIntExact(impactTime - Instant.now().getEpochSecond() - distributionTime);
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
        incomingAlertStateMachineCacheService.fire(alertTriggers.getNextTrigger(), alert, getState());
    }
}

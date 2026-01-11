package msa.AlertStates;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertTypeCacheService;
import msa.mappers.AlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DistributionState extends BaseAlertState {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    @Autowired
    private ZofarimService zofarimService;
    @Autowired
    private SocketIOSender socketIOSender;
    @Autowired
    private AlertStateMachineService alertStateMachineService;
    @Autowired
    private AlertTriggers alertTriggers;
    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private AlertTypeCacheService alertTypeCacheService;

    @Override
    public State getState() {
        return State.DISTRIBUTION;
    }

    @Override
    public void execute(Alert alert) {
        distribute(alertMapper.toDistribution(alert));
        scheduler.schedule(
                () -> alertStateMachineService.fire(alertTriggers.get(Trigger.INVALID), alert),
                alertTypeCacheService.getDistributionTime(alert.getAlertTypeId()),
                TimeUnit.SECONDS
        );
//        alertStateMachineService.fire(alertTriggers.get(Trigger.INVALID), alert);
    }

    @Override
    public List<Transition<State, Trigger, Alert>> getTransitions() {
        return List.of(new Transition<>(Trigger.INVALID, State.INVALIDATED));
    }

    @Override
    public TriggerWithParameters1<Alert, Trigger> getEntryTrigger() {
        return alertTriggers.get(Trigger.NEXT);
    }

    @Override
    public List<Trigger> ignoreTriggers() {
        return List.of();
    }

    public void distribute(AlertDistribution alertDistribution) {
        log.info("distributing alert: {}", alertDistribution.getIncidentId());
        zofarimService.sendRequest(alertDistribution);
    }
}

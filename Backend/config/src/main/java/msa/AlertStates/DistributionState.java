package msa.AlertStates;

import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import msa.mappers.AlertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DistributionState extends BaseAlertState{
    @Autowired
    private ZofarimService zofarimService;
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
                return State.DISTRIBUTION;
            }

    @Override
    public void execute(Alert alert) {
        distribute(alertMapper.toDistribution(alert));
        incomingAlertStateMachineCacheService.fire(alertTriggers.get(Trigger.INVALID), alert, getState());
    }

    @Override
            public List<Transition<State, Trigger, Alert>> getTransitions() {
                return List.of(new Transition<>(
                        alertTriggers.get(Trigger.INVALID),
                        State.INVALIDATED
                ));
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

    public void sendCancellationToClients(int incidentId) {
        log.info("sending cancellation to clients");
        socketIOSender.sendCancellationToAll(incidentId);
    }
}

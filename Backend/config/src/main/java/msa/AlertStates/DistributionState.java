package msa.AlertStates;

import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import lombok.extern.slf4j.Slf4j;
import msa.*;
import msa.CacheServices.AlertStateCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DistributionState implements StateDefinition<State, Trigger, Alert>{
    @Autowired
    private ZofarimService zofarimService;
    @Autowired
    private SocketIOSender socketIOSender;
    @Autowired
    private AlertStateCacheService alertStateCacheService;
    @Autowired
    private AlertTriggers alertTriggers;

            @Override
            public State getState() {
                return State.DISTRIBUTION;
            }

            @Override
            public Action2<Alert, State> getAction() {
                return (alert, state) -> {
                    if (state == State.WAITING) {
                        sendCancellationToClients(alert.getIncidentId());
                    }

                    distribute(new AlertDistribution(alert));
                    alertStateCacheService.fire(alertTriggers.getInvalidateTrigger(), alert, state);
                };
            }

            @Override
            public List<Transition<State, Trigger, Alert>> getPermissions() {
                return List.of(new Transition<>(
                        alertTriggers.getInvalidateTrigger(),
                        (alert, state) -> State.INVALIDATED
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

    public void distribute(AlertDistribution alertDistribution) {
        log.info("distributing alert: {}", alertDistribution.getIncidentId());
        zofarimService.sendRequest(alertDistribution);
    }

    public void sendCancellationToClients(int incidentId) {
        log.info("sending cancellation to clients");
        socketIOSender.sendCancellationToAll(incidentId);
    }
}

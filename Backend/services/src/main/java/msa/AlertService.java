package msa;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.extern.slf4j.Slf4j;
import msa.CacheServices.IncomingAlertStateMachineCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertService {
    @Autowired
    private StateMachineConfig<State, Trigger> stateMachineConfig;
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;
    @Autowired
    private AlertTriggers alertTriggers;

    public void processAlert(Alert alert) throws NotFoundException, InvalidSenderException {
        StateMachine<State, Trigger> stateMachine =
                new StateMachine<>(State.INITIAL, stateMachineConfig);
        incomingAlertStateMachineCacheService.addIncomingAlert(alert, stateMachine);
        stateMachine.fire(alertTriggers.getStartAutoTrigger(), alert, State.INITIAL);
    }
}

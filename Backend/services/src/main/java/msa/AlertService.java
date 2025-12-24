package msa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertService {
    @Autowired
    private AlertProcessingService alertProcessingService;

    public void processAlert(Alert alert) throws NotFoundException, InvalidSenderException {
        AlertStateMachine stateMachine =
                new AlertStateMachine(alertProcessingService, alert);

        stateMachine.fire(Trigger.START);
    }
}

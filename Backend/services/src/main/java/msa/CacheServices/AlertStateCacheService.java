package msa.CacheServices;

import lombok.extern.slf4j.Slf4j;
import msa.*;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class AlertStateCacheService{
    @Autowired
    private Cache<Integer, AlertContext> alertContextCache;
    @Autowired
    private AlertStateMachineService alertStateMachineService;
    @Autowired
    private AlertStateCacheService alertStateCacheService;
    @Autowired
    private AlertTriggers alertTriggers;

    public int getKey(Alert alert) {
        return alert.getIncidentId();
    }

    public void addAlertContext(Alert alert, State state) {
        if (alertContextCache.containsKey(getKey(alert))) {
            alertStateMachineService
                    .fire(Trigger.INVALID,
                            alertContextCache.get(getKey(alert)).getAlert());
        }
        alertContextCache.put(getKey(alert), new AlertContext(alert, state));
    }

    public void checkAlertRelevance(int incidentId, Alert alert) {
        if (alertContextCache.containsKey(incidentId)) {
            if (alertContextCache.get(incidentId).getState() == State.INVALIDATED) {
                throw new AlertDiscreditedException(incidentId, alert);
            }
        }
    }

    public void updateState(Alert alert, State state) {
        if (alertContextCache.containsKey(getKey(alert))) {
            AlertContext AlertContext = alertContextCache.get(getKey(alert));

            if (alert.getIdentifier().equals(AlertContext.getAlert().getIdentifier())) {
                log.info("updating state of alert {} to {}", getKey(alert), state);
                AlertContext.setState(state);
            }

        }
    }

    public Alert getAlert(int incidentId) {
        return alertContextCache.get(incidentId).getAlert();
    }
}

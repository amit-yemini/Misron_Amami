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
    private AlertTriggers alertTriggers;

    public int getKey(Alert alert) {
        return alert.getIncidentId();
    }

    public void addAlertContext(Alert alert, State state) {
        if (alertContextCache.containsKey(getKey(alert))) {
            alertStateMachineService
                    .fire(alertTriggers.get(Trigger.INVALID),
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
        if (alertContextCache.containsKey(getKey(alert))
                && Objects.equals(alert.getIdentifier(), alertContextCache.get(getKey(alert)).getAlert().getIdentifier())) {
            log.info("updating state of alert {} to {}", getKey(alert), state);
            alertContextCache.get(getKey(alert)).setState(state);
        }
    }
}

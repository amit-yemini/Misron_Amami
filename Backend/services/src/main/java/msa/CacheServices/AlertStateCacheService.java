package msa.CacheServices;

import lombok.extern.slf4j.Slf4j;
import msa.*;
import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertStateCacheService{
    @Autowired
    private Cache<Integer, AlertContext> alertContextCache;
    @Autowired
    private IncomingAlertStateMachineCacheService incomingAlertStateMachineCacheService;
    @Autowired
    private AlertTriggers alertTriggers;

    public int getKey(Alert alert) {
        return alert.getIncidentId();
    }

    public void addAlertContext(Alert alert, State state) {
        if (alertContextCache.containsKey(getKey(alert))) {
            incomingAlertStateMachineCacheService
                    .fire(alertTriggers.get(Trigger.INVALID),
                            alertContextCache.get(getKey(alert)).getAlert(), state);
        }
        alertContextCache.put(getKey(alert), new AlertContext(alert, state));
    }

    public void checkAlertRelevance(int incidentId) {
        if (alertContextCache.containsKey(incidentId)) {
            if (alertContextCache.get(incidentId).getState() == State.INVALIDATED) {
                throw new AlertDiscreditedException(incidentId);
            }
        }
    }

    public void updateState(Alert alert, State state) {
        if (alertContextCache.containsKey(getKey(alert))) {
            alertContextCache.get(getKey(alert)).setState(state);
        }
    }
}

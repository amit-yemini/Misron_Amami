package msa;

import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertStateCacheService{
    @Autowired
    private Cache<Integer, AlertStateMachine> alertStateMachineCache;

    public void addAlertStateMachine(int key, AlertStateMachine alertStateMachine) {
        alertStateMachineCache.put(key, alertStateMachine);
    }

    public void checkAlertRelevance(int incidentId) {
        if (alertStateMachineCache.containsKey(incidentId)) {
            if (alertStateMachineCache.get(incidentId).getState() == State.DISTRIBUTION) {
                throw new AlertDiscreditedException(incidentId);
            }
        }
    }

    public AlertStateMachine getAlertStateMachine(int incidentId) {
        return alertStateMachineCache.get(incidentId);
    }
}

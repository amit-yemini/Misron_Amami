package msa;

public interface AlertProcessingActions {
    void sanityCheck(Alert alert);
    void additionalCheck(Alert alert);
    int calculateInterventionTime(long impactTime, int alertTypeId);
    void scheduleWaitExpired(Alert alert, int delaySeconds);
    void distribute(AlertDistribution distribution);
    void sendToClients(AlertDistribution alert);
    void addAlertStateMachine(int incidentId, AlertStateMachine stateMachine);
}

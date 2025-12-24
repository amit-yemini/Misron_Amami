package msa;

public interface AlertProcessingActions {
    void sanityCheck(Alert alert);
    void additionalCheck(Alert alert);
    int calculateInterventionTime(long impactTime, int alertTypeId);
    void scheduleWaitExpired(Alert alert, int delaySeconds);
    void distribute(AlertDistribution distribution);
    void sendAlertToClients(AlertDistribution alert);
    void sendCancellationToClients(int incidentId);
    void addAlertStateMachine(int incidentId, AlertStateMachine stateMachine);
}

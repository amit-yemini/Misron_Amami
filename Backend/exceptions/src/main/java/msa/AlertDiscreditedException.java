package msa;

public class AlertDiscreditedException extends AlertProcessingException {
    public AlertDiscreditedException(int id, Alert alert) {
        super("Alert with id " + id + " is manual or was cancelled", alert);
    }
}

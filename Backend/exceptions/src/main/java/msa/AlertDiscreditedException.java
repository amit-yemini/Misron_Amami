package msa;

public class AlertDiscreditedException extends RuntimeException {
    public AlertDiscreditedException(int id) {
        super("Alert with id " + id + " is manual or was cancelled");
    }
}

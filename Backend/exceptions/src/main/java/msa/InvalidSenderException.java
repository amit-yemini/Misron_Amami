package msa;

public class InvalidSenderException extends AlertProcessingException{
    public InvalidSenderException(String senderName, Alert alert) {
        super("No sender with name " + senderName, alert);
    }
}

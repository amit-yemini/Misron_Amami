package msa;

public class NotFoundException extends AlertProcessingException{
    public NotFoundException(String message, Alert alert) {
        super(message, alert);
    }
}

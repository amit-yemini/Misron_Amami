package msa;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AlertProcessingException extends RuntimeException{
    private Alert alert;
    public AlertProcessingException(String message, Alert alert) {
        super(message);
        setAlert(alert);
    }
}

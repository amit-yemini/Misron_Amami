package msa;

public class TimeException extends AlertProcessingException{
  public TimeException(String message, Alert alert) {
    super(message, alert);
  }
}

package msa;

public class InvalidSenderException extends RuntimeException{
    public InvalidSenderException(String senderName) {
        super("No sender with name " + senderName);
    }
}

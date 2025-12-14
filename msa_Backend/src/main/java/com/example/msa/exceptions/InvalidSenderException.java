package com.example.msa.exceptions;

public class InvalidSenderException extends Exception{
    public InvalidSenderException(String senderName) {
        super("No sender with name " + senderName);
    }
}

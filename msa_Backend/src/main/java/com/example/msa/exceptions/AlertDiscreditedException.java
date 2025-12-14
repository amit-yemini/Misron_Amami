package com.example.msa.exceptions;

public class AlertDiscreditedException extends Exception {
    public AlertDiscreditedException(int id) {
        super("Alert with id " + id + " is manual or was cancelled");
    }
}

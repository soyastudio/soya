package com.albertsons.specright.service;

public class SpecrightException extends Exception {
    public SpecrightException() {
    }

    public SpecrightException(String message) {
        super(message);
    }

    public SpecrightException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpecrightException(Throwable cause) {
        super(cause);
    }
}

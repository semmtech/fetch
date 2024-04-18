package com.semmtech.laces.fetch.configuration.exceptions;

public class LacesTokenAuthorizationException extends CodedException {
    public LacesTokenAuthorizationException(String message) {
        super(message, "[AUTHORIZATION ERROR]");
    }
}

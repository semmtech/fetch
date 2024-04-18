package com.semmtech.laces.fetch.configuration.exceptions;

public abstract class CodedException extends RuntimeException {
    private final String code;

    public CodedException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String codedMessage() {
        return code + ":" + getMessage();
    }

    public ErrorMessage toErrorMessage() {
        return ErrorMessage.builder()
                .code(code)
                .message(getMessage())
                .build();
    }
}

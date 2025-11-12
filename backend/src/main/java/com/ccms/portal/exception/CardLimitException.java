package com.ccms.portal.exception;

import java.io.Serial;

public class CardLimitException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public CardLimitException() {
        super();
    }

    public CardLimitException(String message) {
        super(message);
    }

    public CardLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardLimitException(Throwable cause) {
        super(cause);
    }
}

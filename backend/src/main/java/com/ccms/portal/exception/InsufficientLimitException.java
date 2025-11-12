package com.ccms.portal.exception;

public class InsufficientLimitException extends RuntimeException {
  public InsufficientLimitException(String message) {
    super(message);
  }

  public InsufficientLimitException(String message, Throwable cause) {
    super(message, cause);
  }
}

package com.ccms.portal.exception;

public class CardNotFoundException extends RuntimeException {
  public CardNotFoundException(String message) {
    super(message);
  }

  public CardNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

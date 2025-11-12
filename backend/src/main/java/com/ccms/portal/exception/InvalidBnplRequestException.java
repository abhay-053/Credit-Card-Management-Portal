package com.ccms.portal.exception;

public class InvalidBnplRequestException extends RuntimeException {
  public InvalidBnplRequestException(String message) {
    super(message);
  }

  public InvalidBnplRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}

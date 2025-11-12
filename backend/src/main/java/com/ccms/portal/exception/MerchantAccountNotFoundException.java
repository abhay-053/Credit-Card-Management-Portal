package com.ccms.portal.exception;

public class MerchantAccountNotFoundException extends RuntimeException {
  public MerchantAccountNotFoundException(String message) {
    super(message);
  }

  public MerchantAccountNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

package com.ccms.portal.exception;

import com.ccms.portal.dto.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class TransactionExceptionHandler {

  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleTransactionNotFound(TransactionNotFoundException ex) {
    log.error("Transaction not found: {}", ex.getMessage());
    return buildErrorResponse("Transaction Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
  }

  @ExceptionHandler(InvalidTransactionException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidTransaction(InvalidTransactionException ex) {
    log.error("Invalid transaction: {}", ex.getMessage());
    return buildErrorResponse("Invalid Transaction", ex.getMessage(), HttpStatus.BAD_REQUEST, null);
  }

  @ExceptionHandler(CreditCardNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleCreditCardNotFound(CreditCardNotFoundException ex) {
    log.error("Credit Card not found in transaction context: {}", ex.getMessage());
    return buildErrorResponse("Credit Card Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
  }

  @ExceptionHandler(InsufficientLimitException.class)
  public ResponseEntity<ApiErrorResponse> handleInsufficientLimit(InsufficientLimitException ex) {
    log.error("Insufficient limit in transaction: {}", ex.getMessage());
    return buildErrorResponse("Insufficient Credit Limit", ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleTransactionValidationException(MethodArgumentNotValidException ex) {
    log.error("Transaction validation error: {}", ex.getMessage());

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

    return buildErrorResponse("Transaction Validation Failed", "Invalid transaction data", HttpStatus.BAD_REQUEST,
        fieldErrors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    log.error("Illegal argument in transaction: {}", ex.getMessage());
    return buildErrorResponse("Invalid Argument", ex.getMessage(), HttpStatus.BAD_REQUEST, null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleTransactionGenericException(Exception ex) {
    log.error("Unexpected error in transaction module: {}", ex.getMessage(), ex);
    return buildErrorResponse("Transaction Error", "An unexpected error occurred in transaction processing",
        HttpStatus.INTERNAL_SERVER_ERROR, null);
  }

  private ResponseEntity<ApiErrorResponse> buildErrorResponse(
      String error, String message, HttpStatus status, Map<String, String> fieldErrors) {

    ApiErrorResponse apiError = ApiErrorResponse.builder()
        .error(error)
        .message(message)
        .status(status.value())
        .timestamp(LocalDateTime.now())
        .fieldErrors(fieldErrors)
        .build();

    return ResponseEntity.status(status).body(apiError);
  }
}

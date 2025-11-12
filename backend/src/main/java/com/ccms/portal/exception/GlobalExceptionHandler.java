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
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return buildErrorResponse("User Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(CreditCardNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCreditCardNotFound(CreditCardNotFoundException ex) {
        log.error("Credit Card not found: {}", ex.getMessage());
        return buildErrorResponse("Credit Card Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(CardTypeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCardTypeNotFound(CardTypeNotFoundException ex) {
        log.error("Card Type not found: {}", ex.getMessage());
        return buildErrorResponse("Card Type Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCardNotFoundException(CardNotFoundException ex) {
        log.error("Card not found: {}", ex.getMessage());
        return buildErrorResponse("Card Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(InsufficientLimitException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientLimit(InsufficientLimitException ex) {
        log.error("Insufficient limit: {}", ex.getMessage());
        return buildErrorResponse("Insufficient Credit Limit", ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, null);
    }

    @ExceptionHandler(MerchantAccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMerchantAccountNotFoundException(
            MerchantAccountNotFoundException ex) {
        log.error("Merchant account not found: {}", ex.getMessage());
        return buildErrorResponse("Merchant Account Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(InvalidCardStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCardStatusException(InvalidCardStatusException ex) {
        log.error("Invalid card status: {}", ex.getMessage());
        return buildErrorResponse("Invalid Card Status", ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(InvalidBnplRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBnplRequestException(InvalidBnplRequestException ex) {
        log.error("Invalid BNPL request: {}", ex.getMessage());
        return buildErrorResponse("Invalid BNPL Request", ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse("Validation Failed", "Invalid input data", HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @ExceptionHandler(CardLimitException.class)
    public ResponseEntity<ApiErrorResponse> handleCardLimitException(CardLimitException ex) {
        log.error("Card limit error: {}", ex.getMessage());
        return buildErrorResponse("Card Limit Error", ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse("Internal Server Error", "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    @ExceptionHandler(CardApplicationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCardApplicationNotFound(CardApplicationNotFoundException ex) {
        log.error("Card application not found: {}", ex.getMessage());
        return buildErrorResponse("Card Application Not Found", ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(UnauthorizedApplicationActionException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedApplicationAction(
            UnauthorizedApplicationActionException ex) {
        log.error("Unauthorized application action: {}", ex.getMessage());
        return buildErrorResponse("Unauthorized Action", ex.getMessage(), HttpStatus.FORBIDDEN, null);
    }

    @ExceptionHandler(BnplNotEligibleException.class)
    public ResponseEntity<ApiErrorResponse> handleBnplNotEligible(BnplNotEligibleException ex) {
        log.error("BNPL not eligible: {}", ex.getMessage());
        return buildErrorResponse("BNPL Not Available", ex.getMessage(), HttpStatus.FORBIDDEN, null);
    }
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateApp(DuplicateApplicationException ex) {
        log.error("Duplicate application attempt: {}", ex.getMessage());
        return buildErrorResponse(
                "Duplicate Application",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                null
        );
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

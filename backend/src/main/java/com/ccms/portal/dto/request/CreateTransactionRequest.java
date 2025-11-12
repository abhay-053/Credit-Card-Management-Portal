package com.ccms.portal.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotBlank(message = "Merchant name is required")
    @Size(max = 100, message = "Merchant name must not exceed 100 characters")
    private String merchantName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @NotNull(message = "BNPL flag is required")
    private Boolean isBnpl;

    @Size(max = 20, message = "Card type must not exceed 20 characters")
    private String cardType;

    @Size(max = 4, message = "Last four digits must not exceed 4 characters")
    private String lastFour;

    @Min(value = 1, message = "Tenure must be at least 1 month")
    @Max(value = 24, message = "Tenure must not exceed 24 months")
    private Integer tenureMonths;
}

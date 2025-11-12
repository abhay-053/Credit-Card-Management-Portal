package com.ccms.portal.entity;

import com.ccms.portal.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "card_id", nullable = false)
  private CreditCardEntity card;

  @NotBlank(message = "Merchant name is required")
  @Size(max = 100, message = "Merchant name must not exceed 100 characters")
  @Column(name = "merchant_name", nullable = false, length = 100)
  private String merchantName;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @NotNull(message = "Transaction date is required")
  @Column(name = "transaction_date", nullable = false)
  private LocalDate transactionDate;

  @NotBlank(message = "Category is required")
  @Size(max = 50, message = "Category must not exceed 50 characters")
  @Column(name = "category", nullable = false, length = 50)
  private String category;

  @NotNull(message = "BNPL flag is required")
  @Column(name = "is_bnpl", nullable = false)
  private Boolean isBnpl = false;

  @Size(max = 20, message = "Card type must not exceed 20 characters")
  @Column(name = "card_type", length = 20)
  private String cardType;

  @Size(max = 4, message = "Last four digits must not exceed 4 characters")
  @Column(name = "last_four", length = 4)
  private String lastFour;

  @Min(value = 1, message = "Tenure must be at least 1 month")
  @Max(value = 24, message = "Tenure must not exceed 24 months")
  @Column(name = "tenure_months")
  private Integer tenureMonths;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 30)
  private TransactionStatus status = TransactionStatus.COMPLETED;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (transactionDate == null) {
      transactionDate = LocalDate.now();
    }
  }

}

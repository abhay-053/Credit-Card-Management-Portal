package com.ccms.portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_card_statement")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardStatement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "card_id", nullable = false)
  private Long cardId;

  @Column(name = "statement_date", nullable = false)
  private LocalDate statementDate;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "statement_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal statementAmount;

  @Column(name = "paid_amount", precision = 12, scale = 2)
  @Builder.Default
  private BigDecimal paidAmount = BigDecimal.ZERO;

  @Column(name = "status")
  @Builder.Default
  private String status = "PENDING";

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Computed field for amount due
  public BigDecimal getAmountDue() {
    return statementAmount.subtract(paidAmount);
  }

  // Helper method to check if statement is paid
  public boolean isPaid() {
    return "PAID".equals(status) || paidAmount.compareTo(statementAmount) >= 0;
  }

  // Helper method to check if statement is overdue
  public boolean isOverdue() {
    return "OVERDUE".equals(status) || (LocalDate.now().isAfter(dueDate) && !isPaid());
  }
}

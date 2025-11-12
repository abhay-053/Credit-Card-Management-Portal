package com.ccms.portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bnpl_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BnplPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_id", nullable = false)
  private Transaction transaction;

  @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "tenure_months", nullable = false)
  private Integer tenureMonths;

  @Column(name = "monthly_emi", nullable = false, precision = 12, scale = 2)
  private BigDecimal monthlyEmi;

  @Column(name = "next_due_date")
  private LocalDate nextDueDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private BnplStatus status = BnplStatus.ACTIVE;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public enum BnplStatus {
    ACTIVE, COMPLETED, DEFAULTED
  }
}

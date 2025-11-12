package com.ccms.portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_id", nullable = false)
  private Transaction transaction;

  @Column(name = "settlement_amount", precision = 12, scale = 2)
  private BigDecimal settlementAmount;

  @Column(name = "settlement_date")
  private LocalDate settlementDate;

  @Column(name = "fee_amount", precision = 12, scale = 2)
  private BigDecimal feeAmount;

  @Column(name = "net_amount", precision = 12, scale = 2)
  private BigDecimal netAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private SettlementStatus status = SettlementStatus.PENDING;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public enum SettlementStatus {
    PENDING, COMPLETED
  }
}

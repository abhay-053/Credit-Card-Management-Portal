package com.ccms.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementResponse {
  private Long id;
  private Long cardId;
  private LocalDate statementDate;
  private LocalDate dueDate;
  private BigDecimal statementAmount;
  private BigDecimal paidAmount;
  private BigDecimal amountDue;
  private String status;
  private boolean isPaid;
  private boolean isOverdue;
}

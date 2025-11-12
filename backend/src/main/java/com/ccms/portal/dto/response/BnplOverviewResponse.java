package com.ccms.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BnplOverviewResponse {
  private BigDecimal outstandingAmount;
  private BigDecimal totalPaidAmount;
  private BigDecimal totalAmount;
  private Double progressPercentage;
  private Integer activePlansCount;
  private List<BnplPlanResponse> activePlans;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BnplPlanResponse {
    private Long transactionId;
    private String merchantName;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private Double progressPercentage;
    private Integer totalInstallments;
    private Integer paidInstallments;
    private Integer remainingInstallments;
    private String status;
    private String startDate;
    private String nextDueDate;
    private BigDecimal monthlyEmi;
  }
}

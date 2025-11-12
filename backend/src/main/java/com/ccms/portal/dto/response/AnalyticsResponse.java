package com.ccms.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    private BigDecimal totalSpent;
    private Long transactionCount;
    private List<CategorySpending> categoryBreakdown;
    private List<MonthlySpending> monthlyTrends;
    private BigDecimal averageTransactionAmount;
    private Long bnplTransactionCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySpending {
        private String category;
        private BigDecimal amount;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlySpending {
        private Integer year;
        private Integer month;
        private BigDecimal amount;
        private String monthName;
    }
}

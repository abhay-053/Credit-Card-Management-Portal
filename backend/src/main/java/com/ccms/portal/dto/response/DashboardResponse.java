package com.ccms.portal.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class DashboardResponse {
    private String userName;
    private SummaryResponse summary;
    private List<TransactionResponse> transactions;
    private Instant lastUpdated;
}

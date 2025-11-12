package com.ccms.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {
    private int activeCards;
    private int totalCards;
    private Double totalLimit;
    private Double availableCredit;
    private Double outstanding;
}

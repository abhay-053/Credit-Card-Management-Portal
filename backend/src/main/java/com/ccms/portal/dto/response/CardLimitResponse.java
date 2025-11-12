package com.ccms.portal.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class CardLimitResponse {
    private Long cardId;
    private Double oldLimit;
    private Double newLimit;
    private Double availableLimit;
}

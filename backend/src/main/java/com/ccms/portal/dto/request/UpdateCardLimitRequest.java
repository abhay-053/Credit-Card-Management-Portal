package com.ccms.portal.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCardLimitRequest {
    @NotNull @DecimalMin(value = "1.00" , message = "Limit must be greater than 0")
    private Double newCreditLimit;
}

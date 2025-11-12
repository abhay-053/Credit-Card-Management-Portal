package com.ccms.portal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCardRequest {
    private Long cardTypeId;
    private Long userId;
    private Double creditLimit;
}

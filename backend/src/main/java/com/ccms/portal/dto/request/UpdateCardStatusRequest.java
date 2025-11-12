package com.ccms.portal.dto.request;

import com.ccms.portal.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCardStatusRequest {
    @NotNull(message = "Card status must not be null")
    private CardStatus cardStatus;
}

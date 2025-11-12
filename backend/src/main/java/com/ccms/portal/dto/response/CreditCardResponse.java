package com.ccms.portal.dto.response;

import com.ccms.portal.enums.CardStatus;
import com.ccms.portal.enums.NetworkType;
import lombok.Builder;
import lombok.Getter;
import java.sql.Date;

@Getter
@Builder
public class CreditCardResponse {
    private Long id;
    private String cardHolderName;
    private String cardNumber;
    private CardStatus cardStatus;
    private Double creditLimit;
    private Double availableLimit;
    private Date expiryDate;
    private CardTypeInfo cardType;

    @Getter
    @Builder
    public static class CardTypeInfo {
        private String name;
        private NetworkType networkType;
        private String description;
    }
}

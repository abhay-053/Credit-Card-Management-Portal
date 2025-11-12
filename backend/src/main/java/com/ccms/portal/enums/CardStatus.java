package com.ccms.portal.enums;

import lombok.Getter;

@Getter
public enum CardStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BLOCKED("BLOCKED");

    private final String cardStatus;

    private CardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }
}


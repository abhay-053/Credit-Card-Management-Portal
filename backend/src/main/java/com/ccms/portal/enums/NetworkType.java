package com.ccms.portal.enums;

import lombok.Getter;

@Getter
public enum NetworkType {
    AMEX("Amex"),
    VISA("VISA"),
    MASTERCARD("Mastercard");

    private final String networkType;

    private NetworkType(String networkType){
        this.networkType = networkType;
    }
}


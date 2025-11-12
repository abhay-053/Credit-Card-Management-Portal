package com.ccms.portal.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CardApplicationStatus {
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    PENDING("PENDING");

    private final String applicationStatus;
    CardApplicationStatus(String applicationStatus){
        this.applicationStatus = applicationStatus;
    }

}
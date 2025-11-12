package com.ccms.portal.enums;
public enum TokenVariable {
    USER_ROLE("role"),
    USER_ID("userId"),
    USER_EMAIL("email");

    private final String value;
    TokenVariable(String value) { this.value = value; }
    public String getValue() { return value; }
}

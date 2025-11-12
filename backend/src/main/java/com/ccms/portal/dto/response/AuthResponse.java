package com.ccms.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class AuthResponse {
    private String token;
    public String refreshToken;
    private UserResponse user;

    public AuthResponse(String token, String refreshToken,UserResponse user) {
        this.token = token;
        this.refreshToken=refreshToken;
        this.user = user;
    }
}

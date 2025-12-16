package com.spendy.backend.dto.auth;

public class AuthTokenDTO {
    private final String accessToken;

    public AuthTokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() { return accessToken; }
}
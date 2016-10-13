package com.tomtom.sosnicki.okienczak.security;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    private String username;
    private String token;
    private Instant expirationTime;

    public TokenResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public TokenResponse(String username, String token, Instant expirationTime) {
        this.username = username;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "username='" + username + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}

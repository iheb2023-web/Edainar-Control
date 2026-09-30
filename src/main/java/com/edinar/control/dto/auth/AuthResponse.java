package com.edinar.control.dto.auth;

import java.util.UUID;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn; // secondes
    private UUID userId;
    private String email;
    private String displayName;

    public AuthResponse() {}

    public AuthResponse(String accessToken, long expiresIn, UUID userId, String email, String displayName) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

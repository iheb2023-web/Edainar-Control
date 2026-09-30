package com.edinar.control.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties.
 * Values are loaded from application.yml under app.jwt.*
 * and can be overridden via environment variables:
 *   JWT_SECRET, JWT_EXPIRATION_MS
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret = "edinar-control-default-secret-change-in-production-at-least-32-chars";
    private long expirationMs = 86400000L; // 24 heures

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public long getExpirationMs() { return expirationMs; }
    public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
}

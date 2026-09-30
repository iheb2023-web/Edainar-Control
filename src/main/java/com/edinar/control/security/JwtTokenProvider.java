package com.edinar.control.security;

import com.edinar.control.config.JwtProperties;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lightweight manual JWT implementation using HMAC-SHA256.
 * No external JWT library needed — uses Java's built-in crypto and Jackson 3.x.
 *
 * Token format: base64url(header).base64url(payload).base64url(HMAC-SHA256 signature)
 *
 * Payload claims:
 *   sub  — userId (UUID string)
 *   email — user's email
 *   iat  — issued at (epoch seconds)
 *   exp  — expiry (epoch seconds)
 */
@Component
public class JwtTokenProvider {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HEADER_ENCODED = Base64.getUrlEncoder().withoutPadding()
        .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = new ObjectMapper();
    }

    // =========================================================================
    // Token generation
    // =========================================================================

    public String generateToken(UUID userId, String email) {
        long nowSeconds = System.currentTimeMillis() / 1000;
        long expSeconds = nowSeconds + (jwtProperties.getExpirationMs() / 1000);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", userId.toString());
        payload.put("email", email);
        payload.put("iat", nowSeconds);
        payload.put("exp", expSeconds);

        String encodedPayload = encodePayload(payload);
        String signingInput = HEADER_ENCODED + "." + encodedPayload;
        String signature = sign(signingInput);

        return signingInput + "." + signature;
    }

    // =========================================================================
    // Token validation
    // =========================================================================

    /**
     * Returns true if the token has a valid signature AND is not expired.
     */
    public boolean validateToken(String token) {
        try {
            String[] parts = split(token);
            if (parts == null) return false;

            // 1. Verify signature
            String signingInput = parts[0] + "." + parts[1];
            String expectedSig = sign(signingInput);
            if (!constantTimeEquals(expectedSig, parts[2])) return false;

            // 2. Check expiry
            Map<?, ?> payload = decodePayload(parts[1]);
            long exp = toLong(payload.get("exp"));
            return System.currentTimeMillis() / 1000 < exp;

        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // Claim extraction (only call after validateToken returns true)
    // =========================================================================

    public UUID extractUserId(String token) {
        try {
            Map<?, ?> payload = decodePayload(split(token)[1]);
            return UUID.fromString((String) payload.get("sub"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot extract userId from JWT", e);
        }
    }

    public String extractEmail(String token) {
        try {
            Map<?, ?> payload = decodePayload(split(token)[1]);
            return (String) payload.get("email");
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot extract email from JWT", e);
        }
    }

    // =========================================================================
    // Internal helpers
    // =========================================================================

    private String encodePayload(Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode JWT payload", e);
        }
    }

    private Map<?, ?> decodePayload(String encodedPayload) throws Exception {
        byte[] decoded = Base64.getUrlDecoder().decode(encodedPayload);
        return objectMapper.readValue(new String(decoded, StandardCharsets.UTF_8), Map.class);
    }

    private String sign(String input) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(key);
            byte[] hmac = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmac);
        } catch (Exception e) {
            throw new RuntimeException("JWT signing failed", e);
        }
    }

    private String[] split(String token) {
        if (token == null) return null;
        String[] parts = token.split("\\.");
        return parts.length == 3 ? parts : null;
    }

    /** Constant-time string comparison to prevent timing attacks. */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private long toLong(Object value) {
        if (value instanceof Number n) return n.longValue();
        throw new IllegalArgumentException("Expected Number, got: " + value);
    }
}

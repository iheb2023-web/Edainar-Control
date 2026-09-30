package com.edinar.control.service;

import com.edinar.control.domain.User;
import com.edinar.control.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CurrentUserService {

    private final UserMapper userMapper;

    public CurrentUserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Returns the authenticated user's UUID from the JWT SecurityContext.
     *
     * The JWT filter sets the principal as a UUID (userId).
     * Falls back to DB lookup for unauthenticated dev/test contexts.
     */
    @Transactional(rollbackFor = Exception.class)
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UUID userId) {
            return userId;
        }

        // Fallback: first user in DB (dev/test without JWT)
        return userMapper.findFirst()
            .map(User::getId)
            .orElseGet(this::createDefaultUser);
    }

    private UUID createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setId(UUID.randomUUID());
        defaultUser.setEmail("owner@edinar.local");
        defaultUser.setPasswordHash(null);
        defaultUser.setDisplayName("Propriétaire e-DINAR");
        defaultUser.setStatus("ACTIVE");
        defaultUser.setTimezone("Africa/Tunis");
        defaultUser.setCreatedAt(OffsetDateTime.now());
        defaultUser.setUpdatedAt(OffsetDateTime.now());
        userMapper.insert(defaultUser);
        return defaultUser.getId();
    }
}

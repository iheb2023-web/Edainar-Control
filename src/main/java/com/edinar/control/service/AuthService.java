package com.edinar.control.service;

import com.edinar.control.config.JwtProperties;
import com.edinar.control.domain.User;
import com.edinar.control.dto.auth.AuthResponse;
import com.edinar.control.dto.auth.ChangeEmailRequest;
import com.edinar.control.dto.auth.ChangePasswordRequest;
import com.edinar.control.dto.auth.LoginRequest;
import com.edinar.control.dto.auth.MeResponse;
import com.edinar.control.dto.auth.RegisterRequest;
import com.edinar.control.exception.BadRequestException;
import com.edinar.control.exception.ResourceNotFoundException;
import com.edinar.control.mapper.UserMapper;
import com.edinar.control.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public AuthService(
        UserMapper userMapper,
        JwtTokenProvider jwtTokenProvider,
        PasswordEncoder passwordEncoder,
        JwtProperties jwtProperties
    ) {
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    // =========================================================================
    // Register
    // =========================================================================

    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        if (userMapper.findByEmail(email).isPresent()) {
            throw new BadRequestException("Un compte existe déjà avec cet email : " + email);
        }

        OffsetDateTime now = OffsetDateTime.now();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setStatus("ACTIVE");
        user.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Africa/Tunis");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userMapper.insert(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        return buildResponse(token, user);
    }

    // =========================================================================
    // Login
    // =========================================================================

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userMapper.findFirst()
            .orElseThrow(() -> new BadRequestException("Aucun compte n'est enregistré"));

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BadRequestException("Votre compte est désactivé");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Email ou mot de passe incorrect");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        return buildResponse(token, user);
    }

    // =========================================================================
    // Current user profile
    // =========================================================================

    @Transactional(readOnly = true)
    public MeResponse getProfile(UUID userId) {
        User user = userMapper.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        MeResponse r = new MeResponse();
        r.setId(user.getId());
        r.setEmail(user.getEmail());
        r.setDisplayName(user.getDisplayName());
        r.setStatus(user.getStatus());
        r.setTimezone(user.getTimezone());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userMapper.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Le mot de passe actuel est incorrect");
        }

        userMapper.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()), OffsetDateTime.now());
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeEmail(UUID userId, ChangeEmailRequest request) {
        User user = userMapper.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        String email = request.getEmail().toLowerCase().trim();

        if (!email.equalsIgnoreCase(user.getEmail()) && userMapper.findByEmail(email).isPresent()) {
            throw new BadRequestException("Un compte existe déjà avec cet email : " + email);
        }

        userMapper.updateEmail(userId, email, OffsetDateTime.now());
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private AuthResponse buildResponse(String token, User user) {
        long expiresInSeconds = jwtProperties.getExpirationMs() / 1000;
        return new AuthResponse(token, expiresInSeconds, user.getId(), user.getEmail(), user.getDisplayName());
    }
}

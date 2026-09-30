package com.edinar.control.controller;

import com.edinar.control.dto.auth.AuthResponse;
import com.edinar.control.dto.auth.ChangeEmailRequest;
import com.edinar.control.dto.auth.ChangePasswordRequest;
import com.edinar.control.dto.auth.LoginRequest;
import com.edinar.control.dto.auth.MeResponse;
import com.edinar.control.dto.auth.RegisterRequest;
import com.edinar.control.service.AuthService;
import com.edinar.control.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    /**
     * POST /api/auth/register
     * Crée un nouveau compte utilisateur et retourne un JWT.
     */
    @PostMapping("/api/auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * POST /api/auth/login
     * Authentifie un utilisateur existant et retourne un JWT.
     */
    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * GET /api/me
     * Retourne le profil de l'utilisateur actuellement authentifié.
     */
    @GetMapping("/api/me")
    public ResponseEntity<MeResponse> me() {
        return ResponseEntity.ok(authService.getProfile(currentUserService.getCurrentUserId()));
    }

    @PutMapping("/api/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(currentUserService.getCurrentUserId(), request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/me/email")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        authService.changeEmail(currentUserService.getCurrentUserId(), request);
        return ResponseEntity.noContent().build();
    }
}

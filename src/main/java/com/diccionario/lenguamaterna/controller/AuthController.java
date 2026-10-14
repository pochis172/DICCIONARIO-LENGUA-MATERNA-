package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.AuthUtil;
import com.diccionario.lenguamaterna.dto.AuthDtos.*;
import com.diccionario.lenguamaterna.service.AuthService;
import com.diccionario.lenguamaterna.service.JwtService;
import com.diccionario.lenguamaterna.service.JwtService.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService tokens;
    public AuthController(AuthService authService, JwtService tokens) { this.authService = authService; this.tokens = tokens; }
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) { return tokens.issue(authService.registrar(request)); }
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return tokens.issue(authService.login(request)); }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UsuarioResponse me(Authentication authentication) { return authService.me(AuthUtil.requireUserId(authentication)); }
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public Map<String, String> logout(Authentication authentication) {
        tokens.revoke(((JwtAuthenticationToken) authentication).getToken());
        return Map.of("mensaje", "Sesión cerrada.");
    }
}

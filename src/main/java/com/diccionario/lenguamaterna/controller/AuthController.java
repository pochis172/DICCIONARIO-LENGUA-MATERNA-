package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.AuthDtos.LoginRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.RegisterRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UsuarioResponse register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        return authService.registrar(request, session);
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        return authService.login(request, session);
    }

    @GetMapping("/me")
    public UsuarioResponse me(HttpSession session) {
        return authService.me(SessionUtil.requireUserId(session));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada."));
    }
}

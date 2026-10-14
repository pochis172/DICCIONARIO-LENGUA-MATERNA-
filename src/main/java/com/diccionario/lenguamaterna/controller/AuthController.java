package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.AuthDtos.AuthResponse;
import com.diccionario.lenguamaterna.dto.AuthDtos.LoginRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.RegisterRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;

import com.diccionario.lenguamaterna.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }


    // REGISTRO PÚBLICO
    // Devuelve usuario + token JWT
    @PostMapping("/register")
    public AuthResponse register(
            @Valid
            @RequestBody RegisterRequest request
    ) {

        return authService.registrar(request);
    }


    // INICIO DE SESIÓN
    // Devuelve usuario + token JWT
    @PostMapping("/login")
    public AuthResponse login(
            @Valid
            @RequestBody LoginRequest request
    ) {

        return authService.login(request);
    }


    // USUARIO ACTUAL
    // El correo se obtiene del JWT validado por Spring Security
    @GetMapping("/me")
    public UsuarioResponse me(
            Authentication authentication
    ) {

        return authService.me(
                authentication.getName()
        );
    }


    // LOGOUT EN JWT
    // Como JWT es stateless, el cliente debe eliminar el token.
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Sesión cerrada. Elimina el token JWT del cliente."
                )
        );
    }
}
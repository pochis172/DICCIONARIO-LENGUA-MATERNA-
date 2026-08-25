package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}

    public record LoginRequest(
        @NotBlank @Email String correo,
        @NotBlank String contrasena
    ) {}

    public record RegisterRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 120) String correo,
        @NotBlank @Size(min = 6, max = 100) String contrasena,
        boolean aceptaTerminos
    ) {}

    public record UsuarioResponse(Long id, String nombre, String correo, String rol) {}
}

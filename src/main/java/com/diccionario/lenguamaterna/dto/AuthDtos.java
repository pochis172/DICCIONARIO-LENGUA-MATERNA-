package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    // DATOS PARA INICIAR SESIÓN
    public record LoginRequest(

            @NotBlank
            @Email
            String correo,

            @NotBlank
            String contrasena

    ) {
    }


    // DATOS PARA REGISTRARSE
    public record RegisterRequest(

            @NotBlank
            @Size(max = 100)
            String nombre,

            @NotBlank
            @Email
            @Size(max = 120)
            String correo,

            @NotBlank
            @Size(min = 6, max = 100)
            String contrasena,

            boolean aceptaTerminos

    ) {
    }


    // RESPUESTA NORMAL DE USUARIO
    public record UsuarioResponse(

            Long id,
            String nombre,
            String correo,
            String rol

    ) {
    }


    // RESPUESTA DEL LOGIN O REGISTRO
    // Incluye el token JWT
    public record AuthResponse(

            Long id,
            String nombre,
            String correo,
            String rol,
            String token

    ) {
    }
}
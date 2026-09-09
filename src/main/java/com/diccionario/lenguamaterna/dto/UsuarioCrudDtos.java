package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UsuarioCrudDtos {

    private UsuarioCrudDtos() {
    }

    public record CrearUsuarioRequest(

            @NotBlank(message = "El nombre es obligatorio")
            @Size(
                    min = 3,
                    max = 100,
                    message = "El nombre debe tener entre 3 y 100 caracteres"
            )
            String nombre,

            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo debe tener un formato válido")
            @Size(
                    max = 120,
                    message = "El correo no puede superar los 120 caracteres"
            )
            String correo,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(
                    min = 6,
                    max = 100,
                    message = "La contraseña debe tener mínimo 6 caracteres"
            )
            String contrasena
    ) {
    }

    public record ActualizarUsuarioRequest(

            @NotBlank(message = "El nombre es obligatorio")
            @Size(
                    min = 3,
                    max = 100,
                    message = "El nombre debe tener entre 3 y 100 caracteres"
            )
            String nombre,

            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo debe tener un formato válido")
            @Size(
                    max = 120,
                    message = "El correo no puede superar los 120 caracteres"
            )
            String correo
    ) {
    }

    public record UsuarioResponse(
            Long id,
            String nombre,
            String correo,
            String rol
    ) {
    }
}
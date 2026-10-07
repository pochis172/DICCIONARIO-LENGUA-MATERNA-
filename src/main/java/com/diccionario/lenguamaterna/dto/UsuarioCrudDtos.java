package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UsuarioCrudDtos {

    private UsuarioCrudDtos() {
    }


    // =====================================================
    // CREAR USUARIO
    // El SUPER_USUARIO puede asignar el rol al crearlo
    // =====================================================
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
            String contrasena,

            @NotBlank(message = "El rol es obligatorio")
            String rol

    ) {
    }


    // =====================================================
    // ACTUALIZAR USUARIO
    // Todos los campos son opcionales
    //
    // Se puede actualizar:
    // - solo nombre
    // - solo correo
    // - solo rol
    // - dos campos
    // - los tres campos
    // =====================================================
    public record ActualizarUsuarioRequest(

            @Size(
                    min = 3,
                    max = 100,
                    message = "El nombre debe tener entre 3 y 100 caracteres"
            )
            String nombre,

            @Email(message = "El correo debe tener un formato válido")
            @Size(
                    max = 120,
                    message = "El correo no puede superar los 120 caracteres"
            )
            String correo,

            String rol

    ) {
    }


    // =====================================================
    // RESPUESTA DEL USUARIO
    // =====================================================
    public record UsuarioResponse(

            Long id,

            String nombre,

            String correo,

            String rol

    ) {
    }
}
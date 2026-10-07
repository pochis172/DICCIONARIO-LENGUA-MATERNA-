package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.NotBlank;

public record AsignarRolRequest(

        @NotBlank(message = "El rol es obligatorio")
        String rol

) {
}
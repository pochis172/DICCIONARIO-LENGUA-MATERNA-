package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class TraduccionDtos {

    private TraduccionDtos() {
    }

    public record TraduccionRequest(

            @NotNull(message = "La palabra es obligatoria") Long palabraId,

            @NotNull(message = "La lengua es obligatoria") Long lenguaId,

            @NotBlank(message = "La traducción es obligatoria") @Size(max = 150, message = "La traducción no puede superar 150 caracteres") String traduccion

    ) {
    }

    public record TraduccionResponse(
            Long id,
            Long palabraId,
            Long lenguaId,
            String traduccion) {
    }
}

package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class TraduccionDtos {

    private TraduccionDtos() {
    }

    public record TraduccionRequest(

            @NotNull(message = "La palabra es obligatoria")
            Long palabraId,

            @NotNull(message = "La lengua es obligatoria")
            Long lenguaId,

            @NotBlank(message = "La traducción es obligatoria")
            @Size(
                    min = 1,
                    max = 150,
                    message = "La traducción debe tener entre 1 y 150 caracteres"
            )
            String traduccion

    ) {
    }

    public record TraduccionResponse(

            Long id,

            Long palabraId,

            String palabraEspanol,

            Long lenguaId,

            String lenguaNombre,

            String traduccion

    ) {
    }
}

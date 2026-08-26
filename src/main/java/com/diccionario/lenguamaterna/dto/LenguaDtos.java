package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class LenguaDtos {

    private LenguaDtos() {}

    public record LenguaRequest(

            @NotBlank(message = "El nombre de la lengua es obligatorio")
            @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
            String nombre,

            @NotNull(message = "La región es obligatoria")
            Long regionId,

            @Size(max = 100, message = "La familia lingüística no puede superar 100 caracteres")
            String familiaLinguistica

    ) {}

    public record LenguaResponse(
            Long id,
            String nombre,
            Long regionId,
            String region,
            String familiaLinguistica
    ) {}
}
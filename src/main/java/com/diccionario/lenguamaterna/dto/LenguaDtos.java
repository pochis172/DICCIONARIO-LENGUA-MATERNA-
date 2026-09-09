package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class LenguaDtos {

    private LenguaDtos() {}

    // =========================
    // DTO PARA CREAR Y ACTUALIZAR
    // =========================
    public record LenguaRequest(

            @NotBlank(message = "El nombre de la lengua es obligatorio")
            @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
            String nombre,

            @NotNull(message = "La región es obligatoria")
            Long regionId,

            @Size(max = 100, message = "La familia lingüística no puede superar 100 caracteres")
            String familiaLinguistica

    ) {}


    // =========================
    // DTO PARA PATCH
    // =========================
    public record LenguaPatchRequest(

            @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
            String nombre,

            Long regionId,

            @Size(max = 100, message = "El nombre de la región no puede superar 100 caracteres")
            String regionNombre,

            @Size(max = 100, message = "La familia lingüística no puede superar 100 caracteres")
            String familiaLinguistica

    ) {}


    // =========================
    // RESPUESTA NORMAL
    // =========================
    public record LenguaResponse(
            Long id,
            String nombre,
            Long regionId,
            String region,
            String familiaLinguistica
    ) {}


    // =========================
    // RESPUESTA DEL RESUMEN
    // =========================
    public record LenguaResumenResponse(
            Long id,
            String nombre,
            String region,
            String familiaLinguistica,
            long cantidadTraducciones
    ) {}
}
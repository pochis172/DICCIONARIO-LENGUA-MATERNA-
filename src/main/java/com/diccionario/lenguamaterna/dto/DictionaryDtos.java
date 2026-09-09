package com.diccionario.lenguamaterna.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class DictionaryDtos {

    private DictionaryDtos() {}

    public record LenguaResponse(
            Long id,
            String nombre,
            String region,
            String familiaLinguistica
    ) {}

    public record TraduccionResponse(
            Long id,
            String traduccion,
            Long lenguaId,
            String lengua
    ) {}

    public record AudioResponse(
            Long id,
            String urlAudio,
            String tipo
    ) {}

    public record PalabraResponse(
            Long id,
            String espanol,
            String categoria,
            String significado,
            LenguaResponse lengua,
            List<TraduccionResponse> traducciones,
            List<String> ejemplos,
            List<AudioResponse> audios,
            boolean favorito
    ) {}

    public record TraduccionInput(
            @NotBlank @Size(max = 150)
            String traduccion,

            Long lenguaId
    ) {}

    public record AudioInput(
            @NotBlank @Size(max = 255)
            String urlAudio,

            @NotBlank @Size(max = 50)
            String tipo
    ) {}

    public record PalabraRequest(
            @NotBlank @Size(max = 120)
            String espanol,

            @NotBlank @Size(max = 80)
            String categoria,

            @NotBlank @Size(max = 500)
            String significado,

            @NotNull
            Long lenguaId,

            @Valid
            List<TraduccionInput> traducciones,

            List<@NotBlank @Size(max = 255) String> ejemplos,

            @Valid
            List<AudioInput> audios
    ) {}

    public record ActualizarPalabraRequest(
            @Size(max = 120)
            String espanol,

            @Size(max = 80)
            String categoria,

            @Size(max = 500)
            String significado
    ) {}
}
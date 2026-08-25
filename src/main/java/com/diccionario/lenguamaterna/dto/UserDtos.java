package com.diccionario.lenguamaterna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public final class UserDtos {
    private UserDtos() {}

    public record PerfilRequest(@NotBlank @Size(max = 100) String nombre) {}
    public record PasswordRequest(@NotBlank @Size(min = 6, max = 100) String actual, @NotBlank @Size(min = 6, max = 100) String nueva) {}

    public record HistorialResponse(Long id, Long palabraId, String palabra, String traduccion, LocalDateTime fecha) {}
    public record FavoritoResponse(Long id, DictionaryDtos.PalabraResponse palabra) {}

    public record SugerenciaRequest(
        @NotBlank @Size(max = 100) String palabraSugerida,
        @Size(max = 150) String posibleTraduccion,
        Long lenguaId,
        @Size(max = 255) String descripcion
    ) {}

    public record SugerenciaResponse(
        Long id,
        Long usuarioId,
        String usuario,
        String palabraSugerida,
        String posibleTraduccion,
        Long lenguaId,
        String lengua,
        String descripcion,
        String estado,
        LocalDateTime fechaCreacion
    ) {}

    public record EstadoSugerenciaRequest(@NotBlank String estado) {}
}

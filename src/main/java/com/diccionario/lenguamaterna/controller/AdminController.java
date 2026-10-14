package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.ActualizarPalabraRequest;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraRequest;
import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.dto.UserDtos.EstadoSugerenciaRequest;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaResponse;
import com.diccionario.lenguamaterna.service.AdminService;

import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Set<String> AUDIO_EXTENSIONS =
            Set.of(
                    "mp3",
                    "wav",
                    "ogg",
                    "m4a"
            );

    private final AdminService adminService;

    public AdminController(
            AdminService adminService
    ) {
        this.adminService = adminService;
    }


    // =====================================================
    // LISTAR PALABRAS
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/palabras")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<List<PalabraResponse>> palabras(
            @RequestParam(required = false) String q,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                adminService.listarPalabras(q)
        );
    }


    // =====================================================
    // CREAR PALABRA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PostMapping("/palabras")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<PalabraResponse> crearPalabra(
            @Valid
            @RequestBody PalabraRequest request,
            Authentication authentication
    ) {

        PalabraResponse creada =
                adminService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }


    // =====================================================
    // ACTUALIZAR PALABRA COMPLETAMENTE
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PutMapping("/palabras/{id}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<PalabraResponse> editarPalabra(
            @PathVariable Long id,
            @Valid
            @RequestBody PalabraRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                adminService.editar(
                        id,
                        request
                )
        );
    }


    // =====================================================
    // ACTUALIZAR PALABRA PARCIALMENTE
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PatchMapping("/palabras/{id}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<PalabraResponse> actualizarDatosPalabra(
            @PathVariable Long id,
            @Valid
            @RequestBody ActualizarPalabraRequest request,
            Authentication authentication
    ) {

        PalabraResponse actualizada =
                adminService.actualizarDatos(
                        id,
                        request
                );

        return ResponseEntity.ok(
                actualizada
        );
    }


    // =====================================================
    // ELIMINAR PALABRA
    // SOLO ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @DeleteMapping("/palabras/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<Void> eliminarPalabra(
            @PathVariable Long id,
            Authentication authentication
    ) {

        adminService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }


    // =====================================================
    // LISTAR SUGERENCIAS
    // SOLO ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/sugerencias")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPER_USUARIO')")
    public List<SugerenciaResponse> sugerencias(
            Authentication authentication
    ) {

        return adminService.sugerencias();
    }


    // =====================================================
    // CAMBIAR ESTADO DE SUGERENCIA
    // SOLO ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PatchMapping("/sugerencias/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPER_USUARIO')")
    public SugerenciaResponse estado(
            @PathVariable Long id,
            @Valid
            @RequestBody EstadoSugerenciaRequest request,
            Authentication authentication
    ) {

        return adminService
                .cambiarEstadoSugerencia(
                        id,
                        request.estado()
                );
    }


    // =====================================================
    // SUBIR AUDIO
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PostMapping("/audio/upload")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public Map<String, String> subirAudio(
            @RequestParam("file")
            MultipartFile file,
            Authentication authentication
    ) throws IOException {


        if (file.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selecciona un archivo de audio."
            );
        }


        String original =
                StringUtils.cleanPath(
                        file.getOriginalFilename() == null
                                ? "audio"
                                : file.getOriginalFilename()
                );


        String extension =
                original.contains(".")
                        ? original
                        .substring(
                                original.lastIndexOf('.') + 1
                        )
                        .toLowerCase()
                        : "";


        if (!AUDIO_EXTENSIONS.contains(extension)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato no permitido. Usa MP3, WAV, OGG o M4A."
            );
        }


        Path directorio =
                Path.of(
                        "uploads",
                        "audio"
                );


        Files.createDirectories(
                directorio
        );


        String nombre =
                UUID.randomUUID()
                        + "."
                        + extension;


        Files.copy(
                file.getInputStream(),
                directorio.resolve(nombre),
                StandardCopyOption.REPLACE_EXISTING
        );


        return Map.of(
                "url",
                "/uploads/audio/" + nombre
        );
    }
}
package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionRequest;
import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionResponse;
import com.diccionario.lenguamaterna.service.TraduccionService;

import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/admin/traducciones")
public class TraduccionController {

    private final TraduccionService traduccionService;

    public TraduccionController(
            TraduccionService traduccionService
    ) {
        this.traduccionService = traduccionService;
    }


    // =====================================================
    // LISTAR TODAS LAS TRADUCCIONES
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<TraduccionResponse> listar(
            Authentication authentication
    ) {

        return traduccionService.listar();
    }


    // =====================================================
    // BUSCAR POR PALABRA O TRADUCCIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<TraduccionResponse> buscar(
            @RequestParam String q,
            Authentication authentication
    ) {

        return traduccionService.buscar(q);
    }


    // =====================================================
    // BUSCAR TRADUCCIÓN POR ID
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public TraduccionResponse buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return traduccionService.buscarPorId(id);
    }


    // =====================================================
    // LISTAR TRADUCCIONES POR PALABRA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/palabra/{palabraId}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<TraduccionResponse> listarPorPalabra(
            @PathVariable Long palabraId,
            Authentication authentication
    ) {

        return traduccionService.listarPorPalabra(
                palabraId
        );
    }


    // =====================================================
    // LISTAR TRADUCCIONES POR LENGUA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/lengua/{lenguaId}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<TraduccionResponse> listarPorLengua(
            @PathVariable Long lenguaId,
            Authentication authentication
    ) {

        return traduccionService.listarPorLengua(
                lenguaId
        );
    }


    // =====================================================
    // CREAR TRADUCCIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<TraduccionResponse> crear(
            @Valid
            @RequestBody TraduccionRequest request,
            Authentication authentication
    ) {

        TraduccionResponse creada =
                traduccionService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }


    // =====================================================
    // ACTUALIZAR TRADUCCIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public TraduccionResponse actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody TraduccionRequest request,
            Authentication authentication
    ) {

        return traduccionService.actualizar(
                id,
                request
        );
    }


    // =====================================================
    // ELIMINAR TRADUCCIÓN
    // SOLO ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication
    ) {

        traduccionService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionRequest;
import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionResponse;
import com.diccionario.lenguamaterna.service.TraduccionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

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


    // LISTAR TODAS LAS TRADUCCIONES
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping
    public List<TraduccionResponse> listar() {

        return traduccionService.listar();
    }


    // BUSCAR POR PALABRA O TRADUCCION
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/buscar")
    public List<TraduccionResponse> buscar(
            @RequestParam String q
    ) {

        return traduccionService.buscar(q);
    }


    // BUSCAR TRADUCCION POR ID
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/{id}")
    public TraduccionResponse buscarPorId(
            @PathVariable Long id
    ) {

        return traduccionService.buscarPorId(id);
    }


    // LISTAR TRADUCCIONES POR PALABRA
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/palabra/{palabraId}")
    public List<TraduccionResponse> listarPorPalabra(
            @PathVariable Long palabraId
    ) {

        return traduccionService.listarPorPalabra(
                palabraId
        );
    }


    // LISTAR TRADUCCIONES POR LENGUA
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/lengua/{lenguaId}")
    public List<TraduccionResponse> listarPorLengua(
            @PathVariable Long lenguaId
    ) {

        return traduccionService.listarPorLengua(
                lenguaId
        );
    }


    // CREAR TRADUCCION
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @PostMapping
    public ResponseEntity<TraduccionResponse> crear(
            @Valid
            @RequestBody TraduccionRequest request
    ) {

        TraduccionResponse creada =
                traduccionService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }


    // ACTUALIZAR TRADUCCION
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @PutMapping("/{id}")
    public TraduccionResponse actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody TraduccionRequest request
    ) {

        return traduccionService.actualizar(
                id,
                request
        );
    }


    // ELIMINAR TRADUCCION
    // SOLO ADMINISTRADOR O SUPER_USUARIO
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPER_USUARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        traduccionService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
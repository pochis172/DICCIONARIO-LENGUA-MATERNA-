package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaPatchRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResumenResponse;
import com.diccionario.lenguamaterna.service.LenguaService;

import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lenguas")
public class LenguaController {

    private final LenguaService lenguaService;

    public LenguaController(LenguaService lenguaService) {
        this.lenguaService = lenguaService;
    }


    // =====================================================
    // LISTAR TODAS LAS LENGUAS
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<LenguaResponse> listar(
            Authentication authentication
    ) {

        return lenguaService.listar();
    }


    // =====================================================
    // BUSCAR POR NOMBRE
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<LenguaResponse> buscarPorNombre(
            @RequestParam String nombre,
            Authentication authentication
    ) {

        return lenguaService.buscarPorNombre(nombre);
    }


    // =====================================================
    // BUSCAR POR ID DE REGIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/region/{regionId}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<LenguaResponse> buscarPorRegion(
            @PathVariable Long regionId,
            Authentication authentication
    ) {

        return lenguaService.buscarPorRegion(regionId);
    }


    // =====================================================
    // BUSCAR POR NOMBRE DE REGIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/region")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<LenguaResponse> buscarPorNombreRegion(
            @RequestParam String nombre,
            Authentication authentication
    ) {

        return lenguaService.buscarPorNombreRegion(nombre);
    }


    // =====================================================
    // BUSCAR POR FAMILIA LINGÜÍSTICA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/familia")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public List<LenguaResponse> buscarPorFamilia(
            @RequestParam String nombre,
            Authentication authentication
    ) {

        return lenguaService.buscarPorFamilia(nombre);
    }


    // =====================================================
    // RESUMEN DE UNA LENGUA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/{id}/resumen")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public LenguaResumenResponse obtenerResumen(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return lenguaService.obtenerResumen(id);
    }


    // =====================================================
    // BUSCAR LENGUA POR ID
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public LenguaResponse buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return lenguaService.buscarPorId(id);
    }


    // =====================================================
    // CREAR LENGUA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<LenguaResponse> crear(
            @Valid
            @RequestBody LenguaRequest request,
            Authentication authentication
    ) {

        LenguaResponse creada =
                lenguaService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }


    // =====================================================
    // ACTUALIZAR COMPLETAMENTE
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public LenguaResponse actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody LenguaRequest request,
            Authentication authentication
    ) {

        return lenguaService.actualizar(
                id,
                request
        );
    }


    // =====================================================
    // ACTUALIZAR PARCIALMENTE
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR','ADMINISTRADOR','SUPER_USUARIO')")
    public LenguaResponse actualizarParcial(
            @PathVariable Long id,
            @Valid
            @RequestBody LenguaPatchRequest request,
            Authentication authentication
    ) {

        return lenguaService.actualizarParcial(
                id,
                request
        );
    }


    // =====================================================
    // ELIMINAR LENGUA
    // SOLO ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPER_USUARIO')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication
    ) {

        lenguaService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
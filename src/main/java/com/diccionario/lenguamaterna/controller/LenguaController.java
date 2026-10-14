package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaPatchRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResumenResponse;
import com.diccionario.lenguamaterna.service.LenguaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lenguas")
public class LenguaController {

    private final LenguaService lenguaService;

    public LenguaController(
            LenguaService lenguaService
    ) {
        this.lenguaService = lenguaService;
    }


    // LISTAR TODAS LAS LENGUAS
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping
    public List<LenguaResponse> listar() {

        return lenguaService.listar();
    }


    // BUSCAR POR NOMBRE
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/buscar")
    public List<LenguaResponse> buscarPorNombre(
            @RequestParam String nombre
    ) {

        return lenguaService.buscarPorNombre(nombre);
    }


    // BUSCAR POR ID DE REGION
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/region/{regionId}")
    public List<LenguaResponse> buscarPorRegion(
            @PathVariable Long regionId
    ) {

        return lenguaService.buscarPorRegion(regionId);
    }


    // BUSCAR POR NOMBRE DE REGION
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/region")
    public List<LenguaResponse> buscarPorNombreRegion(
            @RequestParam String nombre
    ) {

        return lenguaService.buscarPorNombreRegion(nombre);
    }


    // BUSCAR POR FAMILIA LINGUISTICA
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/familia")
    public List<LenguaResponse> buscarPorFamilia(
            @RequestParam String nombre
    ) {

        return lenguaService.buscarPorFamilia(nombre);
    }


    // RESUMEN DE UNA LENGUA
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/{id}/resumen")
    public LenguaResumenResponse obtenerResumen(
            @PathVariable Long id
    ) {

        return lenguaService.obtenerResumen(id);
    }


    // BUSCAR LENGUA POR ID
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @GetMapping("/{id}")
    public LenguaResponse buscarPorId(
            @PathVariable Long id
    ) {

        return lenguaService.buscarPorId(id);
    }


    // CREAR LENGUA
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @PostMapping
    public ResponseEntity<LenguaResponse> crear(
            @Valid
            @RequestBody LenguaRequest request
    ) {

        LenguaResponse creada =
                lenguaService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }


    // ACTUALIZAR COMPLETAMENTE
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @PutMapping("/{id}")
    public LenguaResponse actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody LenguaRequest request
    ) {

        return lenguaService.actualizar(
                id,
                request
        );
    }


    // ACTUALIZAR PARCIALMENTE
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMINISTRADOR', 'SUPER_USUARIO')")
    @PatchMapping("/{id}")
    public LenguaResponse actualizarParcial(
            @PathVariable Long id,
            @Valid
            @RequestBody LenguaPatchRequest request
    ) {

        return lenguaService.actualizarParcial(
                id,
                request
        );
    }


    // ELIMINAR LENGUA
    // Solo ADMINISTRADOR o SUPER_USUARIO
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPER_USUARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        lenguaService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
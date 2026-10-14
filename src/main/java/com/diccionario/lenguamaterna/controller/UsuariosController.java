package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.AsignarRolRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.ActualizarUsuarioRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.CrearUsuarioRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.service.UsuarioCrudService;

import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuarioCrudService usuarioCrudService;

    public UsuariosController(
            UsuarioCrudService usuarioCrudService
    ) {
        this.usuarioCrudService = usuarioCrudService;
    }


    // =====================================================
    // CREAR USUARIO
    // SOLO SUPER_USUARIO
    // =====================================================

    @PostMapping
    @PreAuthorize("hasRole('SUPER_USUARIO')")
    public ResponseEntity<UsuarioResponse> crear(
            @Valid
            @RequestBody CrearUsuarioRequest request,
            Authentication authentication
    ) {

        UsuarioResponse usuarioCreado =
                usuarioCrudService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioCreado);
    }


    // =====================================================
    // LISTAR TODOS LOS USUARIOS
    // SOLO SUPER_USUARIO
    // =====================================================

    @GetMapping
    @PreAuthorize("hasRole('SUPER_USUARIO')")
    public ResponseEntity<List<UsuarioResponse>> listar(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                usuarioCrudService.listar()
        );
    }


    // =====================================================
    // BUSCAR USUARIO POR ID
    // SOLO SUPER_USUARIO
    // =====================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_USUARIO')")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                usuarioCrudService.buscarPorId(id)
        );
    }


    // =====================================================
    // ACTUALIZAR USUARIO
    // SOLO SUPER_USUARIO
    // =====================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_USUARIO')")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody ActualizarUsuarioRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                usuarioCrudService.actualizar(
                        id,
                        request
                )
        );
    }


    // =====================================================
    // ASIGNAR ROL A UN USUARIO
    // SOLO SUPER_USUARIO
    // =====================================================

    @PatchMapping("/{id}/rol")
    @PreAuthorize("hasRole('SUPER_USUARIO')")
    public ResponseEntity<UsuarioResponse> asignarRol(
            @PathVariable Long id,
            @Valid
            @RequestBody AsignarRolRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                usuarioCrudService.asignarRol(
                        id,
                        request
                )
        );
    }


    // =====================================================
    // ELIMINAR USUARIO
    // SOLO SUPER_USUARIO
    // =====================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_USUARIO')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication
    ) {

        usuarioCrudService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
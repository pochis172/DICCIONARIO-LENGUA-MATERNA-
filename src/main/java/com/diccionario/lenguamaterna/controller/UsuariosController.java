package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.ActualizarUsuarioRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.CrearUsuarioRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.service.UsuarioCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // CREATE
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid
            @RequestBody
            CrearUsuarioRequest request
    ) {

        UsuarioResponse usuarioCreado =
                usuarioCrudService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioCreado);
    }

    // READ - LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {

        return ResponseEntity.ok(
                usuarioCrudService.listar()
        );
    }

    // READ - BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                usuarioCrudService.buscarPorId(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody
            ActualizarUsuarioRequest request
    ) {

        return ResponseEntity.ok(
                usuarioCrudService.actualizar(
                        id,
                        request
                )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        usuarioCrudService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaRequest;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaResponse;

import com.diccionario.lenguamaterna.service.SuggestionService;
import com.diccionario.lenguamaterna.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sugerencias")
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final UserService userService;

    public SuggestionController(
            SuggestionService suggestionService,
            UserService userService
    ) {
        this.suggestionService = suggestionService;
        this.userService = userService;
    }


    // LISTAR MIS SUGERENCIAS
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mias")
    public List<SugerenciaResponse> mias(
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        return suggestionService.mias(
                usuarioId
        );
    }


    // CREAR SUGERENCIA
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public SugerenciaResponse crear(
            @Valid
            @RequestBody SugerenciaRequest request,
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        return suggestionService.crear(
                usuarioId,
                request
        );
    }


    // EDITAR MI SUGERENCIA
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public SugerenciaResponse editar(
            @PathVariable Long id,
            @Valid
            @RequestBody SugerenciaRequest request,
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        return suggestionService.editar(
                usuarioId,
                id,
                request
        );
    }


    // ELIMINAR MI SUGERENCIA
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        suggestionService.eliminar(
                usuarioId,
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}
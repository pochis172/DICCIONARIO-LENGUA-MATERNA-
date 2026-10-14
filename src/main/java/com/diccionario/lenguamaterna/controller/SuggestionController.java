package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.AuthUtil;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaRequest;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaResponse;
import com.diccionario.lenguamaterna.service.SuggestionService;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/sugerencias")
public class SuggestionController {
    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/mias")
    @PreAuthorize("isAuthenticated()")
    public List<SugerenciaResponse> mias(Authentication authentication) {
        return suggestionService.mias(AuthUtil.requireUserId(authentication));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public SugerenciaResponse crear(@Valid @RequestBody SugerenciaRequest request, Authentication authentication) {
        return suggestionService.crear(AuthUtil.requireUserId(authentication), request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public SugerenciaResponse editar(@PathVariable Long id, @Valid @RequestBody SugerenciaRequest request, Authentication authentication) {
        return suggestionService.editar(AuthUtil.requireUserId(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication authentication) {
        suggestionService.eliminar(AuthUtil.requireUserId(authentication), id);
        return ResponseEntity.noContent().build();
    }
}

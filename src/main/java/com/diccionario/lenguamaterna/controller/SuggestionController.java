package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaRequest;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaResponse;
import com.diccionario.lenguamaterna.service.SuggestionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sugerencias")
public class SuggestionController {
    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/mias")
    public List<SugerenciaResponse> mias(HttpSession session) {
        return suggestionService.mias(SessionUtil.requireUserId(session));
    }

    @PostMapping
    public SugerenciaResponse crear(@Valid @RequestBody SugerenciaRequest request, HttpSession session) {
        return suggestionService.crear(SessionUtil.requireUserId(session), request);
    }

    @PutMapping("/{id}")
    public SugerenciaResponse editar(@PathVariable Long id, @Valid @RequestBody SugerenciaRequest request, HttpSession session) {
        return suggestionService.editar(SessionUtil.requireUserId(session), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpSession session) {
        suggestionService.eliminar(SessionUtil.requireUserId(session), id);
        return ResponseEntity.noContent().build();
    }
}

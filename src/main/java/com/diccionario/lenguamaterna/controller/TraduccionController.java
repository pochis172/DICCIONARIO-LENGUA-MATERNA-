package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionRequest;
import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionResponse;
import com.diccionario.lenguamaterna.service.TraduccionService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/traducciones")
public class TraduccionController {

    private final TraduccionService traduccionService;

    public TraduccionController(TraduccionService traduccionService) {
        this.traduccionService = traduccionService;
    }

    @GetMapping
    public List<TraduccionResponse> listar(HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.listar();
    }

    @GetMapping("/{id}")
    public TraduccionResponse buscarPorId(
            @PathVariable Long id,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.buscarPorId(id);
    }

    @GetMapping("/palabra/{palabraId}")
    public List<TraduccionResponse> listarPorPalabra(
            @PathVariable Long palabraId,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.listarPorPalabra(palabraId);
    }

    @PostMapping
    public ResponseEntity<TraduccionResponse> crear(
            @Valid @RequestBody TraduccionRequest request,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        TraduccionResponse creada = traduccionService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }

    @PutMapping("/{id}")
    public TraduccionResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TraduccionRequest request,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        traduccionService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
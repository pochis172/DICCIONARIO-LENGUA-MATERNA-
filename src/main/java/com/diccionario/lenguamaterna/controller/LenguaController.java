package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
import com.diccionario.lenguamaterna.service.LenguaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lenguas")
public class LenguaController {

    private final LenguaService lenguaService;

    public LenguaController(LenguaService lenguaService) {
        this.lenguaService = lenguaService;
    }

    @GetMapping
    public List<LenguaResponse> listar(HttpSession session) {

        SessionUtil.requireAdmin(session);

        return lenguaService.listar();
    }

    @GetMapping("/{id}")
    public LenguaResponse buscarPorId(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<LenguaResponse> crear(
            @Valid @RequestBody LenguaRequest request,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        LenguaResponse creada = lenguaService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }

    @PutMapping("/{id}")
    public LenguaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LenguaRequest request,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        lenguaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
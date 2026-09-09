package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaPatchRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResumenResponse;
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

    // =========================
    // LISTAR TODAS LAS LENGUAS
    // =========================
    @GetMapping
    public List<LenguaResponse> listar(HttpSession session) {

        SessionUtil.requireAdmin(session);

        return lenguaService.listar();
    }

    // =========================
    // BUSCAR POR NOMBRE
    // =========================
    @GetMapping("/buscar")
    public List<LenguaResponse> buscarPorNombre(
            @RequestParam String nombre,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.buscarPorNombre(nombre);
    }

    // =========================
    // BUSCAR POR ID DE REGIÓN
    // =========================
    @GetMapping("/region/{regionId}")
    public List<LenguaResponse> buscarPorRegion(
            @PathVariable Long regionId,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.buscarPorRegion(regionId);
    }

    // =========================
    // BUSCAR POR NOMBRE REGIÓN
    // =========================
    @GetMapping("/region")
    public List<LenguaResponse> buscarPorNombreRegion(
            @RequestParam String nombre,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.buscarPorNombreRegion(nombre);
    }

    // =========================
    // BUSCAR POR FAMILIA
    // =========================
    @GetMapping("/familia")
    public List<LenguaResponse> buscarPorFamilia(
            @RequestParam String nombre,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.buscarPorFamilia(nombre);
    }

    // =========================
    // RESUMEN DE UNA LENGUA
    // =========================
    @GetMapping("/{id}/resumen")
    public LenguaResumenResponse obtenerResumen(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.obtenerResumen(id);
    }

    // =========================
    // BUSCAR LENGUA POR ID
    // =========================
    @GetMapping("/{id}")
    public LenguaResponse buscarPorId(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.buscarPorId(id);
    }

    // =========================
    // CREAR LENGUA
    // =========================
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

    // =========================
    // ACTUALIZAR COMPLETAMENTE
    // =========================
    @PutMapping("/{id}")
    public LenguaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LenguaRequest request,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.actualizar(id, request);
    }

    // =========================
    // ACTUALIZAR PARCIALMENTE
    // =========================
    @PatchMapping("/{id}")
    public LenguaResponse actualizarParcial(
            @PathVariable Long id,
            @Valid @RequestBody LenguaPatchRequest request,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        return lenguaService.actualizarParcial(id, request);
    }

    // =========================
    // ELIMINAR LENGUA
    // =========================
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
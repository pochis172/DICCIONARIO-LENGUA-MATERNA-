package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaPatchRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
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


    // =====================================================
    // LISTAR TODAS LAS LENGUAS
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping
    public List<LenguaResponse> listar(
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return lenguaService.listar();
    }


    // =====================================================
    // BUSCAR POR NOMBRE
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/buscar")
    public List<LenguaResponse> buscarPorNombre(
            @RequestParam String nombre,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return lenguaService.buscarPorNombre(nombre);
    }


    // =====================================================
    // BUSCAR POR ID DE REGIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/region/{regionId}")
    public List<LenguaResponse> buscarPorRegion(
            @PathVariable Long regionId,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return lenguaService.buscarPorRegion(regionId);
    }


    // =====================================================
    // BUSCAR POR NOMBRE DE REGIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/region")
    public List<LenguaResponse> buscarPorNombreRegion(
            @RequestParam String nombre,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return lenguaService.buscarPorNombreRegion(nombre);
    }


    // =====================================================
    // BUSCAR POR FAMILIA LINGÜÍSTICA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/familia")
    public List<LenguaResponse> buscarPorFamilia(
            @RequestParam String nombre,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return lenguaService.buscarPorFamilia(nombre);
    }


    // =====================================================
    // RESUMEN DE UNA LENGUA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/{id}/resumen")
    public LenguaResumenResponse obtenerResumen(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return lenguaService.obtenerResumen(id);
    }


    // =====================================================
    // BUSCAR LENGUA POR ID
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/{id}")
    public LenguaResponse buscarPorId(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return lenguaService.buscarPorId(id);
    }


    // =====================================================
    // CREAR LENGUA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PostMapping
    public ResponseEntity<LenguaResponse> crear(
            @Valid
            @RequestBody LenguaRequest request,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

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
    public LenguaResponse actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody LenguaRequest request,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

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
    public LenguaResponse actualizarParcial(
            @PathVariable Long id,
            @Valid
            @RequestBody LenguaPatchRequest request,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

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
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        lenguaService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
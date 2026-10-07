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

    public TraduccionController(
            TraduccionService traduccionService
    ) {
        this.traduccionService = traduccionService;
    }


    // =====================================================
    // LISTAR TODAS LAS TRADUCCIONES
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping
    public List<TraduccionResponse> listar(
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return traduccionService.listar();
    }


    // =====================================================
    // BUSCAR POR PALABRA O TRADUCCIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/buscar")
    public List<TraduccionResponse> buscar(
            @RequestParam String q,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return traduccionService.buscar(q);
    }


    // =====================================================
    // BUSCAR TRADUCCIÓN POR ID
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/{id}")
    public TraduccionResponse buscarPorId(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return traduccionService.buscarPorId(id);
    }


    // =====================================================
    // LISTAR TRADUCCIONES POR PALABRA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/palabra/{palabraId}")
    public List<TraduccionResponse> listarPorPalabra(
            @PathVariable Long palabraId,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return traduccionService.listarPorPalabra(
                palabraId
        );
    }


    // =====================================================
    // LISTAR TRADUCCIONES POR LENGUA
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @GetMapping("/lengua/{lenguaId}")
    public List<TraduccionResponse> listarPorLengua(
            @PathVariable Long lenguaId,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return traduccionService.listarPorLengua(
                lenguaId
        );
    }


    // =====================================================
    // CREAR TRADUCCIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PostMapping
    public ResponseEntity<TraduccionResponse> crear(
            @Valid
            @RequestBody TraduccionRequest request,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        TraduccionResponse creada =
                traduccionService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }


    // =====================================================
    // ACTUALIZAR TRADUCCIÓN
    // EDITOR, ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @PutMapping("/{id}")
    public TraduccionResponse actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody TraduccionRequest request,
            HttpSession session
    ) {

        SessionUtil.requireEditor(session);

        return traduccionService.actualizar(
                id,
                request
        );
    }


    // =====================================================
    // ELIMINAR TRADUCCIÓN
    // SOLO ADMINISTRADOR O SUPER_USUARIO
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            HttpSession session
    ) {

        SessionUtil.requireAdmin(session);

        traduccionService.eliminar(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
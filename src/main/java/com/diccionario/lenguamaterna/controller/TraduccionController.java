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
            TraduccionService traduccionService) {

        this.traduccionService = traduccionService;
    }

    // GET - LISTAR TODAS
    @GetMapping
    public List<TraduccionResponse> listar(
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.listar();
    }

    // NUEVO - BUSCAR POR PALABRA O TRADUCCIÓN
    @GetMapping("/buscar")
    public List<TraduccionResponse> buscar(
            @RequestParam String q,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.buscar(q);
    }

    // GET - BUSCAR POR ID
    @GetMapping("/{id}")
    public TraduccionResponse buscarPorId(
            @PathVariable Long id,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.buscarPorId(id);
    }

    // GET - FILTRAR POR PALABRA
    @GetMapping("/palabra/{palabraId}")
    public List<TraduccionResponse> listarPorPalabra(
            @PathVariable Long palabraId,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.listarPorPalabra(
                palabraId
        );
    }

    // NUEVO - FILTRAR POR LENGUA
    @GetMapping("/lengua/{lenguaId}")
    public List<TraduccionResponse> listarPorLengua(
            @PathVariable Long lenguaId,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.listarPorLengua(
                lenguaId
        );
    }

    // POST - CREAR
    @PostMapping
    public ResponseEntity<TraduccionResponse> crear(
            @Valid @RequestBody TraduccionRequest request,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        TraduccionResponse creada =
                traduccionService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }

    // PUT - ACTUALIZAR
    @PutMapping("/{id}")
    public TraduccionResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TraduccionRequest request,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        return traduccionService.actualizar(
                id,
                request
        );
    }

    // DELETE - ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            HttpSession session) {

        SessionUtil.requireAdmin(session);

        traduccionService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}

package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.dto.UserDtos.*;
import com.diccionario.lenguamaterna.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }


    // VER PERFIL DEL USUARIO AUTENTICADO
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/perfil")
    public UsuarioResponse obtenerPerfil(
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        return userService.obtenerPerfil(
                usuarioId
        );
    }


    // ACTUALIZAR PERFIL
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/perfil")
    public UsuarioResponse actualizarPerfil(
            @Valid
            @RequestBody PerfilRequest request,
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        return userService.actualizarPerfil(
                usuarioId,
                request
        );
    }


    // CAMBIAR CONTRASEÑA
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @Valid
            @RequestBody PasswordRequest request,
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        userService.cambiarPassword(
                usuarioId,
                request
        );

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Contraseña actualizada."
                )
        );
    }


    // LISTAR FAVORITOS
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/favoritos")
    public List<FavoritoResponse> favoritos(
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        return userService.favoritos(
                usuarioId
        );
    }


    // AGREGAR FAVORITO
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/favoritos/{palabraId}")
    public ResponseEntity<Map<String, String>> agregarFavorito(
            @PathVariable Long palabraId,
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        userService.agregarFavorito(
                usuarioId,
                palabraId
        );

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Palabra guardada en favoritos."
                )
        );
    }


    // QUITAR FAVORITO
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/favoritos/{palabraId}")
    public ResponseEntity<Void> quitarFavorito(
            @PathVariable Long palabraId,
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        userService.quitarFavorito(
                usuarioId,
                palabraId
        );

        return ResponseEntity
                .noContent()
                .build();
    }


    // VER HISTORIAL
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/historial")
    public List<HistorialResponse> historial(
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        return userService.historial(
                usuarioId
        );
    }


    // LIMPIAR HISTORIAL
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/historial")
    public ResponseEntity<Void> limpiarHistorial(
            Authentication authentication
    ) {

        Long usuarioId =
                userService.obtenerIdPorCorreo(
                        authentication.getName()
                );

        userService.limpiarHistorial(
                usuarioId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}
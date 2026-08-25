package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.SessionUtil;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.dto.UserDtos.*;
import com.diccionario.lenguamaterna.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/perfil")
public UsuarioResponse obtenerPerfil(HttpSession session) {

    Long usuarioId = SessionUtil.requireUserId(session);

    return userService.obtenerPerfil(usuarioId);
}
    @PutMapping("/perfil")
    public UsuarioResponse actualizarPerfil(@Valid @RequestBody PerfilRequest request, HttpSession session) {
        return userService.actualizarPerfil(SessionUtil.requireUserId(session), request);
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> cambiarPassword(@Valid @RequestBody PasswordRequest request, HttpSession session) {
        userService.cambiarPassword(SessionUtil.requireUserId(session), request);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada."));
    }

    @GetMapping("/favoritos")
    public List<FavoritoResponse> favoritos(HttpSession session) {
        return userService.favoritos(SessionUtil.requireUserId(session));
    }

    @PostMapping("/favoritos/{palabraId}")
    public ResponseEntity<Map<String, String>> agregarFavorito(@PathVariable Long palabraId, HttpSession session) {
        userService.agregarFavorito(SessionUtil.requireUserId(session), palabraId);
        return ResponseEntity.ok(Map.of("mensaje", "Palabra guardada en favoritos."));
    }

    @DeleteMapping("/favoritos/{palabraId}")
    public ResponseEntity<Void> quitarFavorito(@PathVariable Long palabraId, HttpSession session) {
        userService.quitarFavorito(SessionUtil.requireUserId(session), palabraId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial")
    public List<HistorialResponse> historial(HttpSession session) {
        return userService.historial(SessionUtil.requireUserId(session));
    }

    @DeleteMapping("/historial")
    public ResponseEntity<Void> limpiarHistorial(HttpSession session) {
        userService.limpiarHistorial(SessionUtil.requireUserId(session));
        return ResponseEntity.noContent().build();
    }
}

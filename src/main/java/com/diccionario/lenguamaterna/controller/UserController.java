package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.config.AuthUtil;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.dto.UserDtos.*;
import com.diccionario.lenguamaterna.service.UserService;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

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
    @PreAuthorize("isAuthenticated()")
public UsuarioResponse obtenerPerfil(Authentication authentication) {

    Long usuarioId = AuthUtil.requireUserId(authentication);

    return userService.obtenerPerfil(usuarioId);
}
    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public UsuarioResponse actualizarPerfil(@Valid @RequestBody PerfilRequest request, Authentication authentication) {
        return userService.actualizarPerfil(AuthUtil.requireUserId(authentication), request);
    }

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> cambiarPassword(@Valid @RequestBody PasswordRequest request, Authentication authentication) {
        userService.cambiarPassword(AuthUtil.requireUserId(authentication), request);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada."));
    }

    @GetMapping("/favoritos")
    @PreAuthorize("isAuthenticated()")
    public List<FavoritoResponse> favoritos(Authentication authentication) {
        return userService.favoritos(AuthUtil.requireUserId(authentication));
    }

    @PostMapping("/favoritos/{palabraId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> agregarFavorito(@PathVariable Long palabraId, Authentication authentication) {
        userService.agregarFavorito(AuthUtil.requireUserId(authentication), palabraId);
        return ResponseEntity.ok(Map.of("mensaje", "Palabra guardada en favoritos."));
    }

    @DeleteMapping("/favoritos/{palabraId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> quitarFavorito(@PathVariable Long palabraId, Authentication authentication) {
        userService.quitarFavorito(AuthUtil.requireUserId(authentication), palabraId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial")
    @PreAuthorize("isAuthenticated()")
    public List<HistorialResponse> historial(Authentication authentication) {
        return userService.historial(AuthUtil.requireUserId(authentication));
    }

    @DeleteMapping("/historial")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> limpiarHistorial(Authentication authentication) {
        userService.limpiarHistorial(AuthUtil.requireUserId(authentication));
        return ResponseEntity.noContent().build();
    }
}

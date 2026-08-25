package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.AuthDtos.LoginRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.RegisterRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.entity.Rol;
import com.diccionario.lenguamaterna.entity.Usuario;
import com.diccionario.lenguamaterna.repository.RolRepository;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrar(RegisterRequest request, HttpSession session) {
        if (!request.aceptaTerminos()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes aceptar los términos y condiciones.");
        }
        String correo = request.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        }

        Rol rolUsuario = rolRepository.findByNombreIgnoreCase("USUARIO")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No existe el rol USUARIO."));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        usuario.setRol(rolUsuario);
        usuarioRepository.save(usuario);
        guardarSesion(usuario, session);
        return toResponse(usuario);
    }

    public UsuarioResponse login(LoginRequest request, HttpSession session) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.correo().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(request.contrasena(), usuario.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }
        guardarSesion(usuario, session);
        return toResponse(usuario);
    }

    public UsuarioResponse me(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La sesión ya no es válida."));
        return toResponse(usuario);
    }

    private void guardarSesion(Usuario usuario, HttpSession session) {
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("rol", usuario.getRol().getNombre());
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol().getNombre());
    }
}

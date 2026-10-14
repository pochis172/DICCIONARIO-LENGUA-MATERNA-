package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.AuthDtos.AuthResponse;
import com.diccionario.lenguamaterna.dto.AuthDtos.LoginRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.RegisterRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;

import com.diccionario.lenguamaterna.entity.Rol;
import com.diccionario.lenguamaterna.entity.Usuario;

import com.diccionario.lenguamaterna.repository.RolRepository;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;

import com.diccionario.lenguamaterna.security.JwtService;

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
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // REGISTRO PUBLICO
    // Crea usuario con rol USUARIO y genera token JWT
    @Transactional
    public AuthResponse registrar(
            RegisterRequest request
    ) {

        if (!request.aceptaTerminos()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes aceptar los términos y condiciones."
            );
        }

        String correo = request.correo()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta con ese correo."
            );
        }

        Rol rolUsuario = rolRepository
                .findByNombreIgnoreCase("USUARIO")
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "No existe el rol USUARIO."
                        )
                );

        Usuario usuario = new Usuario();

        usuario.setNombre(
                request.nombre().trim()
        );

        usuario.setCorreo(
                correo
        );

        usuario.setContrasena(
                passwordEncoder.encode(
                        request.contrasena()
                )
        );

        usuario.setRol(
                rolUsuario
        );

        Usuario guardado =
                usuarioRepository.save(usuario);

        // Genera el token JWT
        String token =
                jwtService.generarToken(guardado);

        return toAuthResponse(
                guardado,
                token
        );
    }

    // INICIO DE SESION
    // Verifica correo y contraseña y genera JWT
    public AuthResponse login(
            LoginRequest request
    ) {

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(
                        request.correo().trim()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Correo o contraseña incorrectos."
                        )
                );

        // Verifica la contraseña cifrada
        if (!passwordEncoder.matches(
                request.contrasena(),
                usuario.getContrasena()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos."
            );
        }

        // Genera token JWT
        String token =
                jwtService.generarToken(usuario);

        return toAuthResponse(
                usuario,
                token
        );
    }

    // CONSULTAR USUARIO AUTENTICADO
    // El correo viene del JWT
    public UsuarioResponse me(
            String correo
    ) {

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(correo)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "El usuario autenticado ya no existe."
                        )
                );

        return toResponse(usuario);
    }

    // RESPUESTA NORMAL DEL USUARIO
    public UsuarioResponse toResponse(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }

    // RESPUESTA DE LOGIN/REGISTRO CON TOKEN
    private AuthResponse toAuthResponse(
            Usuario usuario,
            String token
    ) {

        return new AuthResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre(),
                token
        );
    }
}
package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.ActualizarUsuarioRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.CrearUsuarioRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.entity.Rol;
import com.diccionario.lenguamaterna.entity.Usuario;
import com.diccionario.lenguamaterna.repository.FavoritoRepository;
import com.diccionario.lenguamaterna.repository.HistorialRepository;
import com.diccionario.lenguamaterna.repository.RolRepository;
import com.diccionario.lenguamaterna.repository.SugerenciaRepository;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioCrudService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final FavoritoRepository favoritoRepository;
    private final HistorialRepository historialRepository;
    private final SugerenciaRepository sugerenciaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioCrudService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            FavoritoRepository favoritoRepository,
            HistorialRepository historialRepository,
            SugerenciaRepository sugerenciaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.favoritoRepository = favoritoRepository;
        this.historialRepository = historialRepository;
        this.sugerenciaRepository = sugerenciaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {

        String correo = request.correo()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese correo."
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

        return convertir(guardado);
    }

    // READ - LISTAR
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {

        return usuarioRepository
                .findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    // READ - BUSCAR POR ID
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario =
                buscarEntidad(id);

        return convertir(usuario);
    }

    // UPDATE
    @Transactional
    public UsuarioResponse actualizar(
            Long id,
            ActualizarUsuarioRequest request
    ) {

        Usuario usuario =
                buscarEntidad(id);

        String correoNuevo =
                request.correo()
                        .trim()
                        .toLowerCase();

        if (!usuario
                .getCorreo()
                .equalsIgnoreCase(correoNuevo)
                &&
                usuarioRepository
                        .existsByCorreoIgnoreCase(correoNuevo)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese correo."
            );
        }

        usuario.setNombre(
                request.nombre().trim()
        );

        usuario.setCorreo(
                correoNuevo
        );

        Usuario actualizado =
                usuarioRepository.save(usuario);

        return convertir(actualizado);
    }

    // DELETE
    @Transactional
    public void eliminar(Long id) {

        Usuario usuario =
                buscarEntidad(id);

        sugerenciaRepository
                .deleteByUsuarioId(id);

        historialRepository
                .deleteByUsuarioId(id);

        favoritoRepository
                .deleteByUsuarioId(id);

        usuarioRepository
                .delete(usuario);
    }

    private Usuario buscarEntidad(Long id) {

        return usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario " + id + " no encontrado."
                        )
                );
    }

    private UsuarioResponse convertir(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }
}
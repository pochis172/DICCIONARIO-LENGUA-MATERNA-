package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.AsignarRolRequest;
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


    // =====================================================
    // CREATE
    // SUPER_USUARIO CREA UN USUARIO CON SU ROL
    // =====================================================
    @Transactional
    public UsuarioResponse crear(
            CrearUsuarioRequest request
    ) {

        String correo = request.correo()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese correo."
            );
        }

        String nombreRol = request.rol()
                .trim()
                .toUpperCase();

        // No permitir crear otro SUPER_USUARIO
        if (nombreRol.equals("SUPER_USUARIO")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede crear otro SUPER_USUARIO desde esta operación."
            );
        }

        Rol rol = rolRepository
                .findByNombreIgnoreCase(nombreRol)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "El rol indicado no existe."
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
                rol
        );

        Usuario guardado =
                usuarioRepository.save(usuario);

        return convertir(guardado);
    }


    // =====================================================
    // READ - LISTAR TODOS
    // =====================================================
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {

        return usuarioRepository
                .findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }


    // =====================================================
    // READ - BUSCAR POR ID
    // =====================================================
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(
            Long id
    ) {

        Usuario usuario =
                buscarEntidad(id);

        return convertir(usuario);
    }


    // =====================================================
    // ACTUALIZAR USUARIO PARCIALMENTE
    //
    // Puede modificar:
    // - solo nombre
    // - solo correo
    // - solo rol
    // - dos campos
    // - los tres campos
    // =====================================================
    @Transactional
    public UsuarioResponse actualizar(
            Long id,
            ActualizarUsuarioRequest request
    ) {

        Usuario usuario =
                buscarEntidad(id);


        // Debe enviar mínimo un campo
        if (request.nombre() == null
                && request.correo() == null
                && request.rol() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes enviar al menos un campo para actualizar."
            );
        }


        // =================================================
        // ACTUALIZAR NOMBRE
        // =================================================
        if (request.nombre() != null) {

            String nombreNuevo =
                    request.nombre().trim();

            if (nombreNuevo.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El nombre no puede estar vacío."
                );
            }

            usuario.setNombre(
                    nombreNuevo
            );
        }


        // =================================================
        // ACTUALIZAR CORREO
        // =================================================
        if (request.correo() != null) {

            String correoNuevo =
                    request.correo()
                            .trim()
                            .toLowerCase();

            if (correoNuevo.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El correo no puede estar vacío."
                );
            }

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

            usuario.setCorreo(
                    correoNuevo
            );
        }


        // =================================================
        // ACTUALIZAR ROL
        // =================================================
        if (request.rol() != null) {

            String nombreRol =
                    request.rol()
                            .trim()
                            .toUpperCase();

            if (nombreRol.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El rol no puede estar vacío."
                );
            }

            // No permitir asignar SUPER_USUARIO
            if (nombreRol.equals("SUPER_USUARIO")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El rol SUPER_USUARIO no puede asignarse desde esta operación."
                );
            }

            Rol rol = rolRepository
                    .findByNombreIgnoreCase(nombreRol)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "El rol indicado no existe."
                            )
                    );

            usuario.setRol(
                    rol
            );
        }


        Usuario actualizado =
                usuarioRepository.save(usuario);

        return convertir(actualizado);
    }


    // =====================================================
    // ASIGNAR O CAMBIAR ROL
    // =====================================================
    @Transactional
    public UsuarioResponse asignarRol(
            Long id,
            AsignarRolRequest request
    ) {

        Usuario usuario =
                buscarEntidad(id);

        String nombreRol = request.rol()
                .trim()
                .toUpperCase();

        if (nombreRol.equals("SUPER_USUARIO")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El rol SUPER_USUARIO no puede asignarse desde esta operación."
            );
        }

        Rol rol = rolRepository
                .findByNombreIgnoreCase(nombreRol)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "El rol indicado no existe."
                        )
                );

        usuario.setRol(
                rol
        );

        Usuario actualizado =
                usuarioRepository.save(usuario);

        return convertir(actualizado);
    }


    // =====================================================
    // DELETE
    // =====================================================
    @Transactional
    public void eliminar(
            Long id
    ) {

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


    // =====================================================
    // BUSCAR ENTIDAD USUARIO
    // =====================================================
    private Usuario buscarEntidad(
            Long id
    ) {

        return usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario " + id + " no encontrado."
                        )
                );
    }


    // =====================================================
    // CONVERTIR A RESPONSE
    // =====================================================
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
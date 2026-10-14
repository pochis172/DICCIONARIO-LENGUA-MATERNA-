package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.UserDtos.FavoritoResponse;
import com.diccionario.lenguamaterna.dto.UserDtos.HistorialResponse;
import com.diccionario.lenguamaterna.dto.UserDtos.PerfilRequest;
import com.diccionario.lenguamaterna.dto.UserDtos.PasswordRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;

import com.diccionario.lenguamaterna.entity.Favorito;
import com.diccionario.lenguamaterna.entity.Palabra;
import com.diccionario.lenguamaterna.entity.Usuario;

import com.diccionario.lenguamaterna.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PalabraRepository palabraRepository;
    private final FavoritoRepository favoritoRepository;
    private final HistorialRepository historialRepository;
    private final TraduccionRepository traduccionRepository;
    private final DictionaryService dictionaryService;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UsuarioRepository usuarioRepository,
            PalabraRepository palabraRepository,
            FavoritoRepository favoritoRepository,
            HistorialRepository historialRepository,
            TraduccionRepository traduccionRepository,
            DictionaryService dictionaryService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.palabraRepository = palabraRepository;
        this.favoritoRepository = favoritoRepository;
        this.historialRepository = historialRepository;
        this.traduccionRepository = traduccionRepository;
        this.dictionaryService = dictionaryService;
        this.passwordEncoder = passwordEncoder;
    }


    // OBTENER ID DEL USUARIO AUTENTICADO
    // El correo viene del token JWT
    @Transactional(readOnly = true)
    public Long obtenerIdPorCorreo(
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

        return usuario.getId();
    }


    // OBTENER PERFIL
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfil(
            Long usuarioId
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado."
                ));

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }


    // ACTUALIZAR PERFIL
    @Transactional
    public UsuarioResponse actualizarPerfil(
            Long usuarioId,
            PerfilRequest request
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado."
                ));

        String nuevoNombre =
                request.nombre().trim();

        String nuevoCorreo =
                request.correo()
                        .trim()
                        .toLowerCase();

        if (!usuario.getCorreo()
                .equalsIgnoreCase(nuevoCorreo)) {

            boolean correoExiste =
                    usuarioRepository
                            .existsByCorreoIgnoreCase(
                                    nuevoCorreo
                            );

            if (correoExiste) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Ya existe una cuenta registrada con ese correo."
                );
            }
        }

        usuario.setNombre(
                nuevoNombre
        );

        usuario.setCorreo(
                nuevoCorreo
        );

        Usuario usuarioActualizado =
                usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuarioActualizado.getId(),
                usuarioActualizado.getNombre(),
                usuarioActualizado.getCorreo(),
                usuarioActualizado.getRol().getNombre()
        );
    }


    // CAMBIAR CONTRASEÑA
    @Transactional
    public void cambiarPassword(
            Long usuarioId,
            PasswordRequest request
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado."
                ));

        if (!passwordEncoder.matches(
                request.actual(),
                usuario.getContrasena()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña actual no es correcta."
            );
        }

        usuario.setContrasena(
                passwordEncoder.encode(
                        request.nueva()
                )
        );

        usuarioRepository.save(usuario);
    }


    // LISTAR FAVORITOS
    @Transactional(readOnly = true)
    public List<FavoritoResponse> favoritos(
            Long usuarioId
    ) {

        return favoritoRepository
                .findByUsuarioIdOrderByIdDesc(usuarioId)
                .stream()
                .map(f ->
                        new FavoritoResponse(
                                f.getId(),
                                dictionaryService.toResponse(
                                        f.getPalabra(),
                                        usuarioId
                                )
                        )
                )
                .toList();
    }


    // AGREGAR FAVORITO
    @Transactional
    public void agregarFavorito(
            Long usuarioId,
            Long palabraId
    ) {

        if (favoritoRepository
                .existsByUsuarioIdAndPalabraId(
                        usuarioId,
                        palabraId
                )) {

            return;
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado."
                ));

        Palabra palabra = palabraRepository.findById(palabraId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Palabra no encontrada."
                ));

        Favorito favorito =
                new Favorito();

        favorito.setUsuario(
                usuario
        );

        favorito.setPalabra(
                palabra
        );

        favoritoRepository.save(
                favorito
        );
    }


    // QUITAR FAVORITO
    @Transactional
    public void quitarFavorito(
            Long usuarioId,
            Long palabraId
    ) {

        favoritoRepository
                .findByUsuarioIdAndPalabraId(
                        usuarioId,
                        palabraId
                )
                .ifPresent(
                        favoritoRepository::delete
                );
    }


    // VER HISTORIAL
    @Transactional(readOnly = true)
    public List<HistorialResponse> historial(
            Long usuarioId
    ) {

        return historialRepository
                .findTop50ByUsuarioIdOrderByFechaDesc(usuarioId)
                .stream()
                .map(h -> {

                    String traduccion =
                            traduccionRepository
                                    .findByPalabraIdOrderByIdAsc(
                                            h.getPalabra().getId()
                                    )
                                    .stream()
                                    .findFirst()
                                    .map(t ->
                                            t.getTraduccion()
                                    )
                                    .orElse(
                                            "Sin traducción"
                                    );

                    return new HistorialResponse(
                            h.getId(),
                            h.getPalabra().getId(),
                            h.getPalabra().getEspanol(),
                            traduccion,
                            h.getFecha()
                    );
                })
                .toList();
    }


    // LIMPIAR HISTORIAL
    @Transactional
    public void limpiarHistorial(
            Long usuarioId
    ) {

        historialRepository
                .deleteByUsuarioId(
                        usuarioId
                );
    }
}
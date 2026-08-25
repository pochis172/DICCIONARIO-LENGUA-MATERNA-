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
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    public UserService(UsuarioRepository usuarioRepository, PalabraRepository palabraRepository,
                       FavoritoRepository favoritoRepository, HistorialRepository historialRepository,
                       TraduccionRepository traduccionRepository, DictionaryService dictionaryService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.palabraRepository = palabraRepository;
        this.favoritoRepository = favoritoRepository;
        this.historialRepository = historialRepository;
        this.traduccionRepository = traduccionRepository;
        this.dictionaryService = dictionaryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(Long usuarioId, PerfilRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        usuario.setNombre(request.nombre().trim());
        usuarioRepository.save(usuario);
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol().getNombre());
    }

    @Transactional
    public void cambiarPassword(Long usuarioId, PasswordRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        if (!passwordEncoder.matches(request.actual(), usuario.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta.");
        }
        usuario.setContrasena(passwordEncoder.encode(request.nueva()));
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<FavoritoResponse> favoritos(Long usuarioId) {
        return favoritoRepository.findByUsuarioIdOrderByIdDesc(usuarioId).stream()
                .map(f -> new FavoritoResponse(f.getId(), dictionaryService.toResponse(f.getPalabra(), usuarioId)))
                .toList();
    }

    @Transactional
    public void agregarFavorito(Long usuarioId, Long palabraId) {
        if (favoritoRepository.existsByUsuarioIdAndPalabraId(usuarioId, palabraId)) return;
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        Palabra palabra = palabraRepository.findById(palabraId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palabra no encontrada."));
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setPalabra(palabra);
        favoritoRepository.save(favorito);
    }

    @Transactional
    public void quitarFavorito(Long usuarioId, Long palabraId) {
        favoritoRepository.findByUsuarioIdAndPalabraId(usuarioId, palabraId).ifPresent(favoritoRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<HistorialResponse> historial(Long usuarioId) {
        return historialRepository.findTop50ByUsuarioIdOrderByFechaDesc(usuarioId).stream().map(h -> {
            String traduccion = traduccionRepository.findByPalabraIdOrderByIdAsc(h.getPalabra().getId()).stream()
                    .findFirst().map(t -> t.getTraduccion()).orElse("Sin traducción");
            return new HistorialResponse(h.getId(), h.getPalabra().getId(), h.getPalabra().getEspanol(), traduccion, h.getFecha());
        }).toList();
    }

    @Transactional
    public void limpiarHistorial(Long usuarioId) {
        historialRepository.deleteByUsuarioId(usuarioId);
    }
}

package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.*;
import com.diccionario.lenguamaterna.entity.*;
import com.diccionario.lenguamaterna.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DictionaryService {
    private final PalabraRepository palabraRepository;
    private final TraduccionRepository traduccionRepository;
    private final EjemploRepository ejemploRepository;
    private final AudioRepository audioRepository;
    private final LenguaRepository lenguaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialRepository historialRepository;
    private final FavoritoRepository favoritoRepository;

    public DictionaryService(PalabraRepository palabraRepository, TraduccionRepository traduccionRepository,
                             EjemploRepository ejemploRepository, AudioRepository audioRepository,
                             LenguaRepository lenguaRepository, UsuarioRepository usuarioRepository,
                             HistorialRepository historialRepository, FavoritoRepository favoritoRepository) {
        this.palabraRepository = palabraRepository;
        this.traduccionRepository = traduccionRepository;
        this.ejemploRepository = ejemploRepository;
        this.audioRepository = audioRepository;
        this.lenguaRepository = lenguaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialRepository = historialRepository;
        this.favoritoRepository = favoritoRepository;
    }

    @Transactional
    public List<PalabraResponse> buscar(String q, Long lenguaId, Long usuarioId) {
        String consulta = q == null ? "" : q.trim();
        if (consulta.isBlank()) return List.of();
        List<Palabra> resultados = palabraRepository.buscar(consulta, lenguaId);
        if (usuarioId != null && !resultados.isEmpty()) {
            registrarHistorial(usuarioId, resultados.get(0));
        }
        return resultados.stream().map(p -> toResponse(p, usuarioId)).toList();
    }

    @Transactional(readOnly = true)
    public List<PalabraResponse> listar(String q, String categoria, Long usuarioId) {
        List<Palabra> palabras;
        if (q != null && !q.isBlank()) {
            palabras = palabraRepository.findByEspanolContainingIgnoreCaseOrderByEspanolAsc(q.trim());
        } else if (categoria != null && !categoria.isBlank()) {
            palabras = palabraRepository.findByCategoriaIgnoreCaseOrderByEspanolAsc(categoria.trim());
        } else {
            palabras = palabraRepository.findTop12ByOrderByIdDesc();
        }
        return palabras.stream().map(p -> toResponse(p, usuarioId)).toList();
    }

    @Transactional(readOnly = true)
    public PalabraResponse obtener(Long id, Long usuarioId) {
        Palabra palabra = palabraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palabra no encontrada."));
        return toResponse(palabra, usuarioId);
    }

    @Transactional(readOnly = true)
    public List<String> categorias() {
        return palabraRepository.listarCategorias();
    }

    @Transactional(readOnly = true)
    public List<LenguaResponse> lenguas() {
        return lenguaRepository.findAll().stream()
                .map(l -> new LenguaResponse(l.getId(), l.getNombre(), l.getRegion().getNombre(), l.getFamiliaLinguistica()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PalabraResponse toResponse(Palabra palabra, Long usuarioId) {
        List<TraduccionResponse> traducciones = traduccionRepository.findByPalabraIdOrderByIdAsc(palabra.getId()).stream()
                .map(t -> new TraduccionResponse(t.getId(), t.getTraduccion(), t.getLengua().getId(), t.getLengua().getNombre()))
                .toList();
        List<String> ejemplos = ejemploRepository.findByPalabraIdOrderByIdAsc(palabra.getId()).stream()
                .map(Ejemplo::getEjemplo).toList();
        List<AudioResponse> audios = audioRepository.findByPalabraIdOrderByIdAsc(palabra.getId()).stream()
                .map(a -> new AudioResponse(a.getId(), a.getUrlAudio(), a.getTipo())).toList();
        boolean favorito = usuarioId != null && favoritoRepository.existsByUsuarioIdAndPalabraId(usuarioId, palabra.getId());
        Lengua l = palabra.getLengua();
        return new PalabraResponse(
                palabra.getId(), palabra.getEspanol(), palabra.getCategoria(), palabra.getSignificado(),
                new LenguaResponse(l.getId(), l.getNombre(), l.getRegion().getNombre(), l.getFamiliaLinguistica()),
                traducciones, ejemplos, audios, favorito
        );
    }

    private void registrarHistorial(Long usuarioId, Palabra palabra) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return;
        Historial historial = new Historial();
        historial.setUsuario(usuario);
        historial.setPalabra(palabra);
        historial.setFecha(LocalDateTime.now());
        historialRepository.save(historial);
    }
}

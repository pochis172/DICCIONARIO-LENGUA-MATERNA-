package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.*;
import com.diccionario.lenguamaterna.dto.UserDtos.SugerenciaResponse;
import com.diccionario.lenguamaterna.entity.*;
import com.diccionario.lenguamaterna.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class AdminService {
    private final PalabraRepository palabraRepository;
    private final LenguaRepository lenguaRepository;
    private final TraduccionRepository traduccionRepository;
    private final EjemploRepository ejemploRepository;
    private final AudioRepository audioRepository;
    private final FavoritoRepository favoritoRepository;
    private final HistorialRepository historialRepository;
    private final SugerenciaRepository sugerenciaRepository;
    private final DictionaryService dictionaryService;
    private final SuggestionService suggestionService;

    public AdminService(PalabraRepository palabraRepository, LenguaRepository lenguaRepository,
                        TraduccionRepository traduccionRepository, EjemploRepository ejemploRepository,
                        AudioRepository audioRepository, FavoritoRepository favoritoRepository,
                        HistorialRepository historialRepository, SugerenciaRepository sugerenciaRepository,
                        DictionaryService dictionaryService, SuggestionService suggestionService) {
        this.palabraRepository = palabraRepository;
        this.lenguaRepository = lenguaRepository;
        this.traduccionRepository = traduccionRepository;
        this.ejemploRepository = ejemploRepository;
        this.audioRepository = audioRepository;
        this.favoritoRepository = favoritoRepository;
        this.historialRepository = historialRepository;
        this.sugerenciaRepository = sugerenciaRepository;
        this.dictionaryService = dictionaryService;
        this.suggestionService = suggestionService;
    }

    @Transactional(readOnly = true)
    public List<PalabraResponse> listarPalabras(String q) {
        List<Palabra> palabras = (q == null || q.isBlank())
                ? palabraRepository.findAllByOrderByEspanolAsc()
                : palabraRepository.findByEspanolContainingIgnoreCaseOrderByEspanolAsc(q.trim());
        return palabras.stream().map(p -> dictionaryService.toResponse(p, null)).toList();
    }

    @Transactional
    public PalabraResponse crear(PalabraRequest request) {
        Palabra palabra = new Palabra();
        aplicarPalabra(palabra, request);
        palabraRepository.save(palabra);
        reemplazarDetalles(palabra, request);
        return dictionaryService.toResponse(palabra, null);
    }

    @Transactional
    public PalabraResponse editar(Long id, PalabraRequest request) {
        Palabra palabra = palabraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palabra no encontrada."));
        aplicarPalabra(palabra, request);
        palabraRepository.save(palabra);
        reemplazarDetalles(palabra, request);
        return dictionaryService.toResponse(palabra, null);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!palabraRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Palabra no encontrada.");
        }
        historialRepository.deleteByPalabraId(id);
        favoritoRepository.deleteByPalabraId(id);
        traduccionRepository.deleteByPalabraId(id);
        ejemploRepository.deleteByPalabraId(id);
        audioRepository.deleteByPalabraId(id);
        palabraRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SugerenciaResponse> sugerencias() {
        return sugerenciaRepository.findAllByOrderByFechaCreacionDesc().stream().map(suggestionService::toResponse).toList();
    }

    @Transactional
    public SugerenciaResponse cambiarEstadoSugerencia(Long id, String estado) {
        String normalizado = estado == null ? "" : estado.trim().toUpperCase(Locale.ROOT);
        if (!List.of("PENDIENTE", "APROBADA", "RECHAZADA").contains(normalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no permitido.");
        }
        Sugerencia s = sugerenciaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sugerencia no encontrada."));
        s.setEstado(normalizado);
        return suggestionService.toResponse(sugerenciaRepository.save(s));
    }

    private void aplicarPalabra(Palabra palabra, PalabraRequest request) {
        Lengua lengua = lenguaRepository.findById(request.lenguaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengua no válida."));
        palabra.setEspanol(request.espanol().trim());
        palabra.setCategoria(request.categoria().trim());
        palabra.setSignificado(request.significado().trim());
        palabra.setLengua(lengua);
    }

    private void reemplazarDetalles(Palabra palabra, PalabraRequest request) {
        traduccionRepository.deleteByPalabraId(palabra.getId());
        ejemploRepository.deleteByPalabraId(palabra.getId());
        audioRepository.deleteByPalabraId(palabra.getId());

        if (request.traducciones() != null) {
            for (TraduccionInput input : request.traducciones()) {
                Lengua lengua = input.lenguaId() == null ? palabra.getLengua() : lenguaRepository.findById(input.lenguaId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lengua de traducción no válida."));
                Traduccion t = new Traduccion();
                t.setPalabra(palabra);
                t.setLengua(lengua);
                t.setTraduccion(input.traduccion().trim());
                traduccionRepository.save(t);
            }
        }

        if (request.ejemplos() != null) {
            for (String texto : request.ejemplos()) {
                if (texto == null || texto.isBlank()) continue;
                Ejemplo e = new Ejemplo();
                e.setPalabra(palabra);
                e.setEjemplo(texto.trim());
                ejemploRepository.save(e);
            }
        }

        if (request.audios() != null) {
            for (AudioInput input : request.audios()) {
                Audio a = new Audio();
                a.setPalabra(palabra);
                a.setUrlAudio(input.urlAudio().trim());
                a.setTipo(input.tipo().trim());
                audioRepository.save(a);
            }
        }
    }
}

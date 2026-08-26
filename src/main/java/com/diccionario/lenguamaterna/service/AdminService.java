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

    public AdminService(
            PalabraRepository palabraRepository,
            LenguaRepository lenguaRepository,
            TraduccionRepository traduccionRepository,
            EjemploRepository ejemploRepository,
            AudioRepository audioRepository,
            FavoritoRepository favoritoRepository,
            HistorialRepository historialRepository,
            SugerenciaRepository sugerenciaRepository,
            DictionaryService dictionaryService,
            SuggestionService suggestionService
    ) {
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

    /*
     * LISTAR PALABRAS
     *
     * Si no se envía un término de búsqueda, devuelve todas
     * las palabras ordenadas alfabéticamente.
     *
     * Si se envía q, filtra por la palabra en español.
     */
    @Transactional(readOnly = true)
    public List<PalabraResponse> listarPalabras(String q) {

        List<Palabra> palabras;

        if (q == null || q.isBlank()) {
            palabras = palabraRepository.findAllByOrderByEspanolAsc();
        } else {
            palabras =
                    palabraRepository
                            .findByEspanolContainingIgnoreCaseOrderByEspanolAsc(
                                    q.trim()
                            );
        }

        return palabras.stream()
                .map(palabra -> dictionaryService.toResponse(palabra, null))
                .toList();
    }

    /*
     * CREAR PALABRA
     */
    @Transactional
    public PalabraResponse crear(PalabraRequest request) {

        validarRequest(request);

        String palabraNormalizada = request.espanol().trim();

        validarDuplicado(
                palabraNormalizada,
                request.lenguaId(),
                null
        );

        Palabra palabra = new Palabra();

        aplicarPalabra(palabra, request);

        Palabra guardada = palabraRepository.save(palabra);

        reemplazarDetalles(guardada, request);

        return dictionaryService.toResponse(guardada, null);
    }

    /*
     * EDITAR PALABRA
     */
    @Transactional
    public PalabraResponse editar(
            Long id,
            PalabraRequest request
    ) {

        validarRequest(request);

        Palabra palabra = palabraRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Palabra no encontrada."
                        )
                );

        String palabraNormalizada = request.espanol().trim();

        validarDuplicado(
                palabraNormalizada,
                request.lenguaId(),
                id
        );

        aplicarPalabra(palabra, request);

        Palabra actualizada = palabraRepository.save(palabra);

        reemplazarDetalles(actualizada, request);

        return dictionaryService.toResponse(actualizada, null);
    }

    /*
     * ELIMINAR PALABRA
     */
    @Transactional
    public void eliminar(Long id) {

        if (!palabraRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Palabra no encontrada."
            );
        }

        /*
         * Se eliminan primero los registros relacionados
         * para conservar la integridad referencial.
         */
        historialRepository.deleteByPalabraId(id);
        favoritoRepository.deleteByPalabraId(id);
        traduccionRepository.deleteByPalabraId(id);
        ejemploRepository.deleteByPalabraId(id);
        audioRepository.deleteByPalabraId(id);

        palabraRepository.deleteById(id);
    }

    /*
     * SUGERENCIAS
     */
    @Transactional(readOnly = true)
    public List<SugerenciaResponse> sugerencias() {

        return sugerenciaRepository
                .findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(suggestionService::toResponse)
                .toList();
    }

    @Transactional
    public SugerenciaResponse cambiarEstadoSugerencia(
            Long id,
            String estado
    ) {

        String normalizado =
                estado == null
                        ? ""
                        : estado.trim().toUpperCase(Locale.ROOT);

        if (!List.of(
                "PENDIENTE",
                "APROBADA",
                "RECHAZADA"
        ).contains(normalizado)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado no permitido."
            );
        }

        Sugerencia sugerencia =
                sugerenciaRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Sugerencia no encontrada."
                                )
                        );

        sugerencia.setEstado(normalizado);

        Sugerencia guardada =
                sugerenciaRepository.save(sugerencia);

        return suggestionService.toResponse(guardada);
    }

    /*
     * VALIDACIÓN GENERAL DEL REQUEST
     */
    private void validarRequest(PalabraRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la palabra son obligatorios."
            );
        }

        if (request.espanol() == null ||
                request.espanol().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La palabra en español es obligatoria."
            );
        }

        if (request.categoria() == null ||
                request.categoria().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría es obligatoria."
            );
        }

        if (request.significado() == null ||
                request.significado().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El significado es obligatorio."
            );
        }

        if (request.lenguaId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La lengua es obligatoria."
            );
        }
    }

    /*
     * VALIDACIÓN DE DUPLICADOS
     *
     * No permite registrar dos veces la misma palabra
     * dentro de la misma lengua.
     */
    private void validarDuplicado(
            String espanol,
            Long lenguaId,
            Long idActual
    ) {

        boolean existe;

        if (idActual == null) {

            existe =
                    palabraRepository
                            .existsByEspanolIgnoreCaseAndLengua_Id(
                                    espanol,
                                    lenguaId
                            );

        } else {

            existe =
                    palabraRepository
                            .existsByEspanolIgnoreCaseAndLengua_IdAndIdNot(
                                    espanol,
                                    lenguaId,
                                    idActual
                            );
        }

        if (existe) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe esta palabra registrada para la lengua seleccionada."
            );
        }
    }

    /*
     * ASIGNAR DATOS PRINCIPALES DE PALABRA
     */
    private void aplicarPalabra(
            Palabra palabra,
            PalabraRequest request
    ) {

        Lengua lengua =
                lenguaRepository
                        .findById(request.lenguaId())
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Lengua no válida."
                                )
                        );

        palabra.setEspanol(
                request.espanol().trim()
        );

        palabra.setCategoria(
                request.categoria().trim()
        );

        palabra.setSignificado(
                request.significado().trim()
        );

        palabra.setLengua(lengua);
    }

    /*
     * TRADUCCIONES, EJEMPLOS Y AUDIOS
     */
    private void reemplazarDetalles(
            Palabra palabra,
            PalabraRequest request
    ) {

        traduccionRepository.deleteByPalabraId(
                palabra.getId()
        );

        ejemploRepository.deleteByPalabraId(
                palabra.getId()
        );

        audioRepository.deleteByPalabraId(
                palabra.getId()
        );

        /*
         * TRADUCCIONES
         */
        if (request.traducciones() != null) {

            for (TraduccionInput input :
                    request.traducciones()) {

                if (input == null ||
                        input.traduccion() == null ||
                        input.traduccion().isBlank()) {
                    continue;
                }

                Lengua lengua;

                if (input.lenguaId() == null) {

                    lengua = palabra.getLengua();

                } else {

                    lengua =
                            lenguaRepository
                                    .findById(input.lenguaId())
                                    .orElseThrow(() ->
                                            new ResponseStatusException(
                                                    HttpStatus.BAD_REQUEST,
                                                    "Lengua de traducción no válida."
                                            )
                                    );
                }

                Traduccion traduccion =
                        new Traduccion();

                traduccion.setPalabra(palabra);
                traduccion.setLengua(lengua);

                traduccion.setTraduccion(
                        input.traduccion().trim()
                );

                traduccionRepository.save(
                        traduccion
                );
            }
        }

        /*
         * EJEMPLOS
         */
        if (request.ejemplos() != null) {

            for (String texto :
                    request.ejemplos()) {

                if (texto == null ||
                        texto.isBlank()) {
                    continue;
                }

                Ejemplo ejemplo =
                        new Ejemplo();

                ejemplo.setPalabra(palabra);

                ejemplo.setEjemplo(
                        texto.trim()
                );

                ejemploRepository.save(
                        ejemplo
                );
            }
        }

        /*
         * AUDIOS
         */
        if (request.audios() != null) {

            for (AudioInput input :
                    request.audios()) {

                if (input == null ||
                        input.urlAudio() == null ||
                        input.urlAudio().isBlank()) {
                    continue;
                }

                Audio audio =
                        new Audio();

                audio.setPalabra(palabra);

                audio.setUrlAudio(
                        input.urlAudio().trim()
                );

                if (input.tipo() != null) {
                    audio.setTipo(
                            input.tipo().trim()
                    );
                }

                audioRepository.save(audio);
            }
        }
    }
}
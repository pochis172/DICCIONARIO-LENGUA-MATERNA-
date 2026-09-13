package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionRequest;
import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionResponse;
import com.diccionario.lenguamaterna.entity.Lengua;
import com.diccionario.lenguamaterna.entity.Palabra;
import com.diccionario.lenguamaterna.entity.Traduccion;
import com.diccionario.lenguamaterna.repository.LenguaRepository;
import com.diccionario.lenguamaterna.repository.PalabraRepository;
import com.diccionario.lenguamaterna.repository.TraduccionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TraduccionService {

    private final TraduccionRepository traduccionRepository;
    private final PalabraRepository palabraRepository;
    private final LenguaRepository lenguaRepository;

    public TraduccionService(
            TraduccionRepository traduccionRepository,
            PalabraRepository palabraRepository,
            LenguaRepository lenguaRepository) {

        this.traduccionRepository = traduccionRepository;
        this.palabraRepository = palabraRepository;
        this.lenguaRepository = lenguaRepository;
    }

    // LISTAR TODAS LAS TRADUCCIONES
    @Transactional(readOnly = true)
    public List<TraduccionResponse> listar() {

        return traduccionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // BUSCAR UNA TRADUCCIÓN POR ID
    @Transactional(readOnly = true)
    public TraduccionResponse buscarPorId(Long id) {

        Traduccion traduccion = buscarEntidad(id);

        return toResponse(traduccion);
    }

    // NUEVO: BUSCAR POR PALABRA O POR TEXTO DE TRADUCCIÓN
    @Transactional(readOnly = true)
    public List<TraduccionResponse> buscar(String q) {

        String texto = q == null ? "" : q.trim();

        if (texto.isBlank()) {
            return listar();
        }

        return traduccionRepository
                .findByPalabra_EspanolContainingIgnoreCaseOrTraduccionContainingIgnoreCaseOrderByIdAsc(
                        texto,
                        texto
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // LISTAR TRADUCCIONES DE UNA PALABRA
    @Transactional(readOnly = true)
    public List<TraduccionResponse> listarPorPalabra(Long palabraId) {

        if (!palabraRepository.existsById(palabraId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La palabra indicada no existe."
            );
        }

        return traduccionRepository
                .findByPalabraIdOrderByIdAsc(palabraId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // NUEVO: LISTAR TRADUCCIONES POR LENGUA
    @Transactional(readOnly = true)
    public List<TraduccionResponse> listarPorLengua(Long lenguaId) {

        if (!lenguaRepository.existsById(lenguaId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La lengua indicada no existe."
            );
        }

        return traduccionRepository
                .findByLenguaIdOrderByIdAsc(lenguaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // CREAR UNA NUEVA TRADUCCIÓN
    @Transactional
    public TraduccionResponse crear(TraduccionRequest request) {

        Palabra palabra = buscarPalabra(request.palabraId());

        Lengua lengua = buscarLengua(request.lenguaId());

        String texto = request.traduccion().trim();

        boolean duplicada =
                traduccionRepository
                        .existsByPalabraIdAndLenguaIdAndTraduccionIgnoreCase(
                                palabra.getId(),
                                lengua.getId(),
                                texto
                        );

        if (duplicada) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta traducción ya está registrada."
            );
        }

        Traduccion traduccion = new Traduccion();

        traduccion.setPalabra(palabra);
        traduccion.setLengua(lengua);
        traduccion.setTraduccion(texto);

        Traduccion guardada =
                traduccionRepository.save(traduccion);

        return toResponse(guardada);
    }

    // ACTUALIZAR UNA TRADUCCIÓN
    @Transactional
    public TraduccionResponse actualizar(
            Long id,
            TraduccionRequest request) {

        Traduccion traduccion = buscarEntidad(id);

        Palabra palabra = buscarPalabra(request.palabraId());

        Lengua lengua = buscarLengua(request.lenguaId());

        String texto = request.traduccion().trim();

        boolean duplicada =
                traduccionRepository
                        .existsByPalabraIdAndLenguaIdAndTraduccionIgnoreCaseAndIdNot(
                                palabra.getId(),
                                lengua.getId(),
                                texto,
                                id
                        );

        if (duplicada) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe otra traducción igual."
            );
        }

        traduccion.setPalabra(palabra);
        traduccion.setLengua(lengua);
        traduccion.setTraduccion(texto);

        Traduccion actualizada =
                traduccionRepository.save(traduccion);

        return toResponse(actualizada);
    }

    // ELIMINAR UNA TRADUCCIÓN
    @Transactional
    public void eliminar(Long id) {

        Traduccion traduccion = buscarEntidad(id);

        traduccionRepository.delete(traduccion);
    }

    // BUSCAR ENTIDAD TRADUCCIÓN
    private Traduccion buscarEntidad(Long id) {

        return traduccionRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Traducción no encontrada."
                        )
                );
    }

    // VALIDAR QUE LA PALABRA EXISTA
    private Palabra buscarPalabra(Long id) {

        return palabraRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Palabra no encontrada."
                        )
                );
    }

    // VALIDAR QUE LA LENGUA EXISTA
    private Lengua buscarLengua(Long id) {

        return lenguaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Lengua no encontrada."
                        )
                );
    }

    // CONVERTIR ENTIDAD A RESPUESTA PARA POSTMAN
    private TraduccionResponse toResponse(
            Traduccion traduccion) {

        return new TraduccionResponse(

                traduccion.getId(),

                traduccion.getPalabra().getId(),

                traduccion.getPalabra().getEspanol(),

                traduccion.getLengua().getId(),

                traduccion.getLengua().getNombre(),

                traduccion.getTraduccion()
        );
    }
}

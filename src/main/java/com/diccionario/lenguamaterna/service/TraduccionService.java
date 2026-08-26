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

    @Transactional(readOnly = true)
    public List<TraduccionResponse> listar() {
        return traduccionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TraduccionResponse buscarPorId(Long id) {
        Traduccion traduccion = buscarEntidad(id);
        return toResponse(traduccion);
    }

    @Transactional(readOnly = true)
    public List<TraduccionResponse> listarPorPalabra(Long palabraId) {
        return traduccionRepository.findByPalabraIdOrderByIdAsc(palabraId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TraduccionResponse crear(TraduccionRequest request) {

        Palabra palabra = palabraRepository.findById(request.palabraId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Palabra no encontrada."));

        Lengua lengua = lenguaRepository.findById(request.lenguaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lengua no encontrada."));

        Traduccion traduccion = new Traduccion();

        traduccion.setPalabra(palabra);
        traduccion.setLengua(lengua);
        traduccion.setTraduccion(request.traduccion().trim());

        Traduccion guardada = traduccionRepository.save(traduccion);

        return toResponse(guardada);
    }

    @Transactional
    public TraduccionResponse actualizar(
            Long id,
            TraduccionRequest request) {

        Traduccion traduccion = buscarEntidad(id);

        Palabra palabra = palabraRepository.findById(request.palabraId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Palabra no encontrada."));

        Lengua lengua = lenguaRepository.findById(request.lenguaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lengua no encontrada."));

        traduccion.setPalabra(palabra);
        traduccion.setLengua(lengua);
        traduccion.setTraduccion(request.traduccion().trim());

        Traduccion actualizada = traduccionRepository.save(traduccion);

        return toResponse(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {

        Traduccion traduccion = buscarEntidad(id);

        traduccionRepository.delete(traduccion);
    }

    private Traduccion buscarEntidad(Long id) {
        return traduccionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Traducción no encontrada."));
    }

    private TraduccionResponse toResponse(Traduccion traduccion) {

        return new TraduccionResponse(
                traduccion.getId(),
                traduccion.getPalabra().getId(),
                traduccion.getLengua().getId(),
                traduccion.getTraduccion());
    }
}
package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
import com.diccionario.lenguamaterna.entity.Lengua;
import com.diccionario.lenguamaterna.entity.Region;
import com.diccionario.lenguamaterna.repository.LenguaRepository;
import com.diccionario.lenguamaterna.repository.RegionRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LenguaService {

    private final LenguaRepository lenguaRepository;
    private final RegionRepository regionRepository;

    public LenguaService(
            LenguaRepository lenguaRepository,
            RegionRepository regionRepository
    ) {
        this.lenguaRepository = lenguaRepository;
        this.regionRepository = regionRepository;
    }

    @Transactional(readOnly = true)
    public List<LenguaResponse> listar() {

        return lenguaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LenguaResponse buscarPorId(Long id) {

        Lengua lengua = lenguaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lengua no encontrada."
                ));

        return toResponse(lengua);
    }

    @Transactional
    public LenguaResponse crear(LenguaRequest request) {

        String nombre = request.nombre().trim();

        if (lenguaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una lengua registrada con ese nombre."
            );
        }

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Región no encontrada."
                ));

        Lengua lengua = new Lengua();

        lengua.setNombre(nombre);
        lengua.setRegion(region);
        lengua.setFamiliaLinguistica(
                normalizarFamilia(request.familiaLinguistica())
        );

        Lengua guardada = lenguaRepository.save(lengua);

        return toResponse(guardada);
    }

    @Transactional
    public LenguaResponse actualizar(
            Long id,
            LenguaRequest request
    ) {

        Lengua lengua = lenguaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lengua no encontrada."
                ));

        String nombre = request.nombre().trim();

        lenguaRepository.findByNombreIgnoreCase(nombre)
                .filter(otraLengua ->
                        !otraLengua.getId().equals(id)
                )
                .ifPresent(otraLengua -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Ya existe otra lengua registrada con ese nombre."
                    );
                });

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Región no encontrada."
                ));

        lengua.setNombre(nombre);
        lengua.setRegion(region);
        lengua.setFamiliaLinguistica(
                normalizarFamilia(request.familiaLinguistica())
        );

        Lengua actualizada = lenguaRepository.save(lengua);

        return toResponse(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {

        Lengua lengua = lenguaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lengua no encontrada."
                ));

        try {

            lenguaRepository.delete(lengua);
            lenguaRepository.flush();

        } catch (DataIntegrityViolationException e) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar la lengua porque tiene información relacionada."
            );
        }
    }

    private LenguaResponse toResponse(Lengua lengua) {

        Region region = lengua.getRegion();

        return new LenguaResponse(
                lengua.getId(),
                lengua.getNombre(),
                region.getId(),
                region.getNombre(),
                lengua.getFamiliaLinguistica()
        );
    }

    private String normalizarFamilia(String familia) {

        if (familia == null || familia.isBlank()) {
            return null;
        }

        return familia.trim();
    }
}
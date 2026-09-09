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

    // =========================
    // LISTAR TODAS LAS LENGUAS
    // =========================
    @Transactional(readOnly = true)
    public List<LenguaResponse> listar() {

        return lenguaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // BUSCAR LENGUA POR ID
    // =========================
    @Transactional(readOnly = true)
    public LenguaResponse buscarPorId(Long id) {

        Lengua lengua = lenguaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lengua no encontrada."
                ));

        return toResponse(lengua);
    }

    // =========================
    // BUSCAR POR NOMBRE
    // =========================
    @Transactional(readOnly = true)
    public List<LenguaResponse> buscarPorNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar un nombre para realizar la búsqueda."
            );
        }

        String textoBusqueda = nombre.trim();

        return lenguaRepository
                .findByNombreContainingIgnoreCaseOrderByNombreAsc(textoBusqueda)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // BUSCAR POR ID DE REGIÓN
    // =========================
    @Transactional(readOnly = true)
    public List<LenguaResponse> buscarPorRegion(Long regionId) {

        if (regionId == null || regionId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de la región no es válido."
            );
        }

        if (!regionRepository.existsById(regionId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Región no encontrada."
            );
        }

        return lenguaRepository
                .findByRegionIdOrderByNombreAsc(regionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================================
    // NUEVO: BUSCAR POR NOMBRE DE LA REGIÓN
    // ==========================================
    @Transactional(readOnly = true)
    public List<LenguaResponse> buscarPorNombreRegion(String nombreRegion) {

        if (nombreRegion == null || nombreRegion.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar el nombre de una región."
            );
        }

        String textoBusqueda = nombreRegion.trim();

        return lenguaRepository
                .findByRegionNombreContainingIgnoreCaseOrderByNombreAsc(
                        textoBusqueda
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // BUSCAR POR FAMILIA
    // =========================
    @Transactional(readOnly = true)
    public List<LenguaResponse> buscarPorFamilia(String familia) {

        if (familia == null || familia.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar una familia lingüística."
            );
        }

        String textoBusqueda = familia.trim();

        return lenguaRepository
                .findByFamiliaLinguisticaContainingIgnoreCaseOrderByNombreAsc(
                        textoBusqueda
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // CREAR LENGUA
    // =========================
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

    // =========================
    // ACTUALIZAR LENGUA
    // =========================
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

    // =========================
    // ELIMINAR LENGUA
    // =========================
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

    // =========================
    // CONVERTIR ENTITY A DTO
    // =========================
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

    // =========================
    // NORMALIZAR FAMILIA
    // =========================
    private String normalizarFamilia(String familia) {

        if (familia == null || familia.isBlank()) {
            return null;
        }

        return familia.trim();
    }
}
package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaPatchRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
import com.diccionario.lenguamaterna.entity.Lengua;
import com.diccionario.lenguamaterna.entity.Region;
import com.diccionario.lenguamaterna.repository.LenguaRepository;
import com.diccionario.lenguamaterna.repository.RegionRepository;
import com.diccionario.lenguamaterna.repository.TraduccionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LenguaServiceTest {

    @Mock
    private LenguaRepository lenguaRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private TraduccionRepository traduccionRepository;

    @InjectMocks
    private LenguaService lenguaService;

    // PRUEBA 1
    @Test
    void listar_debeRetornarTodasLasLenguas() {

        Lengua lengua = crearLengua();

        when(lenguaRepository.findAll())
                .thenReturn(List.of(lengua));

        List<LenguaResponse> resultado =
                lenguaService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Inga", resultado.get(0).nombre());

        verify(lenguaRepository).findAll();
    }

    // PRUEBA 2
    @Test
    void buscarPorId_cuandoExiste_debeRetornarLengua() {

        Lengua lengua = crearLengua();

        when(lenguaRepository.findById(1L))
                .thenReturn(Optional.of(lengua));

        LenguaResponse resultado =
                lenguaService.buscarPorId(1L);

        assertEquals(1L, resultado.id());
        assertEquals("Inga", resultado.nombre());
        assertEquals("Putumayo", resultado.region());
    }

    // PRUEBA 3
    @Test
    void buscarPorId_cuandoNoExiste_debeLanzar404() {

        when(lenguaRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException excepcion =
                assertThrows(
                        ResponseStatusException.class,
                        () -> lenguaService.buscarPorId(99L)
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                excepcion.getStatusCode()
        );
    }

    // PRUEBA 4
    @Test
    void crear_cuandoDatosValidos_debeGuardarLengua() {

        Region region = crearRegion();

        LenguaRequest request =
                new LenguaRequest(
                        "Inga",
                        1L,
                        "Quechua"
                );

        when(lenguaRepository.existsByNombreIgnoreCase("Inga"))
                .thenReturn(false);

        when(regionRepository.findById(1L))
                .thenReturn(Optional.of(region));

        when(lenguaRepository.save(any(Lengua.class)))
                .thenAnswer(invocacion -> {

                    Lengua lengua =
                            invocacion.getArgument(0);

                    lengua.setId(1L);

                    return lengua;
                });

        LenguaResponse resultado =
                lenguaService.crear(request);

        assertEquals(1L, resultado.id());
        assertEquals("Inga", resultado.nombre());
        assertEquals("Putumayo", resultado.region());
        assertEquals(
                "Quechua",
                resultado.familiaLinguistica()
        );

        verify(lenguaRepository)
                .save(any(Lengua.class));
    }

    // PRUEBA 5
    @Test
    void crear_cuandoNombreYaExiste_debeLanzar409() {

        LenguaRequest request =
                new LenguaRequest(
                        "Inga",
                        1L,
                        "Quechua"
                );

        when(lenguaRepository.existsByNombreIgnoreCase("Inga"))
                .thenReturn(true);

        ResponseStatusException excepcion =
                assertThrows(
                        ResponseStatusException.class,
                        () -> lenguaService.crear(request)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                excepcion.getStatusCode()
        );

        verify(lenguaRepository, never())
                .save(any(Lengua.class));
    }

    // PRUEBA 6
    @Test
    void actualizarParcial_sinCampos_debeLanzar400() {

        Lengua lengua = crearLengua();

        LenguaPatchRequest request =
                new LenguaPatchRequest(
                        null,
                        null,
                        null,
                        null
                );

        when(lenguaRepository.findById(1L))
                .thenReturn(Optional.of(lengua));

        ResponseStatusException excepcion =
                assertThrows(
                        ResponseStatusException.class,
                        () -> lenguaService.actualizarParcial(
                                1L,
                                request
                        )
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                excepcion.getStatusCode()
        );

        verify(lenguaRepository, never())
                .save(any(Lengua.class));
    }

    private Region crearRegion() {

        Region region = new Region();

        region.setId(1L);
        region.setNombre("Putumayo");

        return region;
    }

    private Lengua crearLengua() {

        Lengua lengua = new Lengua();

        lengua.setId(1L);
        lengua.setNombre("Inga");
        lengua.setRegion(crearRegion());
        lengua.setFamiliaLinguistica("Quechua");

        return lengua;
    }
}
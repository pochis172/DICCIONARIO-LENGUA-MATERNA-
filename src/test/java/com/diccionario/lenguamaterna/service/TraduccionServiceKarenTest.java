
package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionRequest;
import com.diccionario.lenguamaterna.repository.LenguaRepository;
import com.diccionario.lenguamaterna.repository.PalabraRepository;
import com.diccionario.lenguamaterna.repository.TraduccionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraduccionServiceKarenTest {

    @Mock
    private TraduccionRepository traduccionRepository;

    @Mock
    private PalabraRepository palabraRepository;

    @Mock
    private LenguaRepository lenguaRepository;

    @InjectMocks
    private TraduccionService traduccionService;

    // PRUEBA UNITARIA 1:
    // Buscar una traducción inexistente devuelve 404.
    @Test
    void buscarTraduccionInexistente_debeRetornar404() {

        when(traduccionRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> traduccionService.buscarPorId(99L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        verify(traduccionRepository).findById(99L);
    }

    // PRUEBA UNITARIA 2:
    // Una traducción duplicada no debe guardarse.
    @Test
    void crearTraduccionDuplicada_debeRetornar409() {

        var palabra = new com.diccionario.lenguamaterna.entity.Palabra();
        palabra.setId(1L);

        var lengua = new com.diccionario.lenguamaterna.entity.Lengua();
        lengua.setId(2L);

        TraduccionRequest request = new TraduccionRequest(1L, 2L, "Alli puncha");

        when(palabraRepository.findById(1L))
                .thenReturn(Optional.of(palabra));

        when(lenguaRepository.findById(2L))
                .thenReturn(Optional.of(lengua));

        when(traduccionRepository
                .existsByPalabraIdAndLenguaIdAndTraduccionIgnoreCase(
                        1L, 2L, "Alli puncha"))
                .thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> traduccionService.crear(request));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(traduccionRepository, never()).save(
                any(com.diccionario.lenguamaterna.entity.Traduccion.class));
    }

    // PRUEBA UNITARIA 3:
    // Consultar traducciones de una palabra inexistente devuelve 404.
    @Test
    void listarPorPalabraInexistente_debeRetornar404() {

        when(palabraRepository.existsById(99L))
                .thenReturn(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> traduccionService.listarPorPalabra(99L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        verifyNoInteractions(traduccionRepository);
    }
}

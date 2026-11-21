package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.ActualizarPalabraRequest;
import com.diccionario.lenguamaterna.entity.Palabra;
import com.diccionario.lenguamaterna.repository.PalabraRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServicePalabrasKevinTest {

    @Mock
    private PalabraRepository palabraRepository;

    @Mock
    private DictionaryService dictionaryService;

    @InjectMocks
    private AdminService adminService;

    // PRUEBA UNITARIA 1 (Service):
    // Actualizar datos sin ningun campo debe responder 400 y no guardar.
    @Test
    void actualizarDatosSinCampos_debeRetornar400() {

        ActualizarPalabraRequest request =
                new ActualizarPalabraRequest(null, null, null);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adminService.actualizarDatos(1L, request));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(palabraRepository, never()).save(any());
    }

    // PRUEBA UNITARIA 2 (Service):
    // Eliminar una palabra inexistente debe responder 404 y no borrar nada.
    @Test
    void eliminarPalabraInexistente_debeRetornar404() {

        when(palabraRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adminService.eliminar(99L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(palabraRepository, never()).deleteById(any());
    }

    // Complemento: un campo con solo espacios se rechaza antes de guardar.
    @Test
    void actualizarDatosConCampoVacio_debeRetornar400() {

        when(palabraRepository.findById(1L))
                .thenReturn(Optional.of(new Palabra()));

        ActualizarPalabraRequest request =
                new ActualizarPalabraRequest("   ", null, null);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adminService.actualizarDatos(1L, request));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(palabraRepository, never()).save(any());
    }
}

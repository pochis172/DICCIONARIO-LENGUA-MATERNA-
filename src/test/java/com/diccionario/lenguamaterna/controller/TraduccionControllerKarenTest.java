
package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionRequest;
import com.diccionario.lenguamaterna.dto.TraduccionDtos.TraduccionResponse;
import com.diccionario.lenguamaterna.service.TraduccionService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraduccionControllerKarenTest {

    @Mock
    private TraduccionService traduccionService;

    @InjectMocks
    private TraduccionController traduccionController;

    // PRUEBA UNITARIA 4:
    // Crear una traducción válida devuelve HTTP 201.
    @Test
    void crearTraduccionValida_debeRetornar201() {

        TraduccionRequest request = new TraduccionRequest(1L, 2L, "Alli puncha");

        TraduccionResponse respuesta = new TraduccionResponse(
                10L,
                1L,
                "Buenos días",
                2L,
                "Inga",
                "Alli puncha");

        when(traduccionService.crear(request))
                .thenReturn(respuesta);

        ResponseEntity<TraduccionResponse> resultado = traduccionController.crear(request);

        assertEquals(
                HttpStatus.CREATED,
                resultado.getStatusCode());

        assertNotNull(resultado.getBody());

        assertEquals(
                "Alli puncha",
                resultado.getBody().traduccion());

        verify(traduccionService).crear(request);
    }
}

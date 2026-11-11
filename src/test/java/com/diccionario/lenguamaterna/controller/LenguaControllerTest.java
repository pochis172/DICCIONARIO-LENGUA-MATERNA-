package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaRequest;
import com.diccionario.lenguamaterna.dto.LenguaDtos.LenguaResponse;
import com.diccionario.lenguamaterna.service.LenguaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LenguaControllerTest {

    @Mock
    private LenguaService lenguaService;

    @InjectMocks
    private LenguaController lenguaController;

    @Test
    void crear_cuandoDatosValidos_debeRetornar201() {

        LenguaRequest request =
                new LenguaRequest(
                        "Inga",
                        1L,
                        "Quechua"
                );

        LenguaResponse respuesta =
                new LenguaResponse(
                        1L,
                        "Inga",
                        1L,
                        "Putumayo",
                        "Quechua"
                );

        when(lenguaService.crear(request))
                .thenReturn(respuesta);

        ResponseEntity<LenguaResponse> resultado =
                lenguaController.crear(request);

        assertEquals(
                HttpStatus.CREATED,
                resultado.getStatusCode()
        );

        assertEquals(
                "Inga",
                resultado.getBody().nombre()
        );

        verify(lenguaService).crear(request);
    }

    @Test
    void eliminar_cuandoLenguaExiste_debeRetornar204() {

        doNothing()
                .when(lenguaService)
                .eliminar(1L);

        ResponseEntity<Void> resultado =
                lenguaController.eliminar(1L);

        assertEquals(
                HttpStatus.NO_CONTENT,
                resultado.getStatusCode()
        );

        verify(lenguaService).eliminar(1L);
    }
}
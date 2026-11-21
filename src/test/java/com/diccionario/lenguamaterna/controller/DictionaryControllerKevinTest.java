package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.service.DictionaryService;
import com.diccionario.lenguamaterna.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictionaryControllerKevinTest {

    @Mock
    private DictionaryService dictionaryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private DictionaryController dictionaryController;

    // PRUEBA UNITARIA 4 (Controller):
    // Un visitante sin autenticar busca una palabra: el controlador delega
    // en el servicio con usuarioId nulo y devuelve su resultado.
    @Test
    void buscarSinAutenticacion_debeDelegarEnElServicio() {

        List<PalabraResponse> esperado = List.of(mock(PalabraResponse.class));

        when(dictionaryService.buscar("agua", 2L, null)).thenReturn(esperado);

        List<PalabraResponse> resultado =
                dictionaryController.buscar("agua", 2L, null);

        assertSame(esperado, resultado);
        verify(dictionaryService).buscar("agua", 2L, null);
    }
}

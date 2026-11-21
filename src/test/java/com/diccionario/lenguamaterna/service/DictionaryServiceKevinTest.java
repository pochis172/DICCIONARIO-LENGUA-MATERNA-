package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.repository.PalabraRepository;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DictionaryServiceKevinTest {

    @Mock
    private PalabraRepository palabraRepository;

    @InjectMocks
    private DictionaryService dictionaryService;

    // PRUEBA UNITARIA 3 (Service):
    // Una busqueda en blanco devuelve lista vacia sin consultar la base.
    @Test
    void buscarConConsultaEnBlanco_debeRetornarListaVacia() {

        List<PalabraResponse> resultado =
                dictionaryService.buscar("   ", null, null);

        assertTrue(resultado.isEmpty());
        verifyNoInteractions(palabraRepository);
    }

    // Obtener una palabra inexistente responde 404.
    @Test
    void obtenerPalabraInexistente_debeRetornar404() {

        when(palabraRepository.findById(77L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> dictionaryService.obtener(77L, null));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }
}

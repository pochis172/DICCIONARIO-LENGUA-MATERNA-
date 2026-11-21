package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.DictionaryDtos.PalabraResponse;
import com.diccionario.lenguamaterna.repository.PalabraRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
}

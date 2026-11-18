
package com.diccionario.lenguamaterna.controller;

import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void obtenerPerfil_DebeRetornarPerfilDelUsuarioAutenticado() {

        // 1. Preparar los datos de prueba
        String correo = "usuario@gmail.com";
        Long usuarioId = 1L;

        // 2. Simular la autenticación del usuario
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn(correo);

        // 3. Crear una respuesta simulada del perfil
        UsuarioResponse respuestaEsperada = mock(UsuarioResponse.class);

        // 4. Simular el comportamiento del servicio
        when(userService.obtenerIdPorCorreo(correo))
                .thenReturn(usuarioId);

        when(userService.obtenerPerfil(usuarioId))
                .thenReturn(respuestaEsperada);

        // 5. Ejecutar el método del controlador
        UsuarioResponse resultado =
                userController.obtenerPerfil(authentication);

        // 6. Verificar el resultado
        assertSame(respuestaEsperada, resultado);

        // 7. Verificar las llamadas al servicio
        verify(userService).obtenerIdPorCorreo(correo);
        verify(userService).obtenerPerfil(usuarioId);
    }
}


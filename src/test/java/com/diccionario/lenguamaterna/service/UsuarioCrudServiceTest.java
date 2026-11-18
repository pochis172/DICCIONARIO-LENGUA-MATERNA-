
package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.CrearUsuarioRequest;
import com.diccionario.lenguamaterna.dto.UsuarioCrudDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.entity.Rol;
import com.diccionario.lenguamaterna.entity.Usuario;
import com.diccionario.lenguamaterna.repository.FavoritoRepository;
import com.diccionario.lenguamaterna.repository.HistorialRepository;
import com.diccionario.lenguamaterna.repository.RolRepository;
import com.diccionario.lenguamaterna.repository.SugerenciaRepository;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioCrudServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private HistorialRepository historialRepository;

    @Mock
    private SugerenciaRepository sugerenciaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioCrudService usuarioCrudService;

    // PRUEBA UNITARIA 1
    @Test
    void crear_cuandoDatosValidos_debeGuardarUsuario() {

        CrearUsuarioRequest request =
                new CrearUsuarioRequest(
                        "Daniel",
                        "daniel@gmail.com",
                        "123456",
                        "USUARIO"
                );

        Rol rol = new Rol("USUARIO");
        rol.setId(1L);

        when(usuarioRepository.existsByCorreoIgnoreCase("daniel@gmail.com"))
                .thenReturn(false);

        when(rolRepository.findByNombreIgnoreCase("USUARIO"))
                .thenReturn(Optional.of(rol));

        when(passwordEncoder.encode("123456"))
                .thenReturn("contrasena_cifrada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacion -> {
                    Usuario usuario = invocacion.getArgument(0);
                    usuario.setId(1L);
                    return usuario;
                });

        UsuarioResponse resultado =
                usuarioCrudService.crear(request);

        assertEquals(1L, resultado.id());
        assertEquals("Daniel", resultado.nombre());
        assertEquals("daniel@gmail.com", resultado.correo());
        assertEquals("USUARIO", resultado.rol());

        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("123456");
    }

    // PRUEBA UNITARIA 2
    @Test
    void crear_cuandoCorreoExiste_debeLanzar409() {

        CrearUsuarioRequest request =
                new CrearUsuarioRequest(
                        "Daniel",
                        "daniel@gmail.com",
                        "123456",
                        "USUARIO"
                );

        when(usuarioRepository.existsByCorreoIgnoreCase("daniel@gmail.com"))
                .thenReturn(true);

        ResponseStatusException excepcion =
                assertThrows(
                        ResponseStatusException.class,
                        () -> usuarioCrudService.crear(request)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                excepcion.getStatusCode()
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(rolRepository, passwordEncoder);
    }
}

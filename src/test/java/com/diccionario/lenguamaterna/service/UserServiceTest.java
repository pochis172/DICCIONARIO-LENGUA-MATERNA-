
package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.dto.UserDtos.PerfilRequest;
import com.diccionario.lenguamaterna.dto.AuthDtos.UsuarioResponse;
import com.diccionario.lenguamaterna.entity.Rol;
import com.diccionario.lenguamaterna.entity.Usuario;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;
import com.diccionario.lenguamaterna.repository.PalabraRepository;
import com.diccionario.lenguamaterna.repository.FavoritoRepository;
import com.diccionario.lenguamaterna.repository.HistorialRepository;
import com.diccionario.lenguamaterna.repository.TraduccionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PalabraRepository palabraRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private HistorialRepository historialRepository;

    @Mock
    private TraduccionRepository traduccionRepository;

    @Mock
    private DictionaryService dictionaryService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // PRUEBA UNITARIA 3
    @Test
    void actualizarPerfil_cuandoDatosValidos_debeActualizarUsuario() {

        Rol rol = new Rol("USUARIO");
        rol.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Daniel");
        usuario.setCorreo("daniel@gmail.com");
        usuario.setContrasena("password_cifrada");
        usuario.setRol(rol);

        PerfilRequest request = new PerfilRequest(
                "Daniel Actualizado",
                "nuevo@gmail.com"
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByCorreoIgnoreCase("nuevo@gmail.com"))
                .thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        UsuarioResponse resultado =
                userService.actualizarPerfil(1L, request);

        assertEquals(1L, resultado.id());
        assertEquals("Daniel Actualizado", resultado.nombre());
        assertEquals("nuevo@gmail.com", resultado.correo());
        assertEquals("USUARIO", resultado.rol());

        verify(usuarioRepository).save(any(Usuario.class));
    }
}


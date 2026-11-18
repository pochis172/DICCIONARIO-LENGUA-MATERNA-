
package com.diccionario.lenguamaterna.integration;

import com.diccionario.lenguamaterna.entity.Usuario;
import com.diccionario.lenguamaterna.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String correo = "integracion.usuario@test.com";

    @BeforeEach
    void limpiarUsuarioAnterior() {
        usuarioRepository.findByCorreoIgnoreCase(correo)
                .ifPresent(usuarioRepository::delete);
    }

    @Test
    void registrarUsuario_DebeGuardarUsuarioYGenerarToken() throws Exception {

        String json = """
                {
                    "nombre": "Usuario Integracion",
                    "correo": "integracion.usuario@test.com",
                    "contrasena": "Clave123456",
                    "aceptaTerminos": true
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.correo")
                        .value(correo))
                .andExpect(jsonPath("$.rol")
                        .value("USUARIO"))
                .andExpect(jsonPath("$.token")
                        .isNotEmpty());

        Usuario usuarioGuardado = usuarioRepository
                .findByCorreoIgnoreCase(correo)
                .orElseThrow(() ->
                        new AssertionError("El usuario no fue guardado"));

        assertNotNull(usuarioGuardado.getId());

        assertEquals(
                correo,
                usuarioGuardado.getCorreo()
        );

        assertEquals(
                "USUARIO",
                usuarioGuardado.getRol().getNombre()
        );

        assertTrue(
                passwordEncoder.matches(
                        "Clave123456",
                        usuarioGuardado.getContrasena()
                ),
                "La contraseña debe estar cifrada correctamente"
        );
    }
}

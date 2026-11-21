
package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Rol;
import com.diccionario.lenguamaterna.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Test
    void findByCorreoIgnoreCase_DebeEncontrarUsuario() {

        Rol rol = new Rol("USUARIO");
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.setNombre("Vanessa Ruiz");
        usuario.setCorreo("vanessa@gmail.com");
        usuario.setContrasena("clave123");
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        var resultado = usuarioRepository
                .findByCorreoIgnoreCase("VANESSA@GMAIL.COM");

        assertTrue(resultado.isPresent());
        assertEquals("vanessa@gmail.com", resultado.get().getCorreo());
    }

    @Test
    void existsByCorreoIgnoreCase_DebeIndicarSiExiste() {

        Rol rol = new Rol("USUARIO");
        rol = rolRepository.save(rol);

        Usuario usuario = new Usuario();
        usuario.setNombre("Vanessa Ruiz");
        usuario.setCorreo("vanessa@gmail.com");
        usuario.setContrasena("clave123");
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        boolean existe = usuarioRepository
                .existsByCorreoIgnoreCase("VANESSA@GMAIL.COM");

        assertTrue(existe);
    }
}

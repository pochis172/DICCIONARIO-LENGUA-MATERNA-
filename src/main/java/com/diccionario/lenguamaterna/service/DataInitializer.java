package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.entity.*;
import com.diccionario.lenguamaterna.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegionRepository regionRepository;
    private final LenguaRepository lenguaRepository;
    private final PalabraRepository palabraRepository;
    private final TraduccionRepository traduccionRepository;
    private final EjemploRepository ejemploRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                           RegionRepository regionRepository, LenguaRepository lenguaRepository,
                           PalabraRepository palabraRepository, TraduccionRepository traduccionRepository,
                           EjemploRepository ejemploRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.regionRepository = regionRepository;
        this.lenguaRepository = lenguaRepository;
        this.palabraRepository = palabraRepository;
        this.traduccionRepository = traduccionRepository;
        this.ejemploRepository = ejemploRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Rol admin = rolRepository.findByNombreIgnoreCase("ADMINISTRADOR").orElseGet(() -> rolRepository.save(new Rol("ADMINISTRADOR")));
        rolRepository.findByNombreIgnoreCase("USUARIO").orElseGet(() -> rolRepository.save(new Rol("USUARIO")));

        if (!usuarioRepository.existsByCorreoIgnoreCase("admin@diccionario.local")) {
            Usuario u = new Usuario();
            u.setNombre("Administrador");
            u.setCorreo("admin@diccionario.local");
            u.setContrasena(passwordEncoder.encode("Admin123*"));
            u.setRol(admin);
            usuarioRepository.save(u);
        }

        Region andina = regionRepository.findByNombreIgnoreCase("Región Andina")
                .orElseGet(() -> regionRepository.save(new Region("Región Andina")));
        Lengua quechua = lenguaRepository.findByNombreIgnoreCase("Quechua").orElseGet(() -> {
            Lengua l = new Lengua();
            l.setNombre("Quechua");
            l.setRegion(andina);
            l.setFamiliaLinguistica("Quechua");
            return lenguaRepository.save(l);
        });

        if (palabraRepository.count() == 0) {
            crearPalabra(quechua, "Agua", "Naturaleza", "Elemento vital presente en ríos, lagos y lluvia.", "Yaku", "El agua es vida.");
            crearPalabra(quechua, "Casa", "Hogar", "Lugar destinado para vivir y compartir en familia.", "Wasi", "La casa está cerca de la montaña.");
            crearPalabra(quechua, "Perro", "Animales", "Animal doméstico que acompaña a las personas.", "Allku", "El perro acompaña a la familia.");
            crearPalabra(quechua, "Sol", "Naturaleza", "Estrella que ilumina y da energía a la Tierra.", "Inti", "El sol ilumina el camino.");
            crearPalabra(quechua, "Montaña", "Naturaleza", "Elevación natural de gran altura sobre el terreno.", "Urku", "La montaña es alta.");
        }
    }

    private void crearPalabra(Lengua lengua, String espanol, String categoria, String significado, String traduccion, String ejemplo) {
        Palabra p = new Palabra();
        p.setLengua(lengua);
        p.setEspanol(espanol);
        p.setCategoria(categoria);
        p.setSignificado(significado);
        palabraRepository.save(p);

        Traduccion t = new Traduccion();
        t.setPalabra(p);
        t.setLengua(lengua);
        t.setTraduccion(traduccion);
        traduccionRepository.save(t);

        Ejemplo e = new Ejemplo();
        e.setPalabra(p);
        e.setEjemplo(ejemplo);
        ejemploRepository.save(e);
    }
}

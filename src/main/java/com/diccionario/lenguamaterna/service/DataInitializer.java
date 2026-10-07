package com.diccionario.lenguamaterna.service;

import com.diccionario.lenguamaterna.entity.*;
import com.diccionario.lenguamaterna.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public DataInitializer(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            RegionRepository regionRepository,
            LenguaRepository lenguaRepository,
            PalabraRepository palabraRepository,
            TraduccionRepository traduccionRepository,
            EjemploRepository ejemploRepository,
            PasswordEncoder passwordEncoder
    ) {
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

        // =====================================================
        // ROLES
        // =====================================================

        Rol superUsuario = rolRepository
                .findByNombreIgnoreCase("SUPER_USUARIO")
                .orElseGet(() ->
                        rolRepository.save(
                                new Rol("SUPER_USUARIO")
                        )
                );

        Rol administrador = rolRepository
                .findByNombreIgnoreCase("ADMINISTRADOR")
                .orElseGet(() ->
                        rolRepository.save(
                                new Rol("ADMINISTRADOR")
                        )
                );

        Rol editor = rolRepository
                .findByNombreIgnoreCase("EDITOR")
                .orElseGet(() ->
                        rolRepository.save(
                                new Rol("EDITOR")
                        )
                );

        Rol usuario = rolRepository
                .findByNombreIgnoreCase("USUARIO")
                .orElseGet(() ->
                        rolRepository.save(
                                new Rol("USUARIO")
                        )
                );


        // =====================================================
        // USUARIOS DEL EQUIPO
        // =====================================================

        // ALEXIS - SUPER_USUARIO
        crearOActualizarUsuario(
                "Alexis",
                "alexis_superusuario@diccionario.local",
                "123456",
                superUsuario
        );

        // KEVIN - ADMINISTRADOR
        crearOActualizarUsuario(
                "Kevin",
                "kevin_administrador@diccionario.local",
                "123456",
                administrador
        );

        // VANESSA - EDITOR
        crearOActualizarUsuario(
                "Vanessa",
                "vanessa_editor@diccionario.local",
                "123456",
                editor
        );

        // KAREN - USUARIO
        crearOActualizarUsuario(
                "Karen",
                "karen_usuario@diccionario.local",
                "123456",
                usuario
        );


        // =====================================================
        // REGIONES
        // =====================================================

        Region andina = regionRepository
                .findByNombreIgnoreCase("Región Andina")
                .orElseGet(() ->
                        regionRepository.save(
                                new Region("Región Andina")
                        )
                );

        Region amazonica = regionRepository
                .findByNombreIgnoreCase("Región Amazónica")
                .orElseGet(() ->
                        regionRepository.save(
                                new Region("Región Amazónica")
                        )
                );

        Region pacifica = regionRepository
                .findByNombreIgnoreCase("Región Pacífica")
                .orElseGet(() ->
                        regionRepository.save(
                                new Region("Región Pacífica")
                        )
                );

        Region caribe = regionRepository
                .findByNombreIgnoreCase("Región Caribe")
                .orElseGet(() ->
                        regionRepository.save(
                                new Region("Región Caribe")
                        )
                );

        Region surAndina = regionRepository
                .findByNombreIgnoreCase("Región Sur Andina")
                .orElseGet(() ->
                        regionRepository.save(
                                new Region("Región Sur Andina")
                        )
                );


        // =====================================================
        // LENGUAS
        // =====================================================

        Lengua quechua = lenguaRepository
                .findByNombreIgnoreCase("Quechua")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("Quechua");
                    lengua.setRegion(andina);
                    lengua.setFamiliaLinguistica("Quechua");

                    return lenguaRepository.save(lengua);
                });


        lenguaRepository
                .findByNombreIgnoreCase("Inga")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("Inga");
                    lengua.setRegion(amazonica);
                    lengua.setFamiliaLinguistica("Quechua");

                    return lenguaRepository.save(lengua);
                });


        lenguaRepository
                .findByNombreIgnoreCase("Kamëntsá")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("Kamëntsá");
                    lengua.setRegion(amazonica);
                    lengua.setFamiliaLinguistica("Kamëntsá");

                    return lenguaRepository.save(lengua);
                });


        lenguaRepository
                .findByNombreIgnoreCase("Siona")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("Siona");
                    lengua.setRegion(amazonica);
                    lengua.setFamiliaLinguistica(
                            "Tucano Occidental"
                    );

                    return lenguaRepository.save(lengua);
                });


        lenguaRepository
                .findByNombreIgnoreCase("A'ingae")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("A'ingae");
                    lengua.setRegion(amazonica);
                    lengua.setFamiliaLinguistica("Aislada");

                    return lenguaRepository.save(lengua);
                });


        lenguaRepository
                .findByNombreIgnoreCase("Emberá")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("Emberá");
                    lengua.setRegion(pacifica);
                    lengua.setFamiliaLinguistica("Chocó");

                    return lenguaRepository.save(lengua);
                });


        lenguaRepository
                .findByNombreIgnoreCase("Wayuunaiki")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("Wayuunaiki");
                    lengua.setRegion(caribe);
                    lengua.setFamiliaLinguistica("Arawak");

                    return lenguaRepository.save(lengua);
                });


        lenguaRepository
                .findByNombreIgnoreCase("Nasa Yuwe")
                .orElseGet(() -> {

                    Lengua lengua = new Lengua();

                    lengua.setNombre("Nasa Yuwe");
                    lengua.setRegion(surAndina);
                    lengua.setFamiliaLinguistica(
                            "Nasa Yuwe"
                    );

                    return lenguaRepository.save(lengua);
                });


        // =====================================================
        // PALABRAS DE PRUEBA
        // =====================================================

        if (palabraRepository.count() == 0) {

            crearPalabra(
                    quechua,
                    "Agua",
                    "Naturaleza",
                    "Elemento vital presente en ríos, lagos y lluvia.",
                    "Yaku",
                    "El agua es vida."
            );

            crearPalabra(
                    quechua,
                    "Casa",
                    "Hogar",
                    "Lugar destinado para vivir y compartir en familia.",
                    "Wasi",
                    "La casa está cerca de la montaña."
            );

            crearPalabra(
                    quechua,
                    "Perro",
                    "Animales",
                    "Animal doméstico que acompaña a las personas.",
                    "Allku",
                    "El perro acompaña a la familia."
            );

            crearPalabra(
                    quechua,
                    "Sol",
                    "Naturaleza",
                    "Estrella que ilumina y da energía a la Tierra.",
                    "Inti",
                    "El sol ilumina el camino."
            );

            crearPalabra(
                    quechua,
                    "Montaña",
                    "Naturaleza",
                    "Elevación natural de gran altura sobre el terreno.",
                    "Urku",
                    "La montaña es alta."
            );
        }
    }


    // =====================================================
    // CREAR O ACTUALIZAR USUARIO
    // =====================================================

    private void crearOActualizarUsuario(
            String nombre,
            String correo,
            String contrasena,
            Rol rol
    ) {

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(correo)
                .orElseGet(Usuario::new);

        usuario.setNombre(nombre);

        usuario.setCorreo(
                correo.trim().toLowerCase()
        );

        usuario.setContrasena(
                passwordEncoder.encode(contrasena)
        );

        usuario.setRol(rol);

        usuarioRepository.save(usuario);
    }


    // =====================================================
    // CREAR PALABRAS DE PRUEBA
    // =====================================================

    private void crearPalabra(
            Lengua lengua,
            String espanol,
            String categoria,
            String significado,
            String traduccion,
            String ejemplo
    ) {

        Palabra palabra = new Palabra();

        palabra.setLengua(lengua);
        palabra.setEspanol(espanol);
        palabra.setCategoria(categoria);
        palabra.setSignificado(significado);

        palabraRepository.save(palabra);


        Traduccion nuevaTraduccion =
                new Traduccion();

        nuevaTraduccion.setPalabra(palabra);
        nuevaTraduccion.setLengua(lengua);
        nuevaTraduccion.setTraduccion(traduccion);

        traduccionRepository.save(
                nuevaTraduccion
        );


        Ejemplo nuevoEjemplo =
                new Ejemplo();

        nuevoEjemplo.setPalabra(palabra);
        nuevoEjemplo.setEjemplo(ejemplo);

        ejemploRepository.save(
                nuevoEjemplo
        );
    }
}
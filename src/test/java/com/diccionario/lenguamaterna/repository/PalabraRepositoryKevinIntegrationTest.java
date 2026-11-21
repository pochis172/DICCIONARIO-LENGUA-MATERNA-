package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Lengua;
import com.diccionario.lenguamaterna.entity.Palabra;
import com.diccionario.lenguamaterna.entity.Region;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PalabraRepositoryKevinIntegrationTest {

    @Autowired
    private PalabraRepository palabraRepository;

    @Autowired
    private LenguaRepository lenguaRepository;

    @Autowired
    private RegionRepository regionRepository;

    // PRUEBA DE INTEGRACION:
    // Repositorio + JPA + base H2: guarda palabras y verifica la busqueda
    // (coincidencia exacta primero, filtro por lengua) y la regla de duplicados.
    @Test
    void guardarYBuscarPalabras_debePersistirYFiltrarCorrectamente() {

        Region region = new Region();
        region.setNombre("Putumayo");
        Region regionGuardada = regionRepository.saveAndFlush(region);

        Lengua inga = nuevaLengua("Inga Kevin", regionGuardada);
        Lengua kamentsa = nuevaLengua("Kamentsa Kevin", regionGuardada);

        palabraRepository.saveAndFlush(nuevaPalabra("agua", "Naturaleza", inga));
        palabraRepository.saveAndFlush(nuevaPalabra("aguacate", "Alimentos", inga));
        palabraRepository.saveAndFlush(nuevaPalabra("agua", "Naturaleza", kamentsa));

        List<Palabra> enInga = palabraRepository.buscar("agua", inga.getId());

        assertEquals(2, enInga.size());
        assertEquals("agua", enInga.get(0).getEspanol());
        assertEquals("aguacate", enInga.get(1).getEspanol());

        assertEquals(3, palabraRepository.buscar("AGUA", null).size());

        assertTrue(palabraRepository
                .existsByEspanolIgnoreCaseAndLengua_Id("AGUA", inga.getId()));
        assertFalse(palabraRepository
                .existsByEspanolIgnoreCaseAndLengua_Id("fuego", inga.getId()));
        assertTrue(palabraRepository.listarCategorias().contains("Alimentos"));
    }

    private Lengua nuevaLengua(String nombre, Region region) {
        Lengua lengua = new Lengua();
        lengua.setNombre(nombre);
        lengua.setRegion(region);
        lengua.setFamiliaLinguistica("Prueba");
        return lenguaRepository.saveAndFlush(lengua);
    }

    private Palabra nuevaPalabra(String espanol, String categoria, Lengua lengua) {
        Palabra palabra = new Palabra();
        palabra.setEspanol(espanol);
        palabra.setCategoria(categoria);
        palabra.setSignificado("Significado de " + espanol);
        palabra.setLengua(lengua);
        return palabra;
    }
}

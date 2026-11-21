
package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Lengua;
import com.diccionario.lenguamaterna.entity.Palabra;
import com.diccionario.lenguamaterna.entity.Region;
import com.diccionario.lenguamaterna.entity.Traduccion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TraduccionRepositoryKarenIntegrationTest {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private LenguaRepository lenguaRepository;

    @Autowired
    private PalabraRepository palabraRepository;

    @Autowired
    private TraduccionRepository traduccionRepository;

    // PRUEBA DE INTEGRACIÓN:
    // Guardar y recuperar una traducción real en H2.
    @Test
    void guardarYConsultarTraduccion_debeFuncionar() {

        Region region = new Region();
        region.setNombre("Putumayo");
        region = regionRepository.saveAndFlush(region);

        Lengua lengua = new Lengua();
        lengua.setNombre("Inga Prueba Karen");
        lengua.setFamiliaLinguistica("Quechua");
        lengua.setRegion(region);
        lengua = lenguaRepository.saveAndFlush(lengua);

        Palabra palabra = new Palabra();
        palabra.setEspanol("Buenos días");
        palabra.setCategoria("Saludo");
        palabra.setSignificado("Saludo de la mañana");
        palabra.setLengua(lengua);
        palabra = palabraRepository.saveAndFlush(palabra);

        Traduccion traduccion = new Traduccion();
        traduccion.setPalabra(palabra);
        traduccion.setLengua(lengua);
        traduccion.setTraduccion("Alli puncha");

        Traduccion guardada = traduccionRepository.saveAndFlush(traduccion);

        List<Traduccion> encontradas = traduccionRepository
                .findByPalabraIdOrderByIdAsc(palabra.getId());

        assertEquals(1, encontradas.size());

        assertEquals(
                guardada.getId(),
                encontradas.get(0).getId());

        assertEquals(
                "Alli puncha",
                encontradas.get(0).getTraduccion());

        assertEquals(
                "Buenos días",
                encontradas.get(0).getPalabra().getEspanol());

        assertEquals(
                "Inga Prueba Karen",
                encontradas.get(0).getLengua().getNombre());
    }
}

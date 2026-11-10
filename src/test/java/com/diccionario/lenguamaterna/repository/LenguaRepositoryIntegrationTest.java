package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Lengua;
import com.diccionario.lenguamaterna.entity.Region;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LenguaRepositoryIntegrationTest {

    @Autowired
    private LenguaRepository lenguaRepository;

    @Autowired
    private RegionRepository regionRepository;

    // PRUEBA DE INTEGRACIÓN 1
    @Test
    void guardarYBuscarLengua_debePersistirCorrectamente() {

        Region region = new Region();
        region.setNombre("Putumayo");

        Region regionGuardada =
                regionRepository.saveAndFlush(region);

        Lengua lengua = new Lengua();
        lengua.setNombre("Inga Integracion");
        lengua.setRegion(regionGuardada);
        lengua.setFamiliaLinguistica("Quechua");

        Lengua lenguaGuardada =
                lenguaRepository.saveAndFlush(lengua);

        Optional<Lengua> resultado =
                lenguaRepository.findById(
                        lenguaGuardada.getId()
                );

        assertTrue(resultado.isPresent());

        assertEquals(
                "Inga Integracion",
                resultado.get().getNombre()
        );

        assertEquals(
                "Putumayo",
                resultado.get().getRegion().getNombre()
        );

        assertEquals(
                "Quechua",
                resultado.get().getFamiliaLinguistica()
        );
    }

    // PRUEBA DE INTEGRACIÓN 2
    @Test
    void buscarPorRegion_debeRetornarSoloLenguasDeLaRegion() {

        Region putumayo = new Region();
        putumayo.setNombre("Putumayo");

        Region cauca = new Region();
        cauca.setNombre("Cauca");

        putumayo = regionRepository.saveAndFlush(putumayo);
        cauca = regionRepository.saveAndFlush(cauca);

        Lengua inga = new Lengua();
        inga.setNombre("Inga Integracion");
        inga.setRegion(putumayo);
        inga.setFamiliaLinguistica("Quechua");

        Lengua nasa = new Lengua();
        nasa.setNombre("Nasa Integracion");
        nasa.setRegion(cauca);
        nasa.setFamiliaLinguistica("Barbacoana");

        lenguaRepository.saveAllAndFlush(
                List.of(inga, nasa)
        );

        List<Lengua> resultado =
                lenguaRepository
                        .findByRegionIdOrderByNombreAsc(
                                putumayo.getId()
                        );

        assertEquals(1, resultado.size());

        assertEquals(
                "Inga Integracion",
                resultado.get(0).getNombre()
        );

        assertEquals(
                "Putumayo",
                resultado.get(0).getRegion().getNombre()
        );
    }
}
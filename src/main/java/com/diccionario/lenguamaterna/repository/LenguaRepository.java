package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Lengua;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LenguaRepository extends JpaRepository<Lengua, Long> {

    // Buscar una lengua por su nombre exacto.
    Optional<Lengua> findByNombreIgnoreCase(String nombre);

    // Validar si ya existe una lengua con determinado nombre.
    boolean existsByNombreIgnoreCase(String nombre);

    // NUEVO: buscar lenguas escribiendo solo una parte del nombre.
    List<Lengua> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    // NUEVO: listar las lenguas pertenecientes a una región.
    List<Lengua> findByRegionIdOrderByNombreAsc(Long regionId);

// NUEVO: buscar lenguas por el nombre de la región.
List<Lengua> findByRegionNombreContainingIgnoreCaseOrderByNombreAsc(
        String nombreRegion
);
    

    // NUEVO: buscar por familia lingüística.
    List<Lengua> findByFamiliaLinguisticaContainingIgnoreCaseOrderByNombreAsc(
            String familiaLinguistica
    );
}

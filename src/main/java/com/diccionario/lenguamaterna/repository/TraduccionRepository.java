package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Traduccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraduccionRepository extends JpaRepository<Traduccion, Long> {

    // Listar traducciones asociadas a una palabra
    List<Traduccion> findByPalabraIdOrderByIdAsc(Long palabraId);

    // NUEVO: listar traducciones por lengua
    List<Traduccion> findByLenguaIdOrderByIdAsc(Long lenguaId);

    // NUEVO: buscar por palabra en español o por traducción
    List<Traduccion>
    findByPalabra_EspanolContainingIgnoreCaseOrTraduccionContainingIgnoreCaseOrderByIdAsc(
            String palabra,
            String traduccion
    );

    // NUEVO: comprobar si ya existe una traducción igual
    boolean existsByPalabraIdAndLenguaIdAndTraduccionIgnoreCase(
            Long palabraId,
            Long lenguaId,
            String traduccion
    );

    // NUEVO: comprobar duplicados cuando se está actualizando
    boolean existsByPalabraIdAndLenguaIdAndTraduccionIgnoreCaseAndIdNot(
            Long palabraId,
            Long lenguaId,
            String traduccion,
            Long id
    );

    // Se utiliza cuando se elimina una palabra
    void deleteByPalabraId(Long palabraId);

    // Cuenta cuántas traducciones están asociadas a una lengua
    long countByLenguaId(Long lenguaId);
}

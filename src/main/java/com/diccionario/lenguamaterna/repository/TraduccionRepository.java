package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Traduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TraduccionRepository extends JpaRepository<Traduccion, Long> {
    List<Traduccion> findByPalabraIdOrderByIdAsc(Long palabraId);
    void deleteByPalabraId(Long palabraId);
}

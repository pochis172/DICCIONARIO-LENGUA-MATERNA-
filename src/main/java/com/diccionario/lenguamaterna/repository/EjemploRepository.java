package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Ejemplo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EjemploRepository extends JpaRepository<Ejemplo, Long> {
    List<Ejemplo> findByPalabraIdOrderByIdAsc(Long palabraId);
    void deleteByPalabraId(Long palabraId);
}

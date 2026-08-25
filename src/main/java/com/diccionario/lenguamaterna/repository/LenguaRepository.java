package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Lengua;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LenguaRepository extends JpaRepository<Lengua, Long> {
    Optional<Lengua> findByNombreIgnoreCase(String nombre);
}

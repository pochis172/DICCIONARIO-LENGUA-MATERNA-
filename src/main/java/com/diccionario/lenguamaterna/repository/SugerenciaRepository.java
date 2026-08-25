package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Sugerencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SugerenciaRepository extends JpaRepository<Sugerencia, Long> {
    List<Sugerencia> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    List<Sugerencia> findAllByOrderByFechaCreacionDesc();
}

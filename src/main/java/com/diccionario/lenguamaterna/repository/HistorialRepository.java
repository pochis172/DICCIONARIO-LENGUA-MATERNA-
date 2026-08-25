package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialRepository extends JpaRepository<Historial, Long> {
    List<Historial> findTop50ByUsuarioIdOrderByFechaDesc(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
    void deleteByPalabraId(Long palabraId);
}

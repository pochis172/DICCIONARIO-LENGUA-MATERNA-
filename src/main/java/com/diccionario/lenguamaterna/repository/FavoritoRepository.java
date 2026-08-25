package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioIdOrderByIdDesc(Long usuarioId);
    Optional<Favorito> findByUsuarioIdAndPalabraId(Long usuarioId, Long palabraId);
    boolean existsByUsuarioIdAndPalabraId(Long usuarioId, Long palabraId);
    void deleteByPalabraId(Long palabraId);
}

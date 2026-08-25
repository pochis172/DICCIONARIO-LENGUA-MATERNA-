package com.diccionario.lenguamaterna.repository;

import com.diccionario.lenguamaterna.entity.Palabra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PalabraRepository extends JpaRepository<Palabra, Long> {
    List<Palabra> findTop12ByOrderByIdDesc();
    List<Palabra> findAllByOrderByEspanolAsc();
    List<Palabra> findByEspanolContainingIgnoreCaseOrderByEspanolAsc(String espanol);
    List<Palabra> findByCategoriaIgnoreCaseOrderByEspanolAsc(String categoria);

    @Query("""
        select p from Palabra p
        where lower(p.espanol) like lower(concat('%', :q, '%'))
          and (:lenguaId is null or p.lengua.id = :lenguaId)
        order by case when lower(p.espanol) = lower(:q) then 0 else 1 end, p.espanol
        """)
    List<Palabra> buscar(@Param("q") String q, @Param("lenguaId") Long lenguaId);

    @Query("select distinct p.categoria from Palabra p order by p.categoria")
    List<String> listarCategorias();
}

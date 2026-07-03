package com.metalSpring.repository;

import com.metalSpring.model.entity.ComentarioPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioPerfilRepository extends JpaRepository<ComentarioPerfil, String> {
    List<ComentarioPerfil> findByAlvoIdOrderByDataDesc(String alvoId);
    List<ComentarioPerfil> findByAutorIdOrderByDataDesc(String autorId);

    @Query("SELECT AVG(c.nota) FROM ComentarioPerfil c WHERE c.alvo.id = :alvoId AND c.nota IS NOT NULL")
    Double calcularMediaPorAlvo(@Param("alvoId") String alvoId);

    long countByAlvoId(String alvoId);
}

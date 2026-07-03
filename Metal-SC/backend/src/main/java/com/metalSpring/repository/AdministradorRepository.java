package com.metalSpring.repository;

import com.metalSpring.model.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, String> {
    Optional<Administrador> findByEmail(String email);
    List<Administrador> findByNomeContainingIgnoreCase(String nome);
    List<Administrador> findTop10ByOrderByDataCadastroDesc();
    List<Administrador> findByDataCadastroAfter(LocalDateTime data);

    @Query("SELECT COUNT(a) FROM Administrador a")
    long contarTotalAdministradores();

    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
package com.metalSpring.repository;

import com.metalSpring.model.entity.Revendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RevendedorRepository extends JpaRepository<Revendedor, String> {
    Optional<Revendedor> findByCnpj(String cnpj);
    Optional<Revendedor> findByEmail(String email);
    List<Revendedor> findByNomeLojaContainingIgnoreCase(String nomeLoja);
    List<Revendedor> findByAvaliacaoMediaGreaterThanEqual(double avaliacao);
    List<Revendedor> findByAvaliacaoMediaBetween(double min, double max);

    @Query("SELECT r FROM Revendedor r WHERE r.endereco.cidade = :cidade")
    List<Revendedor> findByCidade(String cidade);

    @Query("SELECT r FROM Revendedor r WHERE r.endereco.estado = :estado")
    List<Revendedor> findByEstado(String estado);

    List<Revendedor> findTop10ByOrderByAvaliacaoMediaDesc();

    @Query("SELECT r FROM Revendedor r WHERE r.avaliacaoMedia >= 4.0")
    List<Revendedor> findRevendedoresBemAvaliados();

    List<Revendedor> findAllByOrderByAvaliacaoMediaDesc();
    boolean existsByCnpj(String cnpj);

    @Query("SELECT COUNT(r) FROM Revendedor r WHERE r.endereco.cidade = :cidade")
    long countByCidade(String cidade);

    @Query("SELECT COUNT(r) FROM Revendedor r WHERE r.endereco.estado = :estado")
    long countByEstado(String estado);

    List<Revendedor> findByAvaliacaoMedia(double avaliacao);
    void deleteByCnpj(String cnpj);

    @Query("SELECT r FROM Revendedor r ORDER BY r.dataCadastro DESC")
    List<Revendedor> findRecentesCadastrados();
}
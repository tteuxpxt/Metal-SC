package com.metalSpring.repository;

import com.metalSpring.model.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, String> {
    List<Avaliacao> findByClienteId(String clienteId);
    List<Avaliacao> findByVendedorId(String vendedorId);
    List<Avaliacao> findByPecaId(String pecaId);
    List<Avaliacao> findByNota(int nota);
    List<Avaliacao> findByNotaGreaterThanEqual(int nota);
    List<Avaliacao> findByNotaLessThanEqual(int nota);
    List<Avaliacao> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Avaliacao> findTop10ByOrderByDataDesc();
    List<Avaliacao> findByVendedorIdOrderByDataDesc(String vendedorId);
    List<Avaliacao> findByPecaIdOrderByNotaDesc(String pecaId);

    @Query("SELECT a FROM Avaliacao a WHERE a.nota = 5")
    List<Avaliacao> findAvaliacoesExcelentes();

    @Query("SELECT a FROM Avaliacao a WHERE a.nota = 1")
    List<Avaliacao> findAvaliacoesPessimas();

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.vendedor.id = :vendedorId")
    Double calcularMediaPorRevendedor(@Param("vendedorId") String vendedorId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.peca.id = :pecaId")
    Double calcularMediaPorPeca(@Param("pecaId") String pecaId);

    long countByVendedorId(String vendedorId);
    long countByPecaId(String pecaId);
    long countByNota(int nota);

    @Query("SELECT a FROM Avaliacao a WHERE a.comentario IS NOT NULL AND a.comentario != ''")
    List<Avaliacao> findAvaliacoesComComentario();

    @Query("SELECT a FROM Avaliacao a WHERE a.comentario IS NULL OR a.comentario = ''")
    List<Avaliacao> findAvaliacoesSemComentario();

    List<Avaliacao> findByVendedorIdAndNota(String vendedorId, int nota);
    void deleteByClienteId(String clienteId);
    void deleteByVendedorId(String vendedorId);
    void deleteByPecaId(String pecaId);
}
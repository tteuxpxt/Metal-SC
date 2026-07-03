package com.metalSpring.repository;

import com.metalSpring.model.entity.Transacao;
import com.metalSpring.model.enums.MetodoPagamento;
import com.metalSpring.model.enums.TransacaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, String> {
    Optional<Transacao> findByPedidoId(String pedidoId);
    List<Transacao> findByStatus(TransacaoStatus status);
    List<Transacao> findByMetodo(MetodoPagamento metodo);
    Optional<Transacao> findByReferencia(String referencia);
    List<Transacao> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Transacao> findByStatusAndMetodo(TransacaoStatus status, MetodoPagamento metodo);
    List<Transacao> findTop10ByOrderByDataDesc();

    @Query("SELECT t FROM Transacao t WHERE t.status = 'CONFIRMADA'")
    List<Transacao> findTransacoesConfirmadas();

    @Query("SELECT t FROM Transacao t WHERE t.status = 'PENDENTE'")
    List<Transacao> findTransacoesPendentes();

    @Query("SELECT t FROM Transacao t WHERE t.status = 'RECUSADA'")
    List<Transacao> findTransacoesRecusadas();

    @Query("SELECT t FROM Transacao t WHERE t.status = 'ESTORNADA'")
    List<Transacao> findTransacoesEstornadas();

    long countByStatus(TransacaoStatus status);
    long countByMetodo(MetodoPagamento metodo);

    @Query("SELECT SUM(t.pedido.valorTotal) FROM Transacao t WHERE t.status = 'CONFIRMADA'")
    Double somarTransacoesConfirmadas();

    @Query("SELECT SUM(t.pedido.valorTotal) FROM Transacao t WHERE t.metodo = :metodo AND t.status = 'CONFIRMADA'")
    Double somarPorMetodoPagamento(@Param("metodo") MetodoPagamento metodo);

    @Query("SELECT t FROM Transacao t WHERE t.data BETWEEN :inicio AND :fim AND t.status = :status")
    List<Transacao> findByPeriodoEStatus(@Param("inicio") LocalDateTime inicio,
                                         @Param("fim") LocalDateTime fim,
                                         @Param("status") TransacaoStatus status);

    boolean existsByPedidoId(String pedidoId);
    void deleteByPedidoId(String pedidoId);
}
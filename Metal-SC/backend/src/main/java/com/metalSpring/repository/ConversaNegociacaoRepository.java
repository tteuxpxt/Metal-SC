package com.metalSpring.repository;

import com.metalSpring.model.entity.ConversaNegociacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversaNegociacaoRepository extends JpaRepository<ConversaNegociacao, String> {
    Optional<ConversaNegociacao> findByPecaIdAndClienteId(String pecaId, String clienteId);
    Optional<ConversaNegociacao> findByPedidoId(String pedidoId);
    List<ConversaNegociacao> findByClienteIdOrderByAtualizadaEmDesc(String clienteId);
    List<ConversaNegociacao> findByRevendedorIdOrderByAtualizadaEmDesc(String revendedorId);
}

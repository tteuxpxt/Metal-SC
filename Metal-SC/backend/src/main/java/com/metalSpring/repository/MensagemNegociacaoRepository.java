package com.metalSpring.repository;

import com.metalSpring.model.entity.MensagemNegociacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensagemNegociacaoRepository extends JpaRepository<MensagemNegociacao, String> {
    List<MensagemNegociacao> findByConversaIdOrderByDataEnvioAsc(String conversaId);
    long countByConversaIdAndDestinatarioIdAndLidaFalseAndRemovidaFalse(String conversaId, String destinatarioId);
    List<MensagemNegociacao> findByConversaIdAndDestinatarioIdAndLidaFalse(String conversaId, String destinatarioId);
    long countByDestinatarioIdAndLidaFalseAndRemovidaFalse(String destinatarioId);
    List<MensagemNegociacao> findByDestinatarioIdAndLidaFalseAndRemovidaFalseOrderByDataEnvioDesc(String destinatarioId);
}

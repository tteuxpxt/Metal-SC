package com.metalSpring.repository;

import com.metalSpring.model.entity.AlertaModeracao;
import com.metalSpring.model.enums.AlertaModeracaoStatus;
import com.metalSpring.model.enums.UsuarioTipo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaModeracaoRepository extends JpaRepository<AlertaModeracao, String> {
    List<AlertaModeracao> findAllByOrderByDataHoraDesc();
    List<AlertaModeracao> findByUsuarioIdOrderByDataHoraDesc(String usuarioId);
    List<AlertaModeracao> findByConversaId(String conversaId);
    List<AlertaModeracao> findByMensagem_ConversaId(String conversaId);
    List<AlertaModeracao> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);
    List<AlertaModeracao> findByTipoInfracaoContainingIgnoreCaseOrderByDataHoraDesc(String tipoInfracao);
    List<AlertaModeracao> findByStatusOrderByDataHoraDesc(AlertaModeracaoStatus status);
    long countByStatus(AlertaModeracaoStatus status);
    long countByUsuarioId(String usuarioId);
    long countByUsuarioTipo(UsuarioTipo usuarioTipo);
}

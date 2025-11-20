package com.metalSpring.repository;

import com.metalSpring.model.entity.MensagemSuporte;
import com.metalSpring.model.enums.CanalSuporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MensagemSuporteRepository extends JpaRepository<MensagemSuporte, String> {
    List<MensagemSuporte> findByUsuarioId(String usuarioId);
    List<MensagemSuporte> findByLida(boolean lida);
    List<MensagemSuporte> findByCanal(CanalSuporte canal);
    List<MensagemSuporte> findByAtendenteResponsavel(String atendenteId);
    List<MensagemSuporte> findByDataEnvioBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT m FROM MensagemSuporte m WHERE m.lida = false")
    List<MensagemSuporte> findMensagensNaoLidas();

    @Query("SELECT m FROM MensagemSuporte m WHERE m.lida = true")
    List<MensagemSuporte> findMensagensLidas();

    List<MensagemSuporte> findTop10ByOrderByDataEnvioDesc();
    List<MensagemSuporte> findByUsuarioIdOrderByDataEnvioDesc(String usuarioId);

    @Query("SELECT m FROM MensagemSuporte m WHERE m.atendenteResponsavel IS NULL")
    List<MensagemSuporte> findMensagensSemAtendente();

    @Query("SELECT m FROM MensagemSuporte m WHERE m.atendenteResponsavel IS NOT NULL")
    List<MensagemSuporte> findMensagensComAtendente();

    long countByLida(boolean lida);
    long countByCanal(CanalSuporte canal);
    long countByUsuarioId(String usuarioId);
    long countByAtendenteResponsavel(String atendenteId);

    @Query("SELECT m FROM MensagemSuporte m WHERE m.lida = false AND m.canal = :canal")
    List<MensagemSuporte> findMensagensNaoLidasPorCanal(CanalSuporte canal);

    @Query("SELECT m FROM MensagemSuporte m WHERE m.lida = false AND m.atendenteResponsavel IS NULL ORDER BY m.dataEnvio ASC")
    List<MensagemSuporte> findMensagensUrgentes();

    @Query("SELECT m FROM MensagemSuporte m WHERE m.lida = false AND m.dataEnvio < :dataLimite")
    List<MensagemSuporte> findMensagensAntigasNaoAtendidas(LocalDateTime dataLimite);

    void deleteByUsuarioId(String usuarioId);
    void deleteByLida(boolean lida);
}
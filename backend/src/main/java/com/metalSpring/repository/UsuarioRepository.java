package com.metalSpring.repository;

import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.enums.UsuarioTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
    List<Usuario> findByTipo(UsuarioTipo tipo);
    Optional<Usuario> findByTelefone(String telefone);

    @Query("SELECT u FROM Usuario u WHERE u.endereco.cidade = :cidade")
    List<Usuario> findByCidade(String cidade);

    @Query("SELECT u FROM Usuario u WHERE u.endereco.estado = :estado")
    List<Usuario> findByEstado(String estado);

    List<Usuario> findByDataCadastroBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Usuario> findByDataCadastroAfter(LocalDateTime data);
    List<Usuario> findByDataCadastroBefore(LocalDateTime data);
    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);
    long countByTipo(UsuarioTipo tipo);
    List<Usuario> findTop10ByOrderByDataCadastroDesc();
    Optional<Usuario> findByEmailAndTipo(String email, UsuarioTipo tipo);

    @Query("SELECT COUNT(u) FROM Usuario u")
    long contarTotalUsuarios();

    void deleteByEmail(String email);
}
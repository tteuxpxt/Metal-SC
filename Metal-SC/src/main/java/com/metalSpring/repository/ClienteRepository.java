package com.metalSpring.repository;

import com.metalSpring.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT c FROM Cliente c WHERE SIZE(c.pedidos) > 0 ORDER BY SIZE(c.pedidos) DESC")
    List<Cliente> findClientesMaisAtivos();

    @Query("SELECT c FROM Cliente c WHERE c.endereco.cidade = :cidade")
    List<Cliente> findByCidade(String cidade);

    long countByEndereco_Estado(String estado);
}
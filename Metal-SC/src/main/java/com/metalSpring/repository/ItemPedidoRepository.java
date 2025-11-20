package com.metalSpring.repository;

import com.metalSpring.model.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, String> {
    List<ItemPedido> findByPecaId(String pecaId);
    List<ItemPedido> findByPedidoId(String pedidoId);
    List<ItemPedido> findByQuantidadeGreaterThanEqual(int quantidade);
    List<ItemPedido> findByPrecoUnitarioBetween(double min, double max);

    @Query("SELECT i FROM ItemPedido i ORDER BY i.quantidade DESC")
    List<ItemPedido> findItensMaisVendidos();

    @Query("SELECT SUM(i.quantidade) FROM ItemPedido i WHERE i.peca.id = :pecaId")
    Long contarQuantidadeVendidaPorPeca(@Param("pecaId") String pecaId);

    @Query("SELECT SUM(i.quantidade * i.precoUnitario) FROM ItemPedido i WHERE i.peca.id = :pecaId")
    Double calcularValorTotalVendidoPorPeca(@Param("pecaId") String pecaId);

    void deleteByPecaId(String pecaId);
}
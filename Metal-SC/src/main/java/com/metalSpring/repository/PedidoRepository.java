package com.metalSpring.repository;

import com.metalSpring.model.entity.Pedido;
import com.metalSpring.model.enums.PedidoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {
    List<Pedido> findByClienteId(String clienteId);
    List<Pedido> findByVendedorId(String vendedorId);
    List<Pedido> findByStatus(PedidoStatus status);
    List<Pedido> findByClienteIdAndStatus(String clienteId, PedidoStatus status);
    List<Pedido> findByVendedorIdAndStatus(String vendedorId, PedidoStatus status);
    List<Pedido> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Pedido> findByValorTotalGreaterThanEqual(double valor);
    List<Pedido> findByValorTotalBetween(double min, double max);
    List<Pedido> findByClienteIdOrderByDataCriacaoDesc(String clienteId);
    List<Pedido> findByVendedorIdOrderByDataCriacaoDesc(String vendedorId);
    List<Pedido> findTop10ByOrderByDataCriacaoDesc();

    @Query("SELECT p FROM Pedido p WHERE p.status = 'PENDENTE'")
    List<Pedido> findPedidosPendentes();

    @Query("SELECT p FROM Pedido p WHERE p.status = 'CONFIRMADO'")
    List<Pedido> findPedidosConfirmados();

    @Query("SELECT p FROM Pedido p WHERE p.status = 'CANCELADO'")
    List<Pedido> findPedidosCancelados();

    long countByStatus(PedidoStatus status);
    long countByClienteId(String clienteId);
    long countByVendedorId(String vendedorId);

    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.vendedor.id = :vendedorId AND p.status = 'CONFIRMADO'")
    Double somarVendasPorRevendedor(@Param("vendedorId") String vendedorId);

    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.cliente.id = :clienteId AND p.status = 'CONFIRMADO'")
    Double somarComprasPorCliente(@Param("clienteId") String clienteId);

    @Query("SELECT p FROM Pedido p WHERE p.enderecoEntrega.cidade = :cidade")
    List<Pedido> findByEnderecoEntregaCidade(@Param("cidade") String cidade);

    @Query("SELECT p FROM Pedido p WHERE p.enderecoEntrega.estado = :estado")
    List<Pedido> findByEnderecoEntregaEstado(@Param("estado") String estado);

    @Query("SELECT AVG(p.valorTotal) FROM Pedido p WHERE p.status = 'CONFIRMADO'")
    Double calcularValorMedioPedidos();

    void deleteByClienteId(String clienteId);
    void deleteByVendedorId(String vendedorId);
}
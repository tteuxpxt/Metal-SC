package com.metalSpring.repository;

import com.metalSpring.model.entity.Peca;
import com.metalSpring.model.enums.PecaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PecaRepository extends JpaRepository<Peca, String> {

    @Query("SELECT p FROM Peca p LEFT JOIN p.vendedor v ORDER BY v.premiumAtivo DESC, p.id DESC")
    List<Peca> findAllOrderByPremium();

    
    List<Peca> findByNomeContainingIgnoreCase(String nome);

    
    List<Peca> findByMarcaIgnoreCase(String marca);

    
    List<Peca> findByCategoriaIgnoreCase(String categoria);

    
    List<Peca> findByModeloVeiculoContainingIgnoreCase(String modeloVeiculo);

    
    List<Peca> findByAno(int ano);

    
    List<Peca> findByAnoBetween(int anoInicio, int anoFim);

    
    List<Peca> findByEstado(PecaEstado estado);

    
    List<Peca> findByVendedorId(String vendedorId);

    
    List<Peca> findByEnderecoCidadeIgnoreCase(String cidade);
    List<Peca> findByEnderecoEstadoIgnoreCase(String estado);
    List<Peca> findByEnderecoCidadeIgnoreCaseAndEnderecoEstadoIgnoreCase(String cidade, String estado);

    
    List<Peca> findByPrecoBetween(double precoMin, double precoMax);

    
    List<Peca> findByEstoqueGreaterThan(int estoque);

    
    List<Peca> findByPrecoLessThanEqual(double preco);

    
    List<Peca> findByPrecoGreaterThanEqual(double preco);

    
    List<Peca> findByCategoriaIgnoreCaseAndMarcaIgnoreCase(String categoria, String marca);

    
    List<Peca> findByCategoriaIgnoreCaseAndEstoqueGreaterThan(String categoria, int estoque);

    
    List<Peca> findByVendedorIdAndEstoqueGreaterThan(String vendedorId, int estoque);

    
    @Query("SELECT p FROM Peca p WHERE " +
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:marca IS NULL OR LOWER(p.marca) = LOWER(:marca)) AND " +
            "(:categoria IS NULL OR LOWER(p.categoria) = LOWER(:categoria)) AND " +
            "(:anoMin IS NULL OR p.ano >= :anoMin) AND " +
            "(:anoMax IS NULL OR p.ano <= :anoMax) AND " +
            "(:precoMin IS NULL OR p.preco >= :precoMin) AND " +
            "(:precoMax IS NULL OR p.preco <= :precoMax) AND " +
            "(:estado IS NULL OR p.estado = :estado)")
    List<Peca> buscarAvancada(@Param("nome") String nome,
                              @Param("marca") String marca,
                              @Param("categoria") String categoria,
                              @Param("anoMin") Integer anoMin,
                              @Param("anoMax") Integer anoMax,
                              @Param("precoMin") Double precoMin,
                              @Param("precoMax") Double precoMax,
                              @Param("estado") PecaEstado estado);

    
    long countByCategoria(String categoria);

    
    long countByMarca(String marca);

    
    long countByVendedorId(String vendedorId);

    
    List<Peca> findTop10ByOrderByPrecoDesc();

    
    List<Peca> findTop10ByOrderByPrecoAsc();

    
    @Query("SELECT p FROM Peca p ORDER BY p.id DESC")
    List<Peca> findRecentes();

    
    boolean existsByNomeIgnoreCase(String nome);

    
    void deleteByVendedorId(String vendedorId);
}

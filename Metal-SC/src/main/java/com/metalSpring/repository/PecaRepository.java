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

    // Busca por nome
    List<Peca> findByNomeContainingIgnoreCase(String nome);

    // Busca por marca
    List<Peca> findByMarcaIgnoreCase(String marca);

    // Busca por categoria
    List<Peca> findByCategoriaIgnoreCase(String categoria);

    // Busca por modelo do veículo
    List<Peca> findByModeloVeiculoContainingIgnoreCase(String modeloVeiculo);

    // Busca por ano
    List<Peca> findByAno(int ano);

    // Busca por faixa de ano
    List<Peca> findByAnoBetween(int anoInicio, int anoFim);

    // Busca por estado
    List<Peca> findByEstado(PecaEstado estado);

    // Busca por revendedor
    List<Peca> findByVendedorId(String vendedorId);

    // Busca por faixa de preço
    List<Peca> findByPrecoBetween(double precoMin, double precoMax);

    // Busca peças disponíveis (estoque > 0)
    List<Peca> findByEstoqueGreaterThan(int estoque);

    // Busca por preço menor que
    List<Peca> findByPrecoLessThanEqual(double preco);

    // Busca por preço maior que
    List<Peca> findByPrecoGreaterThanEqual(double preco);

    // Busca combinada: categoria e marca
    List<Peca> findByCategoriaIgnoreCaseAndMarcaIgnoreCase(String categoria, String marca);

    // Busca combinada: categoria e disponibilidade
    List<Peca> findByCategoriaIgnoreCaseAndEstoqueGreaterThan(String categoria, int estoque);

    // Busca por revendedor e disponibilidade
    List<Peca> findByVendedorIdAndEstoqueGreaterThan(String vendedorId, int estoque);

    // Query customizada: busca avançada
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

    // Contar peças por categoria
    long countByCategoria(String categoria);

    // Contar peças por marca
    long countByMarca(String marca);

    // Contar peças por revendedor
    long countByVendedorId(String vendedorId);

    // Peças mais caras (top 10)
    List<Peca> findTop10ByOrderByPrecoDesc();

    // Peças mais baratas (top 10)
    List<Peca> findTop10ByOrderByPrecoAsc();

    // Peças recém adicionadas
    @Query("SELECT p FROM Peca p ORDER BY p.id DESC")
    List<Peca> findRecentes();

    // Verificar se existe peça com nome específico
    boolean existsByNomeIgnoreCase(String nome);

    // Deletar por revendedor
    void deleteByVendedorId(String vendedorId);
}
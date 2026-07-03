package com.metalSpring.services;

import com.metalSpring.model.entity.Peca;
import com.metalSpring.repository.PecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private PecaRepository pecaRepository;

    public List<Peca> buscarPorNome(String nome) {
        return pecaRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Peca> buscarPorMarca(String marca) {
        return pecaRepository.findByMarcaIgnoreCase(marca);
    }

    public List<Peca> buscarPorCategoria(String categoria) {
        return pecaRepository.findByCategoriaIgnoreCase(categoria);
    }

    public List<Peca> buscarPorModeloVeiculo(String modeloVeiculo) {
        return pecaRepository.findByModeloVeiculoContainingIgnoreCase(modeloVeiculo);
    }

    public List<Peca> buscarPorAno(int ano) {
        return pecaRepository.findByAno(ano);
    }

    public List<Peca> buscarPorFaixaPreco(double precoMin, double precoMax) {
        return pecaRepository.findByPrecoBetween(precoMin, precoMax);
    }

    public List<Peca> buscarAvancada(String nome, String marca, String categoria,
                                     String modeloVeiculo, Integer ano,
                                     Double precoMin, Double precoMax) {
        List<Peca> resultados = pecaRepository.findAll();

        if (nome != null && !nome.isEmpty()) {
            resultados = resultados.stream()
                    .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (marca != null && !marca.isEmpty()) {
            resultados = resultados.stream()
                    .filter(p -> p.getMarca().equalsIgnoreCase(marca))
                    .collect(Collectors.toList());
        }

        if (categoria != null && !categoria.isEmpty()) {
            resultados = resultados.stream()
                    .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        if (modeloVeiculo != null && !modeloVeiculo.isEmpty()) {
            resultados = resultados.stream()
                    .filter(p -> p.getModeloVeiculo().toLowerCase().contains(modeloVeiculo.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (ano != null) {
            resultados = resultados.stream()
                    .filter(p -> p.getAno() == ano)
                    .collect(Collectors.toList());
        }

        if (precoMin != null && precoMax != null) {
            resultados = resultados.stream()
                    .filter(p -> p.getPreco() >= precoMin && p.getPreco() <= precoMax)
                    .collect(Collectors.toList());
        }

        return resultados;
    }

    public List<Peca> buscarDisponiveis() {
        return pecaRepository.findByEstoqueGreaterThan(0);
    }

    public List<Peca> buscarPorRevendedor(String revendedorId) {
        return pecaRepository.findByVendedorId(revendedorId);
    }

    public List<String> listarMarcas() {
        return pecaRepository.findAll().stream()
                .map(Peca::getMarca)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> listarCategorias() {
        return pecaRepository.findAll().stream()
                .map(Peca::getCategoria)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Integer> listarAnos() {
        return pecaRepository.findAll().stream()
                .map(Peca::getAno)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
package com.metalSpring.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigrationConfig {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ajustarColunasDeStatus() {
        executar("ALTER TABLE pedidos MODIFY COLUMN status VARCHAR(50) NOT NULL");
        executar("ALTER TABLE pedidos MODIFY COLUMN status_pagamento VARCHAR(50) NOT NULL");
        executar("ALTER TABLE conversas_negociacao MODIFY COLUMN status VARCHAR(50) NOT NULL");
        executar("ALTER TABLE mensagens_negociacao MODIFY COLUMN status_negociacao VARCHAR(50) NULL");
        executar("ALTER TABLE mensagens_negociacao MODIFY COLUMN tipo VARCHAR(50) NOT NULL");
        executar("ALTER TABLE alertas_moderacao MODIFY COLUMN mensagem_id VARCHAR(255) NULL");
        executar("ALTER TABLE alertas_moderacao ADD COLUMN peca_id VARCHAR(255) NULL");
        executar("ALTER TABLE alertas_moderacao ADD COLUMN peca_nome VARCHAR(255) NULL");
        executar("ALTER TABLE alertas_moderacao ADD COLUMN imagem_url VARCHAR(1000) NULL");
        executar("ALTER TABLE alertas_moderacao ADD COLUMN conversa_id VARCHAR(255) NULL");
        executar("ALTER TABLE alertas_moderacao ADD COLUMN denuncia_manual BIT(1) NULL");
        executar("ALTER TABLE conversas_negociacao ADD COLUMN oculta_para_cliente BIT(1) NOT NULL DEFAULT 0");
        executar("ALTER TABLE conversas_negociacao ADD COLUMN oculta_para_revendedor BIT(1) NOT NULL DEFAULT 0");
    }

    @PostConstruct
    public void ajustarCategoriasDePecas() {
        // Migra as categorias antigas (em maiusculo/sem acento) para o novo
        // formato exibido na interface. Compara ignorando espacos e
        // maiusculas/minusculas para pegar variacoes que possam ter sido
        // gravadas manualmente.
        atualizarCategoria("MOTOR", "Motor");
        atualizarCategoria("SUSPENSAO", "Suspensão");
        atualizarCategoria("FREIOS", "Freios");
        atualizarCategoria("ELETRICA", "Elétrica");
        atualizarCategoria("CARROCERIA", "Carroceria e Lataria");
        atualizarCategoria("TRANSMISSAO", "Câmbio e Transmissão");
    }

    private void atualizarCategoria(String valorAntigo, String valorNovo) {
        executar(
            "UPDATE pecas SET categoria = ? WHERE UPPER(TRIM(categoria)) = ? AND categoria <> ?",
            valorNovo, valorAntigo.toUpperCase(), valorNovo
        );
    }

    private void executar(String sql, Object... args) {
        try {
            if (args.length == 0) {
                jdbcTemplate.execute(sql);
            } else {
                jdbcTemplate.update(sql, args);
            }
        } catch (RuntimeException ignored) {
        }
    }
}

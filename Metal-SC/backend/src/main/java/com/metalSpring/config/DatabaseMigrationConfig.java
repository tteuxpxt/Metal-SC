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
    }

    private void executar(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (RuntimeException ignored) {
        }
    }
}

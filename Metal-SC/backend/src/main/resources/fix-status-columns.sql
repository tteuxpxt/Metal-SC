ALTER TABLE pedidos
    MODIFY COLUMN status VARCHAR(50) NOT NULL,
    MODIFY COLUMN status_pagamento VARCHAR(50) NOT NULL;

ALTER TABLE conversas_negociacao
    MODIFY COLUMN status VARCHAR(50) NOT NULL;

ALTER TABLE mensagens_negociacao
    MODIFY COLUMN status_negociacao VARCHAR(50) NULL,
    MODIFY COLUMN tipo VARCHAR(50) NOT NULL;

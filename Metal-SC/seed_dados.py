"""
Script para popular o banco de dados do Metal-SC via API REST.

Por que via API e não SQL direto?
- As senhas são salvas com BCrypt (hash), então um INSERT manual não
  geraria uma senha válida para login.
- A API já cuida de gerar os UUIDs e vincular Peça -> Revendedor e
  Avaliação -> Cliente/Revendedor/Peça corretamente, evitando erros de FK.

Como usar:
1. Suba o backend normalmente (mvnw spring-boot:run).
2. Instale a lib requests, se ainda não tiver:
       pip install requests
3. Rode este script:
       python seed_dados.py

O script é seguro para rodar mais de uma vez: revendedores e clientes
que já existem são reaproveitados (busca por CNPJ/email) em vez de
gerar erro.

Se o backend estiver rodando em outra porta, ajuste BASE_URL abaixo.
"""

import requests

BASE_URL = "http://localhost:8080/api"

# Senha usada para todos os usuários de teste (fácil de lembrar no vídeo)
SENHA_PADRAO = "senha123"

# ---------------------------------------------------------------------------
# 1. Revendedores (lojas de autopeças)
# ---------------------------------------------------------------------------
REVENDEDORES = [
    {
        "nome": "Carlos Mendes",
        "email": "carlos@autopecasmendes.com.br",
        "senha": SENHA_PADRAO,
        "telefone": "47999110022",
        "cnpj": "12345678000190",
        "nomeLoja": "Auto Peças Mendes",
        "endereco": {
            "rua": "Rua Blumenau", "numero": "450", "complemento": "",
            "bairro": "Centro", "cidade": "Joinville", "estado": "SC", "cep": "89201-000"
        },
    },
    {
        "nome": "Fernanda Souza",
        "email": "fernanda@turbopecas.com.br",
        "senha": SENHA_PADRAO,
        "telefone": "47998876655",
        "cnpj": "98765432000155",
        "nomeLoja": "Turbo Peças",
        "endereco": {
            "rua": "Av. Santos Dumont", "numero": "1200", "complemento": "Loja 3",
            "bairro": "Boa Vista", "cidade": "Joinville", "estado": "SC", "cep": "89204-100"
        },
    },
    {
        "nome": "Roberto Alves",
        "email": "roberto@rapecasoriginais.com.br",
        "senha": SENHA_PADRAO,
        "telefone": "47997654321",
        "cnpj": "11222333000144",
        "nomeLoja": "RA Peças Originais",
        "endereco": {
            "rua": "Rua Curitiba", "numero": "78", "complemento": "",
            "bairro": "Anita Garibaldi", "cidade": "Joinville", "estado": "SC", "cep": "89203-050"
        },
    },
]

# ---------------------------------------------------------------------------
# 2. Clientes
# ---------------------------------------------------------------------------
CLIENTES = [
    {
        "nome": "Ana Paula Ribeiro",
        "email": "ana.ribeiro@gmail.com",
        "senha": SENHA_PADRAO,
        "telefone": "47996541230",
        "endereco": {
            "rua": "Rua das Palmeiras", "numero": "22", "complemento": "Apto 101",
            "bairro": "América", "cidade": "Joinville", "estado": "SC", "cep": "89204-310"
        },
    },
    {
        "nome": "Lucas Martins",
        "email": "lucas.martins@gmail.com",
        "senha": SENHA_PADRAO,
        "telefone": "47991234567",
        "endereco": {
            "rua": "Rua Dona Francisca", "numero": "300", "complemento": "",
            "bairro": "Saguaçu", "cidade": "Joinville", "estado": "SC", "cep": "89221-005"
        },
    },
    {
        "nome": "Juliana Costa",
        "email": "juliana.costa@gmail.com",
        "senha": SENHA_PADRAO,
        "telefone": "47993216549",
        "endereco": {
            "rua": "Rua Timbó", "numero": "88", "complemento": "",
            "bairro": "Bucarein", "cidade": "Joinville", "estado": "SC", "cep": "89202-140"
        },
    },
]

# ---------------------------------------------------------------------------
# 3. Peças (30 no total, 10 por revendedor)
# revendedor_index indica qual revendedor (posição na lista acima) vende a peça
# estado deve ser: NOVO | USADO | RECONDICIONADO | DEFEITUOSO
# ---------------------------------------------------------------------------
PECAS = [
    # --- Auto Peças Mendes (index 0) ---
    {"revendedor_index": 0, "nome": "Amortecedor Dianteiro", "descricao": "Amortecedor dianteiro a gás, alta durabilidade.",
     "categoria": "Suspensão", "preco": 289.90, "estado": "NOVO", "ano": 2018, "marca": "Volkswagen",
     "modeloVeiculo": "Gol", "estoque": 12},
    {"revendedor_index": 0, "nome": "Kit Embreagem Completo", "descricao": "Kit com platô, disco e rolamento.",
     "categoria": "Transmissão", "preco": 540.00, "estado": "NOVO", "ano": 2015, "marca": "Fiat",
     "modeloVeiculo": "Palio", "estoque": 8},
    {"revendedor_index": 0, "nome": "Farol Dianteiro Direito", "descricao": "Farol original, pequenos riscos no vidro.",
     "categoria": "Elétrica", "preco": 320.00, "estado": "USADO", "ano": 2016, "marca": "Chevrolet",
     "modeloVeiculo": "Onix", "estoque": 3},
    {"revendedor_index": 0, "nome": "Bomba de Combustível", "descricao": "Bomba elétrica revisada e testada.",
     "categoria": "Motor", "preco": 210.50, "estado": "RECONDICIONADO", "ano": 2014, "marca": "Ford",
     "modeloVeiculo": "Fiesta", "estoque": 5},
    {"revendedor_index": 0, "nome": "Bico Injetor", "descricao": "Bico injetor eletrônico original.",
     "categoria": "Motor", "preco": 95.00, "estado": "NOVO", "ano": 2017, "marca": "Fiat",
     "modeloVeiculo": "Palio", "estoque": 15},
    {"revendedor_index": 0, "nome": "Sensor de Oxigênio", "descricao": "Sonda lambda nova, embalagem lacrada.",
     "categoria": "Elétrica", "preco": 180.00, "estado": "NOVO", "ano": 2019, "marca": "Chevrolet",
     "modeloVeiculo": "Onix", "estoque": 10},
    {"revendedor_index": 0, "nome": "Correia do Alternador", "descricao": "Correia nova, kit avulso.",
     "categoria": "Motor", "preco": 45.00, "estado": "NOVO", "ano": 2018, "marca": "Volkswagen",
     "modeloVeiculo": "Gol", "estoque": 30},
    {"revendedor_index": 0, "nome": "Disco de Freio Dianteiro", "descricao": "Par de discos ventilados.",
     "categoria": "Freios", "preco": 210.00, "estado": "NOVO", "ano": 2016, "marca": "Ford",
     "modeloVeiculo": "Fiesta", "estoque": 14},
    {"revendedor_index": 0, "nome": "Maçaneta Externa", "descricao": "Maçaneta usada, cor preta, sem trincas.",
     "categoria": "Carroceria", "preco": 60.00, "estado": "USADO", "ano": 2012, "marca": "Fiat",
     "modeloVeiculo": "Uno", "estoque": 7},
    {"revendedor_index": 0, "nome": "Bateria 60Ah", "descricao": "Bateria nova, 12 meses de garantia.",
     "categoria": "Elétrica", "preco": 420.00, "estado": "NOVO", "ano": 2023, "marca": "Volkswagen",
     "modeloVeiculo": "Voyage", "estoque": 9},

    # --- Turbo Peças (index 1) ---
    {"revendedor_index": 1, "nome": "Turbina Garrett", "descricao": "Turbina remanufaturada com garantia de 6 meses.",
     "categoria": "Motor", "preco": 1250.00, "estado": "RECONDICIONADO", "ano": 2017, "marca": "Volkswagen",
     "modeloVeiculo": "Amarok", "estoque": 2},
    {"revendedor_index": 1, "nome": "Pastilha de Freio Dianteira", "descricao": "Jogo de pastilhas cerâmicas.",
     "categoria": "Freios", "preco": 95.00, "estado": "NOVO", "ano": 2020, "marca": "Toyota",
     "modeloVeiculo": "Corolla", "estoque": 25},
    {"revendedor_index": 1, "nome": "Radiador de Água", "descricao": "Radiador novo, lacrado na caixa.",
     "categoria": "Arrefecimento", "preco": 380.00, "estado": "NOVO", "ano": 2019, "marca": "Honda",
     "modeloVeiculo": "Civic", "estoque": 6},
    {"revendedor_index": 1, "nome": "Motor de Arranque", "descricao": "Motor de arranque com leve falha, ideal para reparo.",
     "categoria": "Elétrica", "preco": 150.00, "estado": "DEFEITUOSO", "ano": 2013, "marca": "Fiat",
     "modeloVeiculo": "Uno", "estoque": 1},
    {"revendedor_index": 1, "nome": "Intercooler", "descricao": "Intercooler recondicionado, testado sem vazamentos.",
     "categoria": "Motor", "preco": 680.00, "estado": "RECONDICIONADO", "ano": 2018, "marca": "Volkswagen",
     "modeloVeiculo": "Amarok", "estoque": 3},
    {"revendedor_index": 1, "nome": "Disco de Freio Traseiro", "descricao": "Par de discos sólidos.",
     "categoria": "Freios", "preco": 195.00, "estado": "NOVO", "ano": 2021, "marca": "Toyota",
     "modeloVeiculo": "Corolla", "estoque": 11},
    {"revendedor_index": 1, "nome": "Válvula Termostática", "descricao": "Válvula nova original.",
     "categoria": "Arrefecimento", "preco": 75.00, "estado": "NOVO", "ano": 2019, "marca": "Honda",
     "modeloVeiculo": "Civic", "estoque": 16},
    {"revendedor_index": 1, "nome": "Kit Junta do Motor", "descricao": "Kit completo de juntas e retentores.",
     "categoria": "Motor", "preco": 160.00, "estado": "NOVO", "ano": 2014, "marca": "Fiat",
     "modeloVeiculo": "Uno", "estoque": 8},
    {"revendedor_index": 1, "nome": "Sensor de Estacionamento", "descricao": "Kit com 4 sensores traseiros.",
     "categoria": "Elétrica", "preco": 140.00, "estado": "NOVO", "ano": 2021, "marca": "Hyundai",
     "modeloVeiculo": "HB20", "estoque": 12},
    {"revendedor_index": 1, "nome": "Escapamento Completo", "descricao": "Escapamento recondicionado, sem furos.",
     "categoria": "Escapamento", "preco": 590.00, "estado": "RECONDICIONADO", "ano": 2016, "marca": "Chevrolet",
     "modeloVeiculo": "Cruze", "estoque": 2},

    # --- RA Peças Originais (index 2) ---
    {"revendedor_index": 2, "nome": "Retrovisor Elétrico Esquerdo", "descricao": "Peça original, embalagem lacrada.",
     "categoria": "Carroceria", "preco": 180.00, "estado": "NOVO", "ano": 2021, "marca": "Hyundai",
     "modeloVeiculo": "HB20", "estoque": 10},
    {"revendedor_index": 2, "nome": "Correia Dentada", "descricao": "Correia + tensor, kit completo.",
     "categoria": "Motor", "preco": 130.00, "estado": "NOVO", "ano": 2016, "marca": "Renault",
     "modeloVeiculo": "Sandero", "estoque": 18},
    {"revendedor_index": 2, "nome": "Amortecedor Traseiro", "descricao": "Par de amortecedores traseiros usados, bom estado.",
     "categoria": "Suspensão", "preco": 220.00, "estado": "USADO", "ano": 2012, "marca": "Chevrolet",
     "modeloVeiculo": "Celta", "estoque": 4},
    {"revendedor_index": 2, "nome": "Central Multimídia", "descricao": "Central multimídia recondicionada com tela touch.",
     "categoria": "Elétrica", "preco": 450.00, "estado": "RECONDICIONADO", "ano": 2020, "marca": "Jeep",
     "modeloVeiculo": "Renegade", "estoque": 3},
    {"revendedor_index": 2, "nome": "Roda Liga Leve Aro 15", "descricao": "Jogo de 4 rodas usadas, sem amassados.",
     "categoria": "Pneus e Rodas", "preco": 250.00, "estado": "USADO", "ano": 2017, "marca": "Volkswagen",
     "modeloVeiculo": "Gol", "estoque": 4},
    {"revendedor_index": 2, "nome": "Painel de Instrumentos", "descricao": "Painel usado, funcionando perfeitamente.",
     "categoria": "Interior", "preco": 300.00, "estado": "USADO", "ano": 2015, "marca": "Renault",
     "modeloVeiculo": "Sandero", "estoque": 2},
    {"revendedor_index": 2, "nome": "Cabo de Vela", "descricao": "Jogo de cabos de vela novos.",
     "categoria": "Motor", "preco": 55.00, "estado": "NOVO", "ano": 2013, "marca": "Chevrolet",
     "modeloVeiculo": "Celta", "estoque": 20},
    {"revendedor_index": 2, "nome": "Compressor de Ar Condicionado", "descricao": "Compressor recondicionado com garantia.",
     "categoria": "Elétrica", "preco": 720.00, "estado": "RECONDICIONADO", "ano": 2020, "marca": "Jeep",
     "modeloVeiculo": "Renegade", "estoque": 3},
    {"revendedor_index": 2, "nome": "Para-choque Dianteiro", "descricao": "Para-choque usado, pintura boa.",
     "categoria": "Carroceria", "preco": 340.00, "estado": "USADO", "ano": 2019, "marca": "Hyundai",
     "modeloVeiculo": "HB20", "estoque": 5},
    {"revendedor_index": 2, "nome": "Kit Suspensão Completa", "descricao": "Kit com amortecedores, molas e batentes.",
     "categoria": "Suspensão", "preco": 890.00, "estado": "NOVO", "ano": 2021, "marca": "Renault",
     "modeloVeiculo": "Sandero", "estoque": 4},
]

# ---------------------------------------------------------------------------
# 4. Avaliações
# cliente_index / revendedor_index referem-se às listas acima.
# peca_nome deve bater exatamente com um "nome" da lista PECAS,
# vendido por esse mesmo revendedor.
# ---------------------------------------------------------------------------
AVALIACOES = [
    # Auto Peças Mendes
    {"cliente_index": 0, "revendedor_index": 0, "peca_nome": "Amortecedor Dianteiro",
     "nota": 5, "comentario": "Peça chegou certinha e o preço foi justo. Recomendo a loja."},
    {"cliente_index": 1, "revendedor_index": 0, "peca_nome": "Bateria 60Ah",
     "nota": 4, "comentario": "Bateria nova e o vendedor explicou tudo direitinho."},
    {"cliente_index": 2, "revendedor_index": 0, "peca_nome": "Disco de Freio Dianteiro",
     "nota": 5, "comentario": "Ótimo atendimento, entrega rápida e peça original."},

    # Turbo Peças
    {"cliente_index": 1, "revendedor_index": 1, "peca_nome": "Turbina Garrett",
     "nota": 5, "comentario": "Turbina remanufaturada funcionando perfeitamente, superou expectativas."},
    {"cliente_index": 2, "revendedor_index": 1, "peca_nome": "Pastilha de Freio Dianteira",
     "nota": 4, "comentario": "Boa qualidade, só demorou um pouco na retirada."},
    {"cliente_index": 0, "revendedor_index": 1, "peca_nome": "Radiador de Água",
     "nota": 5, "comentario": "Radiador novo, instalação sem problemas."},

    # RA Peças Originais
    {"cliente_index": 2, "revendedor_index": 2, "peca_nome": "Central Multimídia",
     "nota": 4, "comentario": "Funciona bem, só o suporte de instalação que faltou."},
    {"cliente_index": 0, "revendedor_index": 2, "peca_nome": "Kit Suspensão Completa",
     "nota": 5, "comentario": "Kit completo, carro ficou show. Vendedor muito atencioso."},
    {"cliente_index": 1, "revendedor_index": 2, "peca_nome": "Correia Dentada",
     "nota": 3, "comentario": "Peça boa, mas a entrega atrasou dois dias."},
]


def buscar_revendedor_id_por_cnpj(cnpj):
    """Usado quando o cadastro falha por já existir (CNPJ duplicado).
    Este endpoint é público, ao contrário de /api/usuarios/email/{email}."""
    resp = requests.get(f"{BASE_URL}/revendedores/cnpj/{cnpj}")
    if resp.status_code == 200:
        return resp.json()["id"]
    return None


def cadastrar_revendedores():
    ids = []
    for r in REVENDEDORES:
        resp = requests.post(f"{BASE_URL}/revendedores", json=r)
        if resp.status_code == 201:
            data = resp.json()
            print(f"[OK] Revendedor criado: {r['nomeLoja']} (id={data['id']})")
            ids.append(data["id"])
        else:
            existente_id = buscar_revendedor_id_por_cnpj(r["cnpj"])
            if existente_id:
                print(f"[JA EXISTE] Revendedor {r['nomeLoja']} (id={existente_id})")
                ids.append(existente_id)
            else:
                print(f"[ERRO] Revendedor {r['nomeLoja']}: {resp.status_code} - {resp.text}")
                ids.append(None)
    return ids


def login(email, senha):
    """Faz login e retorna o token JWT."""
    resp = requests.post(f"{BASE_URL}/auth/login", json={"email": email, "senha": senha})
    if resp.status_code == 200:
        return resp.json()["token"]
    print(f"[ERRO] Login falhou para {email}: {resp.status_code} - {resp.text}")
    return None


def cadastrar_clientes(headers):
    """Cadastra os clientes e retorna uma lista de ids (na mesma ordem de CLIENTES).
    Usa o token (de um revendedor) para buscar o id via /api/usuarios/email/{email}
    quando o cliente já existir."""
    ids = []
    for c in CLIENTES:
        resp = requests.post(f"{BASE_URL}/clientes", json=c)
        if resp.status_code == 201:
            data = resp.json()
            print(f"[OK] Cliente criado: {c['nome']} (id={data['id']})")
            ids.append(data["id"])
        else:
            busca = requests.get(f"{BASE_URL}/usuarios/email/{c['email']}", headers=headers)
            if busca.status_code == 200:
                cliente_id = busca.json()["id"]
                print(f"[JA EXISTE] Cliente {c['nome']} (id={cliente_id})")
                ids.append(cliente_id)
            else:
                print(f"[ERRO] Cliente {c['nome']}: {resp.status_code} - {resp.text}")
                ids.append(None)
    return ids


def cadastrar_pecas(revendedor_ids, headers):
    """Cadastra as peças e retorna um dict {nome_da_peca: id_da_peca}."""
    peca_ids = {}
    for p in PECAS:
        idx = p["revendedor_index"]
        revendedor_id = revendedor_ids[idx]
        if not revendedor_id:
            print(f"[SKIP] Peca {p['nome']} sem revendedor valido (id={idx})")
            continue
        payload = {k: v for k, v in p.items() if k != "revendedor_index"}
        payload["revendedorId"] = revendedor_id
        resp = requests.post(f"{BASE_URL}/pecas", json=payload, headers=headers)
        if resp.status_code == 201:
            data = resp.json()
            print(f"[OK] Peca criada: {p['nome']}")
            peca_ids[p["nome"]] = data["id"]
        else:
            print(f"[ERRO] Peca {p['nome']}: {resp.status_code} - {resp.text}")
    return peca_ids


def cadastrar_avaliacoes(cliente_ids, revendedor_ids, peca_ids, headers):
    for a in AVALIACOES:
        cliente_id = cliente_ids[a["cliente_index"]]
        revendedor_id = revendedor_ids[a["revendedor_index"]]
        peca_id = peca_ids.get(a["peca_nome"])

        if not cliente_id or not revendedor_id or not peca_id:
            print(f"[SKIP] Avaliação de '{a['peca_nome']}' sem cliente/revendedor/peça válidos.")
            continue

        payload = {
            "clienteId": cliente_id,
            "revendedorId": revendedor_id,
            "pecaId": peca_id,
            "nota": a["nota"],
            "comentario": a["comentario"],
        }
        resp = requests.post(f"{BASE_URL}/avaliacoes", json=payload, headers=headers)
        if resp.status_code == 201:
            print(f"[OK] Avaliação criada para '{a['peca_nome']}' (nota {a['nota']})")
        else:
            print(f"[ERRO] Avaliação de '{a['peca_nome']}': {resp.status_code} - {resp.text}")


if __name__ == "__main__":
    print("== Cadastrando revendedores ==")
    revendedor_ids = cadastrar_revendedores()

    # POST /api/pecas, GET /api/usuarios/email/{email} e POST /api/avaliacoes
    # exigem autenticação (JWT). Loga com o primeiro revendedor cadastrado
    # e reusa o token para todas essas chamadas.
    token = login(REVENDEDORES[0]["email"], SENHA_PADRAO)
    headers = {"Authorization": f"Bearer {token}"} if token else {}
    if not token:
        print("[AVISO] Não foi possível autenticar. Peças e avaliações serão puladas.")

    print("\n== Cadastrando clientes ==")
    cliente_ids = cadastrar_clientes(headers)

    print("\n== Cadastrando peças ==")
    peca_ids = cadastrar_pecas(revendedor_ids, headers) if token else {}

    print("\n== Cadastrando avaliações ==")
    if token:
        cadastrar_avaliacoes(cliente_ids, revendedor_ids, peca_ids, headers)
    else:
        print("[SKIP] Sem token, avaliações não foram cadastradas.")

    print("\nConcluído! Todos os usuários de teste usam a senha:", SENHA_PADRAO)
    print("Admin padrão (já criado pela aplicação): admin@metal.com / admin123")

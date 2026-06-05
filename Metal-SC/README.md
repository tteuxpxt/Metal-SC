(Para saber como rodar esta aplicação, pule para a aba # Pré-requisitos, número 49)

# Metal-SC

Aplicação full stack desenvolvida com:

* Backend: Spring Boot + MySQL
* Frontend: React

O sistema possui autenticação, painel administrativo, gerenciamento de usuários, revendedores e peças.

---

# Estrutura do Projeto

```txt
Metal-SC/
│
├── Metal-SC/              # Backend Spring Boot
│   ├── .env.example
│   ├── pom.xml
│   └── src/
│
└── metal-frontend/        # Frontend React
    ├── package.json
    └── src/
```

---

# Tecnologias Utilizadas

## Backend

* Java 21
* Spring Boot
* Spring Security
* JPA / Hibernate
* MySQL

## Frontend

* React
* Axios
* React Router

---

# Pré-requisitos

Antes de iniciar, você precisa ter instalado:

* Java 21
* MySQL Server
* Node.js
* npm
* Git (opcional)

---

# 1. Configuração do Banco de Dados

Abra o MySQL e crie o banco:

```sql
CREATE DATABASE metalsc;
```

Caso utilize outro nome de banco, altere o valor de `DB_URL` no arquivo `.env`.

---

# 2. Configuração do Backend

## Arquivo `.env`

O backend utiliza variáveis de ambiente para permitir que cada computador utilize seu próprio usuário e senha do MySQL.

O arquivo deve ser criado em:

```txt
Metal-SC/.env
```

Existe um modelo pronto em:

```txt
Metal-SC/.env.example
```

Copie o arquivo `.env.example` e renomeie para `.env`.

---

## Exemplo de configuração

```properties
DB_URL=jdbc:mysql://localhost:3306/metalsc
DB_USER=root
DB_PASSWORD=123456
```

---

## Explicação das variáveis

| Variável    | Descrição               |
| ----------- | ----------------------- |
| DB_URL      | URL de conexão do MySQL |
| DB_USER     | Usuário do MySQL        |
| DB_PASSWORD | Senha do MySQL          |

---

# 3. Executando o Backend

Abra o PowerShell e entre na pasta do backend:

```powershell
cd B:\Metal-Sc\Metal-SC\Metal-SC
```

---

## Compilar o projeto

```powershell
.\mvnw -q -DskipTests compile
```

---

## Iniciar a API

```powershell
.\mvnw spring-boot:run
```

---

## Endereço padrão da API

```txt
http://localhost:8080
```

Prefixo da API:

```txt
http://localhost:8080/api
```

---

# 4. Executando o Frontend

Abra outro terminal PowerShell:

```powershell
cd B:\Metal-Sc\Metal-SC\metal-frontend
```

---

## Instalar dependências

```powershell
npm install
```

---

## Iniciar aplicação React

```powershell
npm start
```

---

## Endereço padrão do frontend

```txt
http://localhost:3000
```

---

# 5. Alterando a Porta do Backend

Se a porta `8080` estiver ocupada:

```powershell
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

---

## Configurar frontend para nova porta

Crie:

```txt
metal-frontend/.env
```

Conteúdo:

```properties
REACT_APP_API_URL=http://localhost:8081/api
```

Depois reinicie:

```powershell
npm start
```

---

# 6. Usuário Administrador Padrão

Ao iniciar a aplicação, um administrador padrão pode ser criado automaticamente.

## Credenciais padrão

```txt
Email: admin@metal.com
Senha: admin123
```

Recomendado alterar em ambiente de produção.

---

# 7. Problemas Comuns

---

## Access denied for user

Exemplo:

```txt
Access denied for user 'root'@'localhost'
```

### Possíveis causas

* arquivo `.env` não existe;
* senha do MySQL incorreta;
* usuário do MySQL incorreto;
* backend iniciado antes de salvar o `.env`.

### Solução

Verifique:

```properties
DB_USER=root
DB_PASSWORD=sua_senha
```

---

## Port 8080 was already in use

A porta já está sendo usada por outro processo.

### Descobrir o processo

```powershell
netstat -ano | findstr :8080
```

### Encerrar processo

```powershell
Stop-Process -Id NUMERO_DO_PID
```

Ou utilize outra porta.

---

## Unknown database metalsc

O banco ainda não foi criado.

### Solução

```sql
CREATE DATABASE metalsc;
```

---

# 8. Configuração Segura do Git

O arquivo `.env` não deve ser enviado para o GitHub.

Adicione no `.gitignore`:

```gitignore
.env
```

O repositório deve conter apenas:

```txt
.env.example
```

---

# 9. Comandos Rápidos

## Backend

```powershell
cd B:\Metal-Sc\Metal-SC\Metal-SC
.\mvnw spring-boot:run
```

---

## Frontend

```powershell
cd B:\Metal-Sc\Metal-SC\metal-frontend
npm start
```

---

# 10. Arquitetura do Projeto

## Backend

* API REST
* Spring Security
* Controle de acesso por roles
* Painel administrativo
* Soft delete de usuários

## Frontend

* React SPA
* Integração via Axios
* Controle de autenticação
* Área administrativa

---

# Desenvolvedor

Projeto desenvolvido para estudos, prática de arquitetura full stack e gerenciamento administrativo com Spring Boot + React.

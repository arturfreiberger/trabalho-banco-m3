# Sistema de Mensageria — Trabalho de Banco de Dados M3

Aplicação Java de console com JDBC e banco MySQL/MariaDB.

## Requisitos

- Java 11 ou superior
- Maven
- MySQL ou MariaDB
- Banco `mensageria` criado pelo script da etapa 4

## Configuração

1. Copie:

   `src/main/resources/database.properties.example`

   para:

   `src/main/resources/database.properties`

2. Informe usuário e senha do banco no novo arquivo.

3. Compile:

   ```bash
   mvn clean compile
   ```

4. Execute:

   ```bash
   mvn exec:java
   ```

## CRUDs

- Usuários: inserir, consultar, atualizar e excluir.
- Conversas: criar, consultar, atualizar, excluir e gerenciar participantes.
- Mensagens: enviar, consultar, editar, remover logicamente e excluir fisicamente.

## Estrutura

- `model`: classes das entidades.
- `dao`: comandos SQL e acesso ao banco.
- `util`: fábrica de conexões JDBC.
- `app`: menu de console.

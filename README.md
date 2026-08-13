# Catálogo de Mídia (PIT)

Um sistema web completo para gerenciamento de um catálogo de filmes e séries, desenvolvido em Java 17+.
Este projeto aplica os conceitos fundamentais de desenvolvimento web, utilizando arquitetura em camadas (MVC) e tecnologias maduras do ecossistema Java.

## Tecnologias Utilizadas

- **Java 17+**: Linguagem base.
- **Jakarta EE 10 (Servlets & JSP)**: Para controle de requisições (Front Controller) e renderização das views.
- **JSTL e EL**: Para manipulação de dados e iterações nas JSPs de forma segura e sem scriptlets (`<% %>`).
- **MySQL 8**: Banco de dados relacional.
- **JDBC**: Para persistência utilizando `PreparedStatement` (prevenção contra SQL Injection).
- **Flyway**: Ferramenta de versionamento de banco de dados (Migrations).
- **JUnit 5 & Mockito**: Testes unitários e funcionais.
- **Testcontainers**: Para testes de integração de banco de dados com containers efêmeros.
- **Docker & Docker Compose**: Para empacotamento e execução padronizada.
- **Maven**: Gerenciamento de dependências e build.

## Como Executar a Aplicação (Docker)

Foi disponibilizado um `Makefile` na raiz do projeto para facilitar a orquestração do ambiente.

1. **Subir os serviços**:
   ```bash
   make up
   ```
   Este comando irá:
   - Fazer o build da aplicação usando o Maven.
   - Construir a imagem Docker do Tomcat injetando o pacote `.war`.
   - Iniciar os serviços (Banco de dados MySQL e o Tomcat).

2. **Acessar a aplicação**:
   Abra seu navegador em: [http://localhost:8080](http://localhost:8080)
   
   A raiz (`/`) redirecionará automaticamente para o painel principal (`/app/list`).

3. **Ver logs**:
   ```bash
   make logs
   ```

4. **Derrubar os serviços**:
   ```bash
   make down
   ```

5. **Limpar todos os dados (Containers e Volumes)**:
   ```bash
   make clean
   ```

## Boas Práticas e Segurança Aplicadas

- **Zero SQL Injection**: Todo o acesso ao banco de dados foi escrito com parâmetros bindados utilizando o `PreparedStatement`.
- **Zero XSS**: Todas as entradas renderizadas de volta ao usuário utilizam a tag `<c:out>` (JSTL), prevenindo execução de scripts arbitrários.
- **Sem ORMs ou Frameworks complexos (ex: Spring, Hibernate)**: O objetivo foi focar no funcionamento subjacente da linguagem e do protocolo HTTP.

## Sobre a Configuração de Ambiente (.env)

> **Aviso de Avaliação:** O arquivo `.env` contendo a chave de API do TMDB está propositalmente versionado neste repositório. Trata-se de um token descartável criado exclusivamente para facilitar a avaliação do projeto (PIT) por parte dos professores, evitando a necessidade de configurações adicionais. Em um ambiente de produção real, chaves de API nunca devem ser versionadas no código-fonte.

## Próximos Passos
O núcleo da aplicação foi concluído.
As possíveis expansões incluem Autofill consumindo a API do TheMovieDB (TMDB), aprimoramentos de frontend (Carrossel) e um sistema interativo de avaliações.
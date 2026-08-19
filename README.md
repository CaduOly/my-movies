# Catálogo de Mídia (PIT)

> 📖 **Para avaliadores:** suba a aplicação com `make start` e acesse **http://localhost:8080/app/about** para a documentação navegável (visão geral, arquitetura, telas e como testar). Você também encontra uma cópia da documentação no arquivo `my-movies - sobre.pdf` na raiz do projeto.

Um sistema web completo para gerenciamento de um catálogo de filmes e séries, desenvolvido em Java 17+.
Este projeto aplica os conceitos fundamentais de desenvolvimento web, utilizando arquitetura em camadas (MVC) e tecnologias maduras do ecossistema Java.

## Tecnologias Utilizadas

- **Java 17+**: Linguagem base.
- **Jakarta EE 10 (Servlets & JSP)**: Para receber as requisições e mostrar as telas (views).
- **JSTL e EL**: Para exibir e manipular os dados nas páginas de forma mais limpa e segura.
- **MySQL 8**: Banco de dados relacional.
- **JDBC**: Para persistência utilizando `PreparedStatement` (prevenção contra SQL Injection).
- **Flyway**: Ferramenta de versionamento de banco de dados (Migrations).
- **JUnit 5 & Mockito**: Testes unitários e funcionais.
- **Testcontainers**: Para testar a integração com o banco de dados usando containers temporários (criados e destruídos automaticamente).
- **Docker & Docker Compose**: Para empacotamento e execução padronizada.
- **Maven**: Gerenciamento de dependências e build.

## Como Executar a Aplicação (Docker)

Foi disponibilizado um `Makefile` na raiz do projeto para facilitar a orquestração do ambiente.

1. **Subir os serviços**:
   ```bash
   make start
   ```
   Este comando irá:
   - Fazer o build da aplicação usando o Maven.
   - Construir a imagem Docker do Tomcat injetando o pacote `.war`.
   - Iniciar os serviços (Banco de dados MySQL e o Tomcat).

2. **Acessar a aplicação**:
   Abra seu navegador em: [http://localhost:8080](http://localhost:8080)
   
   A raiz (`/`) redirecionará automaticamente para o painel principal (`/app/home`).

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

- **Prevenção contra SQL Injection**: Todo o acesso ao banco de dados foi escrito com consultas parametrizadas utilizando o `PreparedStatement`.
- **Prevenção contra XSS**: Todos os dados exibidos para o usuário utilizam a tag `<c:out>` (JSTL), evitando que códigos maliciosos sejam executados na tela.
- **Sem ORMs ou frameworks complexos (como Spring ou Hibernate)**: O objetivo principal foi entender como as coisas funcionam "por baixo dos panos" no Java e na web (HTTP).

## Sobre a Configuração de Ambiente (.env)

> **Aviso de Avaliação:** O arquivo `.env` contendo a chave de API do TMDB está propositalmente versionado neste repositório. Trata-se de um token descartável criado exclusivamente para facilitar a avaliação do projeto (PIT) por parte dos professores, evitando a necessidade de configurações adicionais. Em um ambiente de produção real, chaves de API nunca devem ser versionadas no código-fonte.

## Funcionalidades Principais

- **Integração TMDB**: Preenchimento automático de dados (sinopse, ano, poster) consumindo a API do TheMovieDB.
- **Avaliações**: Sistema interativo de nota por estrelas e comentários.
- **Catálogo Visual**: Página inicial em formato de grid.
- **CRUD e Busca**: Gerenciamento completo de itens e busca por filtros.
- **Internacionalização**: Suporte a Português e Inglês (i18n).
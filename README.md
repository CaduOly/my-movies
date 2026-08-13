# Catálogo de Mídia (PIT)

Um sistema web completo para gerenciamento de um catálogo de filmes e séries, desenvolvido em Java 17+.
Este projeto aplica os conceitos fundamentais de desenvolvimento web, utilizando arquitetura em camadas (MVC), testes de integração e tecnologias maduras do ecossistema Java.

## Funcionalidades

O sistema atende aos seguintes requisitos:
- **CRUD Completo:** Criação, leitura, edição e exclusão de itens de mídia.
- **Busca e Filtros:** Pesquisa flexível por título, ano de lançamento ou autor/diretor.
- **Avaliações e Comentários:** Sistema de classificação por estrelas (rating de 0 a 5).
- **Integração Externa (Autofill):** Preenchimento automático de dados de filmes consumindo a API do **TheMovieDB (TMDB)**.
- **Internacionalização (i18n):** Suporte nativo a múltiplos idiomas (PT/EN) mapeados via *ResourceBundles*.

## Arquitetura do Sistema

A aplicação foi estruturada utilizando o padrão **MVC (Model-View-Controller)** com uma clara separação em camadas:

- **View:** Páginas renderizadas dinamicamente utilizando JSPs, com o uso de JSTL (`<c:out>`) e EL para inibição de Cross-Site Scripting (XSS).
- **Controller (`servlet/`):** O `MediaController` intercepta requisições HTTP, coordena a lógica e delega tarefas.
- **Service (`service/`):** Onde reside a lógica de negócios e validação (`CatalogService`, `MediaItemValidator`).
- **DAO (`dao/`):** Isolamento do acesso a dados (`MySqlMediaItemDAO`), comunicando-se com o banco através de `PreparedStatement` para prevenir SQL Injection.

### Inversão de Dependência (DI) e Composition Root
O sistema desacopla implementações concretas utilizando interfaces, como a `MovieMetadataProvider`. No pacote `infra/`, a classe `AppBootstrap` atua como o **Composition Root** da aplicação: ela é responsável por instanciar as classes corretas (ex: `TmdbMetadataProvider`) e injetá-las nos serviços logo na inicialização, permitindo que a aplicação troque implementações facilmente (útil em testes).

### Árvore de Pacotes
```text
src/main/java/com/seu/catalog
├── dao/        # Acesso a banco de dados (DAOs e interfaces)
├── exception/  # Exceções personalizadas de negócio
├── infra/      # Configurações globais, factories e Injeção de Dependências
├── model/      # Entidades de domínio (ex: MediaItem, MediaType)
├── service/    # Regras de negócio, validações e integrações externas
└── servlet/    # Controladores da camada Web HTTP
```

## Modelo de Dados

A persistência principal ocorre na tabela **`item_media`** dentro de um banco MySQL 8.
- **Campos chaves:** `id`, `title`, `author_director`, `release_year`, `genre`, `synopsis`, `media_type`, `poster_url`, `rating` e `comment`.
- A evolução do banco é feita via **Migrations (Flyway)**. O script `V1` cria a estrutura da tabela, enquanto o `V2` injeta a massa de dados inicial (Seed).

## Tecnologias Utilizadas

- **Linguagem & Web:** Java 17+, Jakarta EE 10 (Servlets & JSP), JSTL/EL.
- **Banco de Dados:** MySQL 8, JDBC (puro, sem ORM para foco em SQL).
- **Infra e Ferramentas:** Flyway (Migrations), Maven, Docker & Docker Compose.
- **Testes:** JUnit 5, Mockito, Testcontainers.

## Processo de Desenvolvimento e TDD

O projeto foi construído sobre uma cultura orientada a testes (**Test-First**). 
- O fluxo de repositório seguiu **Git Flow**, dividindo o trabalho em branches semânticas (`feature/*`).
- As branches só são integradas à `release` principal após serem submetidas e aprovadas por baterias rigorosas de testes unitários e de integração, garantindo o processo contínuo de qualidade que o projeto propõe.

## Pré-requisitos

Para garantir que o ambiente seja configurado corretamente, instale as seguintes ferramentas (os links direcionam para a documentação oficial de instalação):

- [**Java 17+**](https://adoptium.net/): Linguagem e máquina virtual necessárias para compilar e rodar a aplicação (recomenda-se a distribuição Eclipse Temurin).
- [**Apache Maven 3.8+**](https://maven.apache.org/install.html): Gerenciador de pacotes, dependências e ciclo de build do projeto.
- [**Docker**](https://docs.docker.com/get-docker/) e [**Docker Compose**](https://docs.docker.com/compose/install/): Essenciais para instanciar o banco de dados MySQL de desenvolvimento e fundamentais para a execução da biblioteca **Testcontainers** durante os testes automatizados.
- [**Make (GNU Make)**](https://www.gnu.org/software/make/): Ferramenta utilizada para simplificar e orquestrar os comandos no terminal. Já vem por padrão no Linux/macOS. No Windows, você pode usar o [Make for Windows](http://gnuwin32.sourceforge.net/packages/make.htm) ou executar os comandos descritos dentro do `Makefile` manualmente.

## Como Executar e Testar

Foi disponibilizado um `Makefile` na raiz para facilitar a orquestração do ambiente.

### 1. Executando os Testes Automatizados
Os testes de integração utilizam **Testcontainers**, o que exige que o Docker daemon da sua máquina esteja rodando.
```bash
make test
# Ou diretamente pelo maven: mvn clean test
```

### 2. Rodando a Aplicação (Com Docker)
Para subir o banco de dados provido pelo compose e a própria aplicação empacotada no Tomcat:
```bash
make start
```
Após o build, a aplicação estará disponível em: [http://localhost:8080](http://localhost:8080).
(A raiz `/` redirecionará para o painel principal em `/app/home`).

**Outros comandos úteis no Docker:**
- `make logs`: Verifica os logs de acesso.
- `make down`: Derruba o serviço.
- `make clean`: Destrói containers e os volumes persistidos do banco de dados.

### 3. Rodando Localmente (Sem Docker Compose)
Caso prefira não rodar a aplicação como um container:
1. Tenha um banco MySQL 8 rodando localmente (na porta 3306).
2. Empacote a aplicação (`mvn clean package -DskipTests`).
3. Faça o *deploy* do artefato gerado em `target/my-movies-1.0.0.war` em um servidor Tomcat local (ajustando as variáveis de ambiente necessárias que estão indicadas no `docker-compose.yml`).

---
> **Aviso de Avaliação:** O arquivo `.env` contendo a chave de API do TMDB está propositalmente versionado neste repositório como um token descartável para facilitar a avaliação, dispensando configurações adicionais. Em um ambiente de produção real, chaves nunca devem ser versionadas.
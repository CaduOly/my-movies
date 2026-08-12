# PRD: Catálogo de Mídia em Java Web (Media Catalog / PIT)

**Data:** 2026-08-12  
**Versão:** 1.0  
**Status:** Em Planejamento  
**Público:** Avaliadores, Time de Desenvolvimento

---

## 1. Visão Geral

### Objetivo do Projeto

Desenvolver um **catálogo de mídia (filmes, séries e livros)** em **Java web**, funcionando como projeto integrador (PIT) que demonstra domínio de:

- **Desenvolvimento backend** seguro e estruturado (MVC, POO real, JDBC)
- **Padrões de arquitetura** (DIP, separação de responsabilidades)
- **Segurança by design** (SQL Injection, XSS)
- **Qualidade de código** (SOLID, TDD, Javadoc)
- **Entrega profissional** (Git workflow, CI, documentação)

### Escopo

#### Núcleo (Obrigatório)
- **CRUD completo** (Create, Read, Update, Delete) de itens de mídia
- **Busca parametrizada** por título/autor-diretor (anti-injection)
- **Interface web responsiva** com listagem, detalhes, formulário de edição
- **Validação segura** de entrada (tipo, faixa, comprimento)
- **Persistência relacional** (MySQL 8) com versionamento (Flyway)
- **Internacionalização** (PT-BR / EN)

#### Extensões (Opcionais, após o núcleo)
- **Autofill TMDB**: buscar metadados (pôster, gênero, sinopse) via API
- **Home estendida**: grid/carrossel de capas com detalhe expandido
- **Avaliação de itens**: estrelas (0-5) e comentários

---

## 2. Requisitos Funcionais

### RF1: Gerenciamento de Itens
- **Create:** Cadastrar novos itens (filme, série, livro)
- **Read:** Listar todos os itens + visualizar detalhes de um item
- **Update:** Editar qualquer campo de um item existente
- **Delete:** Remover um item do catálogo

### RF2: Busca
- Busca **parametrizada** por termo (título ou autor/diretor)
- Tolerância a caracteres especiais e acentuação (`utf8mb4`)
- Proteção **obrigatória** contra SQL Injection

### RF3: Interface Web
- **Navegação:** Menu lateral com opções (Home, Gerenciar Biblioteca, Adicionar)
- **Listagem:** Tabela ou grid com itens e ações (editar, deletar)
- **Formulário:** Reaproveitável para novo/edição, com validação client e server
- **Detalhe:** Página dedicada a um item, mostrando todos os campos

### RF4: Segurança
- **SQL Injection:** ZERO concatenação; sempre `PreparedStatement`
- **XSS:** Toda saída de usuário escapada via `<c:out>`
- **Validação de entrada:** Obrigatório, tipo/faixa, comprimento máximo
- **Gestão de credenciais:** Fora do código, via variáveis de ambiente ou `.env`

### RF5: Internacionalização
- **Idioma PT-BR** como padrão
- **Suporte a EN** (sem alteração de código)
- **Mensagens dinâmicas** via `ResourceBundle` + `<fmt:message>`

---

## 3. Requisitos Não-Funcionais

### RNF1: Stack Obrigatória
| Componente | Escolha | Versão |
|---|---|---|
| Linguagem | Java | 17+ |
| Paradigma | POO (encapsulamento, herança, polimorfismo) | — |
| Backend | Servlet | Jakarta EE |
| View | JSP | com EL / JSTL |
| Acesso a dados | JDBC puro | `PreparedStatement` |
| SGBD | MySQL | 8 (charset `utf8mb4`) |
| Migrações | Flyway | latest |
| Build | Maven | `packaging=war` |
| Servidor | Apache Tomcat | 10+ |
| Testes | JUnit 5 | com Mockito |

### RNF2: Proibições (o que NÃO fazer)
- ❌ Spring, Spring Boot, Spring MVC
- ❌ Hibernate, JPA, qualquer ORM
- ❌ React, Vue, Angular (SPA)
- ❌ Lombok
- ❌ Scriptlets em JSP (`<% %>`, `<%= %>`)
- ❌ Lógica de negócio em Servlet/JSP
- ❌ `Statement` com concatenação SQL
- ❌ Credenciais no código/Git
- ❌ `System.out.println` para logs

### RNF3: Qualidade de Código
- **Javadoc:** Obrigatório em toda classe/método público (PT, sem warnings)
- **TDD:** Red → Green → Refactor por comportamento
- **Testes:** Unit (Service), Integração (DAO), Funcional (Web)
- **Git:** Conventional Commits, PRs pequenas (um comportamento)
- **CI:** Build + testes verdes antes de merge

### RNF4: Documentação Entregável
- [ ] Diagrama de Casos de Uso (UML)
- [ ] Diagrama de Classes (UML)
- [ ] DER (Diagrama Entidade-Relacionamento)
- [ ] Javadoc gerado (`mvn javadoc:javadoc`)
- [ ] Relatório técnico em PDF (arquitetura, segurança, fluxo)
- [ ] Manual do usuário (1-2 páginas)
- [ ] Histórico Git (branches/PRs como prova de processo)

---

## 4. Modelo de Domínio

### Entidade: MediaItem

```java
public class MediaItem {
    private Integer id;                    // PK, auto-increment
    private String title;                  // NOT NULL, VARCHAR(255)
    private String authorDirector;         // VARCHAR(255), nullable
    private Integer releaseYear;           // INT, nullable
    private String genre;                  // VARCHAR(100), nullable
    private String synopsis;               // TEXT, nullable
    private MediaType mediaType;           // ENUM: MOVIE, SERIES, BOOK
    private String posterUrl;              // VARCHAR(500), nullable (ext)
    private String externalId;             // VARCHAR(50), nullable (ext)
    private Integer rating;                // INT (0-5), nullable (ext)
    private String comment;                // TEXT, nullable (ext)
}

public enum MediaType {
    MOVIE,
    SERIES,
    BOOK
}
```

### Banco de Dados

```sql
CREATE TABLE item_media (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author_director VARCHAR(255),
    release_year INT,
    genre VARCHAR(100),
    synopsis TEXT,
    media_type VARCHAR(20) NOT NULL,
    poster_url VARCHAR(500),
    external_id VARCHAR(50),
    rating INT CHECK (rating >= 0 AND rating <= 5),
    comment TEXT,
    INDEX idx_title (title),
    INDEX idx_author_director (author_director)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 5. Arquitetura

### Camadas (MVC + DIP)

```
┌─────────────────────────────────────────┐
│           HTTP Request                  │
└────────────┬────────────────────────────┘
             │
     ┌───────▼────────────┐
     │   Servlet/JSP      │  (Controller + View)
     │   (Apresentação)   │
     └───────┬────────────┘
             │
     ┌───────▼────────────────┐
     │   CatalogService       │  (Domínio/Negócio)
     │   + Validação          │  + MovieMetadataProvider
     └───────┬────────────────┘
             │
     ┌───────▼────────────────┐
     │   MediaItemDAO         │  (Persistência)
     │   (interface DAO)      │
     └───────┬────────────────┘
             │
     ┌───────▼────────────────┐
     │   MySQL 8              │  (Banco de Dados)
     │   (item_media)         │
     └────────────────────────┘
```

### Dependency Injection (DIP)

- **Interfaces nas fronteiras:** `MediaItemDAO`, `MovieMetadataProvider`
- **Injeção manual por construtor** em `CatalogService`
- **Composition root único:** `AppBootstrap` (listener) constrói o grafo de dependências
- **Sem framework:** uso de interfaces do Java puro

### Fluxo de uma Requisição

1. **Servlet** recebe HTTP, extrai parâmetros, valida formato básico
2. **Service** aplica regras de negócio (validação semântica, transação)
3. **DAO** executa SQL seguro (`PreparedStatement`)
4. **Banco** persiste/retorna dados
5. **JSP** renderiza resposta com `<c:out>` + `<fmt:message>`

---

## 6. Segurança

### SP1: SQL Injection

**Ameaça:** Entrada malformada executa SQL não intencional.

**Controle:** `PreparedStatement` com parâmetros separados de SQL.

```java
// ✓ Seguro
String sql = "SELECT * FROM item_media WHERE title LIKE ? OR author_director LIKE ?";
try (Connection c = ConnectionFactory.get();
     PreparedStatement st = c.prepareStatement(sql)) {
    String like = "%" + term + "%";
    st.setString(1, like);
    st.setString(2, like);
}

// ✗ Proibido
String sql = "SELECT * FROM item_media WHERE title LIKE '%" + term + "%'";
```

**Teste:** Entrada `'; DROP TABLE item_media; --` não altera schema, retorna vazio.

### SP2: XSS (Cross-Site Scripting)

**Ameaça:** Saída contendo `<script>` é executada no navegador.

**Controle:** Escape obrigatório com `<c:out>` em toda JSP.

```jsp
<!-- ✓ Seguro -->
<td><c:out value="${item.title}" /></td>

<!-- ✗ Proibido (e vulnerável) -->
<td>${item.title}</td>
```

**Teste:** Campo preenchido com `<script>alert('xss')</script>` aparece como texto literal.

### SP3: Validação de Entrada

**Regras:**
- **Tipo:** `releaseYear` é numérico
- **Faixa:** `rating` entre 0-5
- **Comprimento:** `title` até 255 caracteres
- **Obrigatoriedade:** `title` e `mediaType` são obrigatórios

**Teste:** Entrada inválida gera `ValidationException`, a app não quebra.

---

## 7. Git Workflow

### Estrutura de Branches

```
main                              (⚠️ protegida)
  ├── release/pit-catalog         (integração final)
  │   ├── delivery/core           (núcleo)
  │   │   ├── feature/contracts   (1ª PR)
  │   │   ├── feature/infra
  │   │   ├── feature/dao-crud
  │   │   ├── feature/dao-search
  │   │   ├── feature/validation
  │   │   ├── feature/service-crud
  │   │   ├── feature/web-crud
  │   │   ├── feature/web-search
  │   │   └── feature/layout
  │   ├── delivery/tmdb           (ext)
  │   │   └── feature/tmdb-search
  │   ├── delivery/frontend-home  (ext)
  │   │   └── feature/home-grid
  │   └── delivery/rating         (ext)
  │       └── feature/rating-ui
```

### Regras

1. **`delivery/*` nasce de `main`** (indep.) ou **`release`** (depende de núcleo)
2. **`feature/*` nasce de `delivery/*`** e volta via PR
3. **1ª PR de cada `delivery`:** sempre `feature/contracts` (interfaces + modelo + exceptions)
4. **Ordem dentro do núcleo:** contracts → infra → dao-* → validation/service → web-*/layout
5. **Commit order:** `test:` → `feat:` → `refactor:` → `docs:`
6. **PR pequena:** um comportamento; CI verde; Javadoc sem warnings

---

## 8. Sequenciamento de Tasks

### Fase 1: Núcleo (avaliado)

| # | Task | Entrega | Estimativa | Prioridade | Status |
|---|---|---|---|---|---|
| — | feature/contracts | delivery/core | 3 pts | 🔴 BLOQUEADOR | — |
| 0 | Infra (Compose, MySQL, Flyway, i18n) | delivery/core | 5 pts | 🔴 BLOQUEADOR | — |
| 1 | DAO CRUD + Search | delivery/core | 8 pts | 🔴 BLOQUEADOR | — |
| 2 | Service + Validação | delivery/core | 6 pts | 🔴 BLOQUEADOR | — |
| 3 | Web (Servlet, JSP, Layout) | delivery/core | 8 pts | 🔴 BLOQUEADOR | — |

### Fase 2: Extensões (opcionais)

| # | Task | Entrega | Estimativa | Depende | Status |
|---|---|---|---|---|---|
| 4 | TMDB Autofill | delivery/tmdb | 8 pts | Tasks 0-3 | — |
| 5 | Home (Grid/Carrossel) | delivery/frontend-home | 5 pts | Tasks 0-4 | — |
| 6 | Rating (Estrelas + Comentário) | delivery/rating | 3 pts | Tasks 0-5 | — |

### Timeline Estimada

- **Semana 1:** Contracts + Task 0 (Infra)
- **Semana 2:** Task 1 (DAO)
- **Semana 3:** Tasks 2 + 3 (Service + Web)
- **Semana 4:** Tasks 4-6 (Extensões)
- **Release:** Finais (testes, docs, PR final)

---

## 9. Critérios de Aceite (Geral)

### Build
- [ ] `mvn clean package` sucede
- [ ] `docker compose up` sobe banco + app
- [ ] App está acessível em `http://localhost:8080<ctx>`

### Testes
- [ ] `mvn test` passa 100%
- [ ] Cobertura mínima: 70% nas camadas core (DAO, Service)
- [ ] Teste de injection SQL com `'; DROP TABLE...` retorna vazio
- [ ] Teste de XSS com `<script>alert...</script>` renderiza como texto

### Código
- [ ] Sem frameworks proibidos (Spring, Hibernate, Lombok, etc.)
- [ ] Sem `System.out.println`; usar `java.util.logging`
- [ ] Sem `PreparedStatement` com concatenação
- [ ] Sem scriptlets em JSP
- [ ] Sem credenciais no código/Git

### Documentação
- [ ] `mvn javadoc:javadoc` sem warnings
- [ ] Javadoc em PT-BR em toda classe/método público
- [ ] Sem TODOs/FIXMEs esquecidos
- [ ] Diagrama UML + DER incluso

### Git
- [ ] Historia limpa (Conventional Commits)
- [ ] PRs com descrição clara
- [ ] Branches protegidas (`main`, `release/*`)

---

## 10. Riscos e Mitigações

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| Caracteres especiais (mojibake) | Média | Alto | Charset `utf8mb4` desde o início, JDBC com `useUnicode=true` |
| SQL Injection em busca | Média | Crítico | Testes explícitos com payloads maliciosos; code review |
| Lógica em JSP/Servlet | Alta | Alto | Code review contra plano; interfaces + stubs desde contracts |
| Dependências circulares | Média | Médio | DIP strict; injeção manual; composição root único |
| Perda de dados (rollback) | Baixa | Crítico | Try-catch-finally + teste de rollback; log detalhado |
| Atraso em extensões | Média | Baixo | Escopo claro (núcleo é obrigatório; ext é bonus) |

---

## 11. Métricas de Sucesso

- ✅ **Funcionalidade:** CRUD + busca 100% operacional
- ✅ **Segurança:** Zero falhas em SQL Injection + XSS; validação 100%
- ✅ **Qualidade:** Testes > 70%, Javadoc completo, sem warnings Maven
- ✅ **Entrega:** Docs (diagramas + PDF + manual); histórico Git; PR final
- ✅ **Timing:** Núcleo em 3-4 semanas; extensões bonus em 1-2

---

## 12. Contato & Escalação

- **Product Owner:** Projeto Integrador (PIT)
- **Revisor:** Avaliador da disciplina
- **Deadline:** Conforme cronograma institucional

---

**Versão 1.0 | 2026-08-12**

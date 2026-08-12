# 📝 RESUMO DO PROJETO: Catálogo de Mídia (PIT)

**Data:** 2026-08-12  
**Versão:** 1.0  
**Status:** Planejamento Completo → Pronto para Implementação

---

## 📦 O Que Foi Criado

### Documentação Geral
- ✅ **`docs/PRD.md`** — Product Requirements Document (5 páginas)
  - Visão, escopo, requisitos funcionais/não-funcionais
  - Arquitetura MVC + DIP
  - Segurança (SQL Injection, XSS, Validação)
  - Layout CSS definido
  - Git workflow profissional

- ✅ **`docs/README.md`** — Documentação técnica (referência)
  - Arquitetura detalhada
  - Checklist de conformidade
  - Setup local + troubleshooting

- ✅ **`INDEX.md`** — Guia de início rápido
  - Tl;dr do projeto
  - Fluxo: leia PRD → task README → task detalhada
  - Checklist inicial

### Plano de Tarefas (7 tasks)
- ✅ **`tasks/README.md`** — Índice de todas as tasks
  - Mapa visual com dependências
  - Timeline estimada
  - Checklist geral

- ✅ **`tasks/00-feature-contracts.md`** (3 pts, 1ª, bloqueador)
  - Modelo `MediaItem` (POJO)
  - Interfaces `MediaItemDAO`, `MovieMetadataProvider`
  - Exceções customizadas
  - Stubs para testes

- ✅ **`tasks/01-task-0-infra.md`** (5 pts, bloqueador)
  - Maven `pom.xml` (Servlet, JSTL, MySQL, Flyway, JUnit)
  - `docker-compose.yml` (MySQL 8 + Tomcat)
  - Flyway migrations (`V1` schema, `V2` seed)
  - `ConnectionFactory` + `AppBootstrap`
  - i18n (`messages_pt_BR`, `messages_en`)

- ✅ **`tasks/02-task-1-dao.md`** (8 pts, bloqueador)
  - `MySqlMediaItemDAO` (CRUD + Search)
  - ZERO SQL Injection (`PreparedStatement`)
  - Testes integração contra banco real
  - **CRÍTICO:** `testSearchInjectionDelete()`, `testSearchInjectionUnion()`

- ✅ **`tasks/03-task-2-service.md`** (6 pts, bloqueador)
  - `MediaItemValidator` (regras semânticas)
  - `CatalogService` (CRUD + DIP)
  - Testes unitários (DAO mockado)
  - Conversão `DAOException` → `ServiceException`

- ✅ **`tasks/04-task-3-web.md`** (8 pts, bloqueador)
  - `MediaController` (Servlet, zero lógica)
  - JSP views (list, form, detail, search)
  - Layout fixo (sidebar + content)
  - CSS base (cores, responsive)
  - **CRÍTICO:** `<c:out>` em toda saída (zero XSS)
  - Testes funcionais

- ✅ **`tasks/05-task-4-tmdb.md`** (8 pts, bonus)
  - `TmdbMetadataProvider` (HTTP, JSON parsing)
  - API key via env var
  - Autofill (pôster, gênero, sinopse, diretor)
  - Fallback gracioso (null se indisponível)
  - Timeout 5s

- ✅ **`tasks/06-task-5-frontend-home.md`** (5 pts, bonus)
  - Home com grid de capas (pôsteres)
  - Carrossel vanilla JS
  - Placeholder se sem pôster
  - Responsive (desktop + mobile)

- ✅ **`tasks/07-task-6-rating.md`** (3 pts, bonus)
  - Rating (0-5 estrelas)
  - Comentário (< 1000 chars)
  - Validação server-side
  - Testes de validação

---

## 🎯 Escopo Confirmado

### Core (Obrigatório — 30 pts em 4 semanas)
✅ CRUD completo (create, read, update, delete)  
✅ Busca parametrizada (anti-injection)  
✅ Interface web responsiva  
✅ Validação segura (tipo, faixa, comprimento)  
✅ Persistência MySQL versionada (Flyway)  
✅ Internacionalização (PT-BR / EN)  
✅ Segurança by design (SQL Injection + XSS)  

### Extensões (Bonus — 16 pts em 2 semanas)
✅ TMDB autofill via API  
✅ Home com grid/carrossel  
✅ Avaliação (estrelas + comentário)  

### Fora do Escopo
❌ Login/autenticação  
❌ Multi-usuário  
❌ Microserviços  
❌ Frameworks (Spring, Hibernate, etc)  
❌ Frontend frameworks (React, Vue, etc)  

---

## 🔐 Segurança: 3 Pontos Críticos Documentados

### SP1: SQL Injection (Task 1)
```java
// ✗ ERRADO
String sql = "WHERE title = '" + input + "'";

// ✓ CORRETO
String sql = "WHERE title = ?";
stmt.setString(1, input);

// TESTE: input = "'; DROP TABLE item_media; --"
// Esperado: retorna vazio, schema intacto
```

### SP2: XSS (Task 3)
```jsp
<!-- ✗ ERRADO -->
<td>${item.title}</td>

<!-- ✓ CORRETO -->
<td><c:out value="${item.title}" /></td>

<!-- TESTE: title = "<script>alert('xss')</script>" -->
<!-- Esperado: renderiza como texto literal em HTML -->
```

### SP3: Validação (Task 2)
```java
// Regras:
✅ title: obrigatório, 1-255 chars
✅ mediaType: obrigatório
✅ releaseYear: opcional, 1800-2100
✅ rating: opcional, 0-5

// TESTE: input inválido → ValidationException (não quebra app)
```

---

## 📊 Cronograma

| Semana | Task | Estimativa | Status |
|---|---|---|---|
| 1 | Contracts (3) + Infra (5) | 8 pts | ⬜ |
| 2 | DAO CRUD + Search (8) | 8 pts | ⬜ |
| 3 | Service (6) + Web CRUD (8) | 14 pts | ⬜ |
| 4 | Web finishers | — | ⬜ |
| 5 | TMDB (8) + Frontend (5) | 13 pts | ⬜ |
| 6 | Rating (3) + docs | 3 pts | ⬜ |

**Total:** 30 pts core + 16 bonus = 46 pts  
**Velocity:** ~8 pts/semana  
**Duration:** 6 semanas (4 core + 2 bonus)

---

## 📋 Checklist de Conformidade

### Código
- [ ] Java 17+, POO real (encapsulamento, herança, polimorfismo)
- [ ] JDBC puro com `PreparedStatement` (zero concatenação)
- [ ] JSP com `<c:out>` + JSTL (zero scriptlets)
- [ ] Sem Lombok, Spring, Hibernate, React
- [ ] `java.util.logging` (não `System.out.println`)
- [ ] Sem credenciais em código/Git

### Testes
- [ ] TDD (Red → Green → Refactor)
- [ ] Unit (Service com DAO mockado)
- [ ] Integração (DAO contra banco real)
- [ ] Funcional (Servlet com Service mockado)
- [ ] Cobertura > 70%
- [ ] **Injection tests:** DROP TABLE, UNION (Task 1)
- [ ] **XSS tests:** `<script>` escapa (Task 3)

### Documentação
- [ ] Javadoc PT-BR em toda classe/método público
- [ ] `mvn javadoc:javadoc` zero warnings
- [ ] Sem TODOs/FIXMEs
- [ ] Sem comentário redundante
- [ ] Diagramas UML (casos de uso, classes, DER)

### Git
- [ ] Commits Conventional (test:, feat:, fix:, refactor:, docs:)
- [ ] PRs pequenas (um comportamento)
- [ ] Branch `main` sempre verde
- [ ] `release/pit-catalog` integra entregas

---

## 🏗️ Arquitetura Resumida

```
HTTP Request
    ↓
Servlet MediaController (orquestra, zero SQL/lógica)
    ↓
CatalogService + MediaItemValidator (regras + validação)
    ↓
MediaItemDAO interface → MySqlMediaItemDAO (JDBC puro)
    ↓
MySQL 8 (utf8mb4, migrations Flyway)
    ↓
JSP + JSTL + EL (<c:out> em toda saída)
    ↓
HTML Response
```

---

## 📂 Estrutura de Pastas

```
my-movies/
├── INDEX.md ..................... Guia rápido (leia 1º)
├── PROJETO-RESUMO.md ............ Este arquivo
├── docs/
│   ├── PRD.md ................... Product Req Doc (5 min read)
│   └── README.md ................ Referência técnica
├── tasks/
│   ├── README.md ................ Índice de tasks
│   └── 00-07-*.md ............... 7 tasks detalhadas
├── agent/
│   └── plan-base.md ............. Plano original (referência)
├── src/
│   ├── main/java/com/seu/catalog/
│   │   ├── model/ ............... MediaItem, MediaType
│   │   ├── dao/ ................. Interfaces, MySqlMediaItemDAO
│   │   ├── service/ ............. CatalogService, Validator
│   │   ├── servlet/ ............. MediaController
│   │   ├── exception/ ........... DAOException, etc
│   │   └── infra/ ............... ConnectionFactory, Bootstrap
│   ├── main/webapp/
│   │   ├── WEB-INF/jsp/ ......... Views (list, form, detail)
│   │   ├── css/ ................. style.css
│   │   └── js/ .................. carousel.js, rating.js
│   ├── main/resources/
│   │   ├── db/migration/ ........ V1, V2 (Flyway)
│   │   └── messages_*.properties . i18n
│   └── test/java/...
├── pom.xml ...................... Maven
├── docker-compose.yml ........... MySQL + Tomcat
└── Dockerfile ................... Tomcat image
```

---

## 🚀 Como Usar Esta Documentação

### Para Começar
1. Leia [`INDEX.md`](./INDEX.md) (2 min)
2. Leia [`docs/PRD.md`](./docs/PRD.md) (5 min)
3. Leia [`tasks/README.md`](./tasks/README.md) (2 min)
4. Pegue [`tasks/00-feature-contracts.md`](./tasks/00-feature-contracts.md)

### Durante Implementação
- Consulte o arquivo da task que está fazendo
- Leia a seção **Travas** (constraints duras)
- Siga **Checkpoints** durante o trabalho
- Finalize com **Definition of Done** (checklist)

### Quando Ficar Perdido
- Consulte [`docs/README.md`](./docs/README.md) (referência)
- Volta ao [`agent/plan-base.md`](./agent/plan-base.md) (decisões)

---

## ✅ Pronto Para Começar?

**Checklist inicial:**
- [ ] Java 17+ instalado
- [ ] Maven instalado
- [ ] Docker + Docker Compose instalado
- [ ] Git configurado
- [ ] IDE aberta no projeto
- [ ] Leu [`INDEX.md`](./INDEX.md)
- [ ] Leu [`docs/PRD.md`](./docs/PRD.md)

**Próximo passo:**
```bash
git checkout -b feature/contracts
# Implemente conforme tasks/00-feature-contracts.md
mvn clean verify
git push origin feature/contracts
# Open PR
```

---

## 📞 Referência Rápida

| Tópico | Arquivo | Seção |
|---|---|---|
| Comece aqui | `INDEX.md` | Tudo |
| Stack obrigatória | `docs/PRD.md` | 3 (RNF1) |
| Proibições | `docs/PRD.md` | 3 (RNF2) |
| Segurança SQL | `docs/PRD.md` | 6 (SP1) |
| Segurança XSS | `docs/PRD.md` | 6 (SP2) |
| Validação | `docs/PRD.md` | 6 (SP3) |
| Tasks | `tasks/README.md` | Tudo |
| Task 1 (Contratos) | `tasks/00-feature-contracts.md` | Tudo |
| Task 0 (Infra) | `tasks/01-task-0-infra.md` | Tudo |
| Task 1 (DAO) | `tasks/02-task-1-dao.md` | Tudo |
| Task 2 (Service) | `tasks/03-task-2-service.md` | Tudo |
| Task 3 (Web) | `tasks/04-task-3-web.md` | Tudo |
| Extensões | `tasks/05-07-*.md` | Tudo |

---

## 🎓 Aprendizados Esperados

✅ Backend seguro (JDBC, PreparedStatement, validação)  
✅ Padrões de design (MVC, DIP, Test Doubles)  
✅ Frontend defensivo (JSP, `<c:out>`, i18n)  
✅ Infraestrutura moderna (Docker, Flyway, Maven)  
✅ Qualidade de código (TDD, Javadoc, Git)  
✅ Documentação técnica (UML, arquitetura)  

---

## 🎬 Linha do Tempo

```
Hoje (2026-08-12)
├─ Documentação COMPLETA ✅
│  ├─ PRD (requisitos + arquitetura)
│  ├─ 7 tasks detalhadas (contratos, código, testes)
│  └─ Referência (README, troubleshooting)
│
Semana 1-2
├─ Implementar core (tasks 0-3)
├─ Build verde, testes OK
└─ PRs merged em delivery/core
│
Semana 3-4
├─ Integração (delivery/core → release)
├─ Começar extensões (TMDB, frontend)
└─ Testes integração
│
Semana 5-6
├─ Extensões prontas
├─ Documentação final (PDF, diagramas)
└─ Release final (release/pit-catalog → main, tag v1.0)
│
Final
└─ Projeto completo, seguro, testado, documentado ✅
```

---

**Projeto Integrador (PIT) — Catálogo de Mídia em Java Web**  
**Versão 1.0 | 2026-08-12**  
**Documentação Completa | Pronto para Implementação**

---

🚀 **Boa sorte com o projeto!**

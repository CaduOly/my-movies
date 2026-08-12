# 📚 Documentação: Catálogo de Mídia (PIT)

## 🎯 Começar Por Aqui

1. **[PRD.md](./PRD.md)** — Product Requirements Document (5 min read)
   - Visão geral, escopo, requisitos funcionais/não-funcionais
   - Modelo de domínio (MediaItem)
   - Arquitetura MVC + DIP
   - Segurança (SQL Injection, XSS, Validação)

2. **[../tasks/README.md](../tasks/README.md)** — Mapa de Tarefas (2 min read)
   - Índice de todas as 7 tarefas
   - Ordem de execução
   - Dependências entre tasks

3. **Documentação Detalhada** — Escolha uma task:
   - [`../tasks/00-feature-contracts.md`](../tasks/00-feature-contracts.md) — Contratos & Modelo (1ª)
   - [`../tasks/01-task-0-infra.md`](../tasks/01-task-0-infra.md) — Infraestrutura (Docker, Flyway)
   - [`../tasks/02-task-1-dao.md`](../tasks/02-task-1-dao.md) — DAO (CRUD + Search, **SQL Injection tests**)
   - [`../tasks/03-task-2-service.md`](../tasks/03-task-2-service.md) — Service (Validação + Transações)
   - [`../tasks/04-task-3-web.md`](../tasks/04-task-3-web.md) — Web (Servlet, JSP, **XSS tests**)
   - [`../tasks/05-task-4-tmdb.md`](../tasks/05-task-4-tmdb.md) — TMDB Autofill (Bonus)
   - [`../tasks/06-task-5-frontend-home.md`](../tasks/06-task-5-frontend-home.md) — Frontend (Grid/Carrossel)
   - [`../tasks/07-task-6-rating.md`](../tasks/07-task-6-rating.md) — Rating (Estrelas)

---

## 🏗️ Arquitetura (Overview)

```
HTTP Request
    ↓
┌─────────────────────────────┐
│  Servlet Controller         │  (orquestra, zero SQL/lógica)
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  CatalogService + Validator │  (regras de negócio, validação)
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  MediaItemDAO (JDBC)        │  (persistência, ZERO concatenação)
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  MySQL 8 (utf8mb4)          │
└─────────────────────────────┘
    ↑
    └─ Flyway (migrations)
    ↓
┌─────────────────────────────┐
│  JSP + EL + JSTL            │  (view, ZERO scriptlets)
│  <c:out> em toda saída      │  (XSS protection)
└─────────────────────────────┘
    ↓
HTTP Response (HTML)
```

### Camadas

| Camada | Classe | Responsabilidade | Testa com |
|---|---|---|---|
| Controller | `MediaController` (Servlet) | Orquestra fluxo HTTP | Functional tests (mocks de Service) |
| Business | `CatalogService` | Lógica de negócio, validação | Unit tests (DAO mocked) |
| Validation | `MediaItemValidator` | Regras semânticas | Unit tests (sem banco) |
| Persistence | `MediaItemDAO` (interface) | CRUD + Search | Integration tests (banco real) |
| DB | MySQL 8 | Persistência | N/A |
| View | JSP + JSTL | Renderização HTML | E2E tests (Selenium, futuro) |

---

## 🔐 Segurança

### SP1: SQL Injection ✓
**Ameaça:** `SELECT * FROM item_media WHERE title LIKE '%'; DROP TABLE item_media; --%'`

**Controle:** `PreparedStatement` com parâmetros separados

**Verificação:**
```bash
# Task 1 (DAO) tem teste:
testSearchInjectionDelete() → input `'; DROP TABLE...` retorna vazio, schema intacto
testSearchInjectionUnion() → input `' UNION ...` retorna vazio
```

### SP2: XSS (Cross-Site Scripting) ✓
**Ameaça:** `<script>alert('xss')</script>` é executado no navegador

**Controle:** `<c:out value="${item.title}" />` escapa HTML

**Verificação:**
```bash
# Task 3 (Web) tem teste:
testXssEscape() → entrada `<script>...` renderiza como texto literal em HTML
```

### SP3: Validação de Entrada ✓
**Ameaça:** Dados inválidos quebram lógica ou banco

**Controle:** Validação semântica em `MediaItemValidator`

**Regras:**
- `title` obrigatório, 1-255 chars
- `mediaType` obrigatório
- `releaseYear` opcional, 1800-2100
- `rating` opcional, 0-5

**Verificação:**
```bash
# Task 2 (Service) tem testes:
testReleaseYearInvalid() → fora de faixa lança ValidationException
testRatingOutOfRange() → > 5 lança ValidationException
```

---

## 📊 Cronograma Estimado

| Semana | Tasks | Estimativa | Status |
|---|---|---|---|
| 1 | Contracts + Infra | 3 + 5 = 8 pts | ⬜ |
| 2 | DAO CRUD + Search | 8 pts | ⬜ |
| 3 | Service + Web (CRUD) | 6 + 8 = 14 pts | ⬜ |
| 4 | Web (finishers) | — | ⬜ |
| 5+ | Extensões (TMDB, Frontend, Rating) | 8 + 5 + 3 = 16 pts | ⬜ |

**Total:** ~30 pts core + 16 bonus = 46 pts  
**Velocity:** ~8 pts/semana → **6 semanas**

---

## ✅ Checklist de Conformidade

### Antes de Commitar

- [ ] `mvn clean compile` — compila sem erros
- [ ] `mvn test` — testes passam 100%
- [ ] `mvn javadoc:javadoc` — Javadoc sem warnings
- [ ] Nenhuma proibição (Spring, Lombok, scriptlets, hardcoded secrets)
- [ ] Commits claros (Conventional Commits)
- [ ] PR pequena (um comportamento/feature)

### Fim de Cada Task (Definition of Done)

- [ ] Critérios de Aceite atendidos (veja arquivo da task)
- [ ] Build verde
- [ ] Testes > 70% cobertura
- [ ] Código revisado contra plan.md seções 1, 2, 3
- [ ] Javadoc PT-BR completo, sem warnings
- [ ] Sem dead code / imports não usados
- [ ] Sem TODOs/FIXMEs esquecidos

### Fim do Projeto (Release)

- [ ] Todas as 5 tarefas core completas
- [ ] PRs merged em `delivery/core`
- [ ] `delivery/core` mergeada em `release/pit-catalog`
- [ ] Documentação final (PDFs, diagramas, manual)
- [ ] Histórico Git limpo (branches deletadas, tags criadas)
- [ ] `release/pit-catalog` → `main` (tag v1.0)

---

## 🛠️ Executar Localmente

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker + Docker Compose
- Git

### Setup

```bash
# 1. Clone repo
git clone <repo>
cd my-movies

# 2. Build
mvn clean package

# 3. Start Docker (MySQL + Tomcat)
docker compose up -d

# 4. Aguarde ~10s (healthcheck)
sleep 10

# 5. Acesse
curl -s http://localhost:8080 | head -20
# ou abra no navegador: http://localhost:8080
```

### Parar

```bash
docker compose down
```

### Reset (limpar dados)

```bash
docker compose down -v
```

---

## 📂 Estrutura de Pastas

```
my-movies/
├── docs/
│   ├── PRD.md                      ← Este arquivo é PRD completo
│   └── README.md                   ← Este arquivo (você está aqui)
├── tasks/
│   ├── 00-feature-contracts.md
│   ├── 01-task-0-infra.md
│   ├── 02-task-1-dao.md
│   ├── 03-task-2-service.md
│   ├── 04-task-3-web.md
│   ├── 05-task-4-tmdb.md
│   ├── 06-task-5-frontend-home.md
│   ├── 07-task-6-rating.md
│   └── README.md                   ← Índice de tasks
├── src/
│   ├── main/
│   │   ├── java/com/seu/catalog/
│   │   │   ├── model/              (MediaItem, MediaType)
│   │   │   ├── dao/                (interfaces, MySqlMediaItemDAO)
│   │   │   ├── service/            (CatalogService, Validator, Providers)
│   │   │   ├── servlet/            (MediaController)
│   │   │   ├── exception/          (DAOException, etc)
│   │   │   └── infra/              (ConnectionFactory, AppBootstrap)
│   │   ├── webapp/
│   │   │   ├── WEB-INF/jsp/        (views: list, form, detail, etc)
│   │   │   ├── css/                (style.css)
│   │   │   └── js/                 (carousel.js, rating.js)
│   │   └── resources/
│   │       ├── db/migration/       (V1__create_item_media.sql, V2__seed_data.sql)
│   │       ├── messages_pt_BR.properties
│   │       └── messages_en.properties
│   └── test/
│       └── java/com/seu/catalog/   (tests)
├── pom.xml
├── docker-compose.yml
├── Dockerfile
├── Makefile
└── README.md                        (como rodar)
```

---

## 🔗 Referências Rápidas

| Tópico | Documento | Seção |
|---|---|---|
| Stack obrigatória | PRD | 3 (RNF1) |
| Proibições | PRD | 3 (RNF2) |
| Javadoc padrão | PRD | Não tem; vide `tasks/00-feature-contracts.md` |
| TDD ordem | PRD | Não tem; vide plan-base.md seção 4 |
| Git workflow | PRD | 7 (Git Workflow) |
| Segurança SQL Injection | PRD | 6 (SP1) |
| Segurança XSS | PRD | 6 (SP2) |
| Validação | PRD | 6 (SP3) |
| Layout CSS | PRD | 7 (Layout Front) |

---

## 📞 Dúvidas / Bloqueadores

1. **Técnicas:** Veja `tasks/NN-task-X.md` correspondente
2. **Arquitetura:** Veja PRD seção 5
3. **Segurança:** Veja PRD seção 6
4. **Git:** Veja PRD seção 7
5. **Padrões:** Veja `agent/plan-base.md`

---

## 👤 Contribudores

- **Product Owner:** Projeto Integrador (PIT)
- **Arquiteto:** Plan-base.md
- **Implementador:** Você!

---

**Versão 1.0 | 2026-08-12**  
**Projeto Integrador (PIT) — Catálogo de Mídia em Java Web**

---

## 🎓 Aprendizados Esperados

Ao completar este projeto, você terá:

✅ **Backend seguro**
- JDBC com `PreparedStatement` (zero SQL Injection)
- Validação semântica em Service
- Transações (try-catch-finally)

✅ **Padrões de Design**
- MVC em camadas (Servlet → Service → DAO)
- Dependency Injection (construtor)
- Test Doubles (mock, stub, fake)

✅ **Frontend defensivo**
- JSP com `<c:out>` (zero XSS)
- Internacionalização (i18n)
- CSS responsivo

✅ **Infraestrutura moderna**
- Docker + Docker Compose
- Flyway para versionamento de schema
- Maven build + CI ready

✅ **Qualidade de código**
- TDD (Red → Green → Refactor)
- Javadoc completo (PT-BR)
- Git workflow profissional
- Code review checklist

✅ **Documentação técnica**
- UML (casos de uso, classes, DER)
- Arquitetura em diagramas
- Manual do usuário

---

Boa sorte! 🚀

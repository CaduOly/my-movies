# 🎯 Índice Geral — Catálogo de Mídia (PIT)

> **Tl;dr:** Leia [PRD](./docs/PRD.md) (5 min), depois pegue [primeira task](./tasks/00-feature-contracts.md).

---

## 📍 Você Está Aqui

```
projeto-my-movies/
├── INDEX.md ..................... ← Você está aqui (início rápido)
├── docs/
│   ├── PRD.md ................... Requisitos + Arquitetura (leia 1º)
│   └── README.md ................ Visão geral + referências
├── tasks/
│   ├── README.md ................ Mapa de todas as 7 tarefas
│   ├── 00-feature-contracts.md .. 1ª Task (Modelo + Interfaces)
│   ├── 01-task-0-infra.md ....... Infra (Docker, MySQL, Flyway)
│   ├── 02-task-1-dao.md ......... DAO CRUD + Search **[SQL Injection Tests]**
│   ├── 03-task-2-service.md ..... Service + Validação
│   ├── 04-task-3-web.md ......... Servlet + JSP **[XSS Tests]**
│   ├── 05-task-4-tmdb.md ........ TMDB Autofill (Bonus)
│   ├── 06-task-5-frontend-home.md Grid/Carrossel (Bonus)
│   └── 07-task-6-rating.md ...... Avaliação (Bonus)
├── src/main/java/...
├── src/main/webapp/...
├── src/test/java/...
├── pom.xml
└── docker-compose.yml
```

---

## ⚡ Início Rápido

### 1. Entender o Projeto (5 min)
```bash
# Leia o PRD (Product Requirements Document)
cat docs/PRD.md

# Resultado: você sabe:
# - Stack: Java 17, Servlet, JSP, JDBC, MySQL, Flyway
# - Modelo: MediaItem (título, autor, ano, tipo, etc)
# - Segurança: SQL Injection + XSS + Validação
# - Arquitetura: MVC em camadas + DIP
```

### 2. Ver Mapa de Tasks (2 min)
```bash
# Veja índice de todas as tarefas
cat tasks/README.md

# Resultado: você sabe:
# - 5 tasks core (obrigatórias) + 2 extensões
# - Ordem de execução
# - Dependências
```

### 3. Pegar Primeira Task (30 min)
```bash
# Leia documentação detalhada da 1ª task
cat tasks/00-feature-contracts.md

# Resultado: você sabe:
# - O que implementar (MediaItem, interfaces, exceções)
# - Como testar (nenhum teste aqui, é POJO)
# - Definition of Done (checklist antes de commitar)
```

### 4. Implementar
```bash
# Crie as classes conforme a tarefa descreve
# Siga TDD: Red → Green → Refactor

# Build
mvn clean package

# Teste
mvn test

# Javadoc
mvn javadoc:javadoc
```

### 5. Próxima Task
```bash
# Após merge de feature/contracts, passe para:
cat tasks/01-task-0-infra.md

# Ela depende de contracts (que já mergou)
# Você pode fazer ela em paralelo com outras
```

---

## 🎓 Estrutura de Cada Task

Todo arquivo `tasks/NN-task-X-name.md` segue este padrão:

```markdown
# Task N: Descrição

**Entrega:** delivery/core ou delivery/xxx
**Branch:** feature/name
**Estimativa:** X pts
**Prioridade:** 🔴 BLOQUEADOR ou 🟢 BONUS
**Depende:** Tasks Y, Z (vazio se nenhuma)
**Cobre:** SituaçãoProblema (SP1, SP2, etc)

## Objetivo
O que você vai entregar.

## Escopo
1. Implementação (código fonte, classes, métodos)
2. Testes (unit, integração, funcional)
3. Documentação (Javadoc)

## Travas (Constraints Críticas)
🔴 TRAVA 1: Restrição dura que não pode quebrar
🔴 TRAVA 2: ...

## Critérios de Aceite
- [ ] Build compila
- [ ] Testes passam
- [ ] Sem warnings Javadoc
- [ ] Nenhuma proibição
- [ ] Commits claros

## Definition of Done (Checklist Final)
[Antes de PR, verificar tudo isto]

## Próximos Passos
Qual task fazer depois desta.
```

---

## 🔐 Segurança: Os 3 Pontos Críticos

### SP1: SQL Injection
**Onde:** Task 1 (DAO)  
**O que:** Entrada maliciosa (`'; DROP TABLE...`) não quebra banco  
**Teste:** `testSearchInjectionDelete()` — retorna vazio, schema intacto  
**Controle:** `PreparedStatement` com `setString()`, zero concatenação

### SP2: XSS (Cross-Site Scripting)
**Onde:** Task 3 (Web/JSP)  
**O que:** `<script>alert('xss')</script>` renderiza como texto, não executa  
**Teste:** Adicione item com `<script>...`, verifique HTML source  
**Controle:** `<c:out value="${item.title}" />` escapa tudo

### SP3: Validação de Entrada
**Onde:** Task 2 (Service)  
**O que:** Dados inválidos (year=abc, rating=10) lançam `ValidationException`  
**Teste:** `testReleaseYearInvalid()`, `testRatingOutOfRange()`  
**Controle:** `MediaItemValidator` com regras semânticas

---

## 📊 Pontos (Estimativa)

| Task | Core | Bonus | Descrição |
|---|---|---|---|
| Contracts | 3 | — | Modelo + Interfaces (POJO, sem impl) |
| Infra | 5 | — | Docker, MySQL, Flyway |
| DAO | 8 | — | CRUD + Search, testes de injection |
| Service | 6 | — | Validação + transações |
| Web | 8 | — | Servlet + JSP, testes XSS |
| **Core Total** | **30** | | |
| TMDB | — | 8 | Autofill de metadados via API |
| Frontend | — | 5 | Grid/carrossel de capas |
| Rating | — | 3 | Estrelas + comentário |
| **Bonus Total** | | **16** | |
| **GRANDE TOTAL** | **30** | **16** | **46 pontos** |

**Velocity estimada:** 8 pts/semana → **6 semanas** (4 core + 2 bonus)

---

## ✅ Checklist Rápido Antes de Começar

- [ ] Java 17+ instalado (`java -version`)
- [ ] Maven instalado (`mvn -v`)
- [ ] Docker + Docker Compose (`docker -v`, `docker compose -v`)
- [ ] Git configurado (`git config user.name`)
- [ ] VSCode/IntelliJ aberto no diretório do projeto
- [ ] Terminal pronto para comandos Maven

---

## 🚀 Primeiro Comando

```bash
# Clone, entre no diretório
cd my-movies

# Verifique setup (opcionalmente)
mvn -v
docker -v
java -version

# Start (após feature/contracts + feature/infra prontas)
docker compose up -d
mvn clean package
curl -s http://localhost:8080 | head -20

# Para
docker compose down
```

---

## 🎯 Próximas Tarefas (Pela Ordem)

```
1️⃣  tasks/00-feature-contracts.md
     └─ Branch: feature/contracts
     └─ PR para: delivery/core
     └─ Espera por: nada (1ª!)
     └─ Libera: todo o resto (fornece contratos)

2️⃣  tasks/01-task-0-infra.md
     └─ Branch: feature/infra
     └─ PR para: delivery/core
     └─ Espera por: feature/contracts merged
     └─ Paralelo com: Task 1, 2 (compartilham contratos)

3️⃣  tasks/02-task-1-dao.md
     └─ Paralelo com Task 0, 2
     └─ PR para: delivery/core
     └─ **CRÍTICO:** Testes de SQL Injection

4️⃣  tasks/03-task-2-service.md
     └─ Paralelo com Task 0, 1
     └─ PR para: delivery/core
     └─ Espera por: Task 1 (DAO ready)

5️⃣  tasks/04-task-3-web.md
     └─ PR para: delivery/core
     └─ Espera por: Task 2 (Service ready)
     └─ **CRÍTICO:** Testes XSS

6️⃣  delivery/core → release/pit-catalog (integração final)

7️⃣  Extensões (Tasks 4-6) em paralelo
     └─ Dependem de Tasks 0-3
     └─ PRs para: release/pit-catalog
```

---

## 💡 Dicas Importantes

### Git Workflow
```bash
# Cada task = nova branch
git checkout main
git pull
git checkout -b feature/contracts

# Work... commit... test...

# Push + PR
git push origin feature/contracts
# Open PR on GitHub
```

### Build Local
```bash
# Sempre antes de commitar
mvn clean verify

# Se tiver erro, corrija e rode novamente
# Não commita se tiver erro
```

### Executar Testes
```bash
# Um arquivo de teste
mvn test -Dtest=MySqlMediaItemDAOTest

# Todos os testes
mvn test

# Cobertura
mvn jacoco:report
open target/site/jacoco/index.html
```

### Javadoc
```bash
# Gerar
mvn javadoc:javadoc

# Abrir
open target/site/apidocs/index.html

# Verificar warnings (deve ser zero!)
mvn javadoc:javadoc 2>&1 | grep -i "warning:"
```

---

## 📖 Documentação de Referência

| Arquivo | Leia quando | Tempo |
|---|---|---|
| [`docs/PRD.md`](./docs/PRD.md) | Antes de começar | 5 min |
| [`tasks/README.md`](./tasks/README.md) | Antes de pegar 1ª task | 2 min |
| [`tasks/00-feature-contracts.md`](./tasks/00-feature-contracts.md) | Vou fazer essa | 15 min |
| [`agent/plan-base.md`](./agent/plan-base.md) | Referência (problemas, decisões) | 10 min |
| [`docs/README.md`](./docs/README.md) | Quando ficar perdido | 10 min |

---

## 🆘 Troubleshooting

| Problema | Solução |
|---|---|
| `mvn: command not found` | Instale Maven ou adicione ao PATH |
| `docker: command not found` | Instale Docker |
| `Connection refused` (MySQL) | Rode `docker compose up -d` ou verifique porta 3306 |
| `Javadoc warnings` | Corrija comentários; veja `target/javadoc.errors` |
| `Tests failing` | Rode individualmente com `-Dtest=ClassName` |
| `Git conflicts` | Rebase ou merge conforme workflow da branch |

---

## 🎓 Conceitos Importantes

- **TDD:** Red (teste falha) → Green (impl mínima) → Refactor
- **DIP:** Dependency Injection via construtor
- **MVC:** Model (MediaItem) + View (JSP) + Controller (Servlet)
- **Prepared Statement:** Parametrização de SQL
- **try-with-resources:** Auto-close de Streams/Connections
- **JSTL:** Loops e lógica em JSP (sem scriptlets)

---

## 🎬 Resumo Executivo

```
Projeto: Catálogo de Mídia em Java Web

O quê:
  • CRUD de filmes/séries/livros
  • Persistência em MySQL
  • Interface web com JSP
  • Segurança (SQL Injection, XSS, Validação)

Como:
  • Java 17 + Servlet + JDBC + Flyway
  • MVC em camadas + DIP
  • TDD com JUnit 5
  • Docker para local dev
  • Git workflow com branches protegidas

Quando:
  • Core: 4 semanas (30 pts)
  • Bonus: 2 semanas (16 pts)
  • Total: 6 semanas

Sucesso = Seguro + Testado + Documentado + Deploável
```

---

## 🚀 Vamos Começar!

**Próximo passo:** Abra [`docs/PRD.md`](./docs/PRD.md) e leia em 5 minutos.

Depois, pegue [`tasks/00-feature-contracts.md`](./tasks/00-feature-contracts.md) e começar a implementar.

```bash
# Command rápido
git checkout -b feature/contracts
# ... implement conforme tasks/00-feature-contracts.md
mvn clean verify
git push origin feature/contracts
# Open PR
```

---

**Versão 1.0 | 2026-08-12**  
Projeto Integrador (PIT) — Catálogo de Mídia em Java Web

Boa sorte! 🎯

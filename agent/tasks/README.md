# 📋 Tasks do Projeto: Catálogo de Mídia (PIT)

## Mapa de Tarefas

Este diretório contém a documentação detalhada de cada tarefa do projeto. **Leia nesta ordem:**

### 📌 Leia Primeiro
- [`PRD.md`](../docs/PRD.md) ← **Product Requirements Document** (visão geral, escopo, requisitos)

---

## 🎯 Núcleo (Obrigatório) — `delivery/core`

| # | Task | Branch | Est. | Arquivo | Status |
|---|---|---|---|---|---|
| — | **Contratos & Modelo** | `feature/contracts` | 3pts | [`00-feature-contracts.md`](./00-feature-contracts.md) | ⬜ Não iniciado |
| 0 | **Infra** (Compose, MySQL, Flyway) | `feature/infra` | 5pts | [`01-task-0-infra.md`](./01-task-0-infra.md) | ⬜ Não iniciado |
| 1 | **DAO CRUD + Search** (seguro) | `feature/dao-*` | 8pts | [`02-task-1-dao.md`](./02-task-1-dao.md) | ⬜ Não iniciado |
| 2 | **Service + Validação** | `feature/validation`, `feature/service-crud` | 6pts | [`03-task-2-service.md`](./03-task-2-service.md) | ⬜ Não iniciado |
| 3 | **Web** (Servlet, JSP, Layout) | `feature/web-*`, `feature/layout` | 8pts | [`04-task-3-web.md`](./04-task-3-web.md) | ⬜ Não iniciado |

**Total Núcleo:** ~30 pts (4 semanas @ 8pts/semana)

---

## 🚀 Extensões (Bonus) — Após Núcleo Completo

| # | Task | Branch | Est. | Arquivo | Depende | Status |
|---|---|---|---|---|---|---|
| 4 | **TMDB Autofill** | `feature/tmdb-search` | 8pts | [`05-task-4-tmdb.md`](./05-task-4-tmdb.md) | Tasks 0-3 | ⬜ Não iniciado |
| 5 | **Frontend Home** (Grid/Carrossel) | `feature/home-grid` | 5pts | [`06-task-5-frontend-home.md`](./06-task-5-frontend-home.md) | Tasks 0-3, 4 | ⬜ Não iniciado |
| 6 | **Rating** (Estrelas + Comentário) | `feature/rating-ui` | 3pts | [`07-task-6-rating.md`](./07-task-6-rating.md) | Tasks 0-3, 5 | ⬜ Não iniciado |

**Total Extensões:** ~16 pts (2 semanas de bonus)

---

## 🔗 Fluxo de Dependências

```
feature/contracts (1ª PR)
    ↓
feature/infra + feature/dao-* + feature/validation + feature/service-crud
(podem rodar em paralelo, compartilhando contratos)
    ↓
feature/web-* + feature/layout (Web aguarda Service)
    ↓
delivery/core → release/pit-catalog
    ↓
[EXTENSÕES: TMDB, Frontend, Rating rodam em paralelo]
    ↓
release/pit-catalog → main (tag v1.0)
```

---

## 📊 Checklist de Conformidade

### Por Task

Cada arquivo tem **Critérios de Aceite** e **Definition of Done**.

**Antes de commitar, verificar:**
- [ ] Compila (`mvn clean compile`)
- [ ] Testes passam (`mvn test`)
- [ ] Javadoc OK (`mvn javadoc:javadoc` sem warnings)
- [ ] Nenhuma proibição do plan.md seção 2
- [ ] Commits claros (Conventional Commits)
- [ ] PR pequena (um comportamento)

### Geral (Sempre)

- [ ] Nenhuma senha em Git
- [ ] Charset UTF-8 em tudo
- [ ] `java.util.logging`, não `System.out.println`
- [ ] Sem Lombok, Spring, Hibernate
- [ ] JDBC com `PreparedStatement` (zero SQL Injection)
- [ ] JSP com `<c:out>` (zero XSS)

---

## 🚦 Como Usar Este Diretório

### 1. Antes de Iniciar Uma Task

```bash
# Leia o arquivo da task
cat tasks/NN-task-X-name.md

# Entenda:
# - Objetivo
# - Travas (constraints)
# - Critérios de Aceite
# - Próximos Passos
```

### 2. Durante a Implementação

```bash
# Trabalhe contra os Checkpoints listados
# - A cada checkpoint, rode testes
# - Quando acabar, check Definition of Done
```

### 3. Antes de Abrir PR

```bash
# Verifique checklist de conformidade
# - mvn clean verify
# - mvn javadoc:javadoc
# - Sem TODOs/FIXMEs esquecidos
```

---

## 📖 Documentação Relacionada

- **[`../docs/PRD.md`](../docs/PRD.md)** — Visão geral, escopo, requisitos, arquitetura
- **[`../agent/plan-base.md`](../agent/plan-base.md)** — Plano detalhado do projeto (original)
- **`../README.md`** — Como rodar a aplicação
- **`../docker-compose.yml`** — Setup Docker (Task 0)

---

## 🎓 Referências Importantes

### Pontos Inegociáveis (Plan.md Seção 1)

1. **Java 17+**, POO real, MVC em camadas
2. **JDBC puro** com `PreparedStatement` (zero concatenação)
3. **Flyway** para migrations (schema versionado)
4. **JSP + EL + JSTL** (sem scriptlets)
5. **Testes:** JUnit 5 (unit + integração)
6. **Javadoc** obrigatório em PT-BR
7. **Git:** branches protegidas, PRs descritivas

### Proibições (Plan.md Seção 2)

❌ Spring / Hibernate / ORM  
❌ React / SPA  
❌ Lombok  
❌ Scriptlets em JSP  
❌ Lógica em Servlet/JSP  
❌ Credenciais em Git  

### Padrão Javadoc (Plan.md Seção 3)

```java
/**
 * Insere um novo item de mídia no catálogo.
 *
 * @param item item a persistir; não pode ser nulo
 * @return o mesmo item com id gerado pelo banco preenchido
 * @throws DAOException se ocorrer falha de acesso ao banco
 */
public MediaItem insert(MediaItem item) throws DAOException { ... }
```

---

## 🤝 Dúvidas / Bloqueadores

Se encontrar um bloqueador:

1. **Cheque o arquivo da task** — muitas respostas estão lá
2. **Cheque o PRD** — visão geral e arquitetura
3. **Cheque o plan.md** — decisões de projeto

---

## 📝 Notas Finais

- **Tasks são independentes** (contratos = interface)
- **TDD é obrigatório** (test → implementation → refactor)
- **Não há atalhos** — segurança, qualidade e documentação vêm do planejamento
- **Valide frequentemente** — não deixe surpresas para o final

---

**Versão 1.0 | 2026-08-12**  
**Projeto Integrador (PIT) — Catálogo de Mídia em Java Web**

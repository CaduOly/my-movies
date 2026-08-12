# 🎯 COMECE AQUI: Índice de Documentação

**Projeto:** Catálogo de Mídia em Java Web (PIT)  
**Data:** 2026-08-12  
**Status:** ✅ Documentação Completa | Pronto para Implementação

---

## 📚 Leitura Recomendada (Nesta Ordem)

### 1️⃣ **Visão Geral Rápida** (2 min)
👉 **[`INDEX.md`](./INDEX.md)**
- Tl;dr do projeto
- Próximos passos
- Checklist inicial

### 2️⃣ **Product Requirements Document** (10 min)
👉 **[`PRD.md`](./docs/PRD.md)**
- Requisitos funcionais + não-funcionais
- Arquitetura MVC + DIP
- 3 Situações-Problema (SQL Injection, XSS, Validação)
- Stack obrigatória
- Git workflow

### 3️⃣ **Mapa de Tasks** (3 min)
👉 **[`tasks/README.md`](./tasks/README.md)**
- Índice de todas as 7 tarefas
- Timeline
- Dependências

### 4️⃣ **Primeira Task** (20 min)
👉 **[`00-feature-contracts.md`](./00-feature-contracts.md)**
- Modelo + Interfaces
- Exceções customizadas
- Definition of Done

---

## 📋 Tarefas (Pela Ordem)

| # | Arquivo | Pts | Prioridade | Escopo |
|---|---------|-----|-----------|--------|
| — | [`00-feature-contracts.md`](./tasks/00-feature-contracts.md) | 3 | 🔴 1ª | Modelo, Interfaces, Exceções |
| 0 | [`01-task-0-infra.md`](./tasks/01-task-0-infra.md) | 5 | 🔴 | Docker, MySQL, Flyway, i18n |
| 1 | [`02-task-1-dao.md`](./tasks/02-task-1-dao.md) | 8 | 🔴 | **CRUD + SQL Injection Tests** |
| 2 | [`03-task-2-service.md`](./tasks/03-task-2-service.md) | 6 | 🔴 | Validação + Service + DIP |
| 3 | [`04-task-3-web.md`](./tasks/04-task-3-web.md) | 8 | 🔴 | **Servlet, JSP, XSS Tests** |
| 4 | [`05-task-4-tmdb.md`](./tasks/05-task-4-tmdb.md) | 8 | 🟢 | TMDB Autofill (Bonus) |
| 5 | [`06-task-5-frontend-home.md`](./tasks/06-task-5-frontend-home.md) | 5 | 🟢 | Grid/Carrossel (Bonus) |
| 6 | [`07-task-6-rating.md`](./tasks/07-task-6-rating.md) | 3 | 🟢 | Rating (Bonus) |

---

## 📖 Referência Rápida

| Quando | Consulte |
|--------|----------|
| Sou novo no projeto | [`INDEX.md`](./INDEX.md) |
| Preciso entender requisitos | [`docs/PRD.md`](./docs/PRD.md) |
| Quero ver mapa de tarefas | [`tasks/README.md`](./tasks/README.md) |
| Vou fazer a 1ª task | [`tasks/00-feature-contracts.md`](./tasks/00-feature-contracts.md) |
| Vou fazer infra (Task 0) | [`tasks/01-task-0-infra.md`](./tasks/01-task-0-infra.md) |
| Vou fazer DAO (Task 1) | [`tasks/02-task-1-dao.md`](./tasks/02-task-1-dao.md) |
| Vou fazer Service (Task 2) | [`tasks/03-task-2-service.md`](./tasks/03-task-2-service.md) |
| Vou fazer Web (Task 3) | [`tasks/04-task-3-web.md`](./tasks/04-task-3-web.md) |
| Quero mais referência técnica | [`docs/README.md`](./docs/README.md) |
| Preciso de síntese | [`PROJETO-RESUMO.md`](./PROJETO-RESUMO.md) |
| Quero ver o que foi entregue | [`ENTREGA.md`](./ENTREGA.md) |

---

## 🚀 Comece Agora

```bash
# 1. Leia INDEX.md (2 min)
cat INDEX.md

# 2. Leia PRD.md (10 min)
cat PRD.md

# 3. Leia 1ª task (20 min)
cat 00-feature-contracts.md

# 4. Comece a codificar
git checkout -b feature/contracts
# ... implemente conforme task ...
mvn clean verify
git push origin feature/contracts
```

---

## 📊 Estatísticas

- **Arquivos:** 14 documentos
- **Linhas:** 6.387+ de documentação
- **Tamanho:** ~90 KB
- **Tasks:** 7 (5 core + 2 bonus)
- **Estimativa:** 46 pts (30 core + 16 bonus)
- **Timeline:** 6 semanas

---

## ✅ Próximo Passo

👉 **Abra [`INDEX.md`](./INDEX.md)** e comece! 🎯

---

**Projeto Integrador (PIT) — Catálogo de Mídia em Java Web**  
v1.0 | 2026-08-12

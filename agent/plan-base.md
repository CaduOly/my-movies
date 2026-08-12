# plan.md ? PIT: Media Catalog

Plano de desenvolvimento do projeto integrador (PIT) ? catálogo de mídia em Java web.
Fonte da verdade. As seções **Pontos Inegociáveis**, **Proibições**, **Padrão de Javadoc**, **TDD** e **Git Workflow** têm precedência sobre qualquer conveniência de implementação.

---

## 1. Pontos Inegociáveis (implementar à risca)

Todos derivam da orientação do projeto e **não são negociáveis**. Cada item é verificável.

### Stack obrigatória (exatamente estas ferramentas)
1. **Linguagem:** Java (17+). Nada de outra linguagem no backend.
2. **Paradigma:** POO real (encapsulamento, herança, polimorfismo), não decorativa.
3. **Controller:** Servlets ? recebem HTTP, delegam, escolhem a view. Nada além.
4. **View:** JSP com **EL** e **JSTL**. Sem lógica de negócio.
5. **Acesso a dados:** JDBC puro, sempre com **`PreparedStatement`**.
6. **SGBD:** MySQL 8 (relacional), charset `utf8mb4`.
7. **Migrations:** **Flyway** (substitui `init.sql`) ? schema e seed versionados.
8. **Build:** Maven, `packaging=war`.
9. **Servidor:** Apache Tomcat.
10. **Testes:** JUnit 5 ? unitários (validação/serviço) e integração (DAO).

### Funcionalidades mandatórias (CRUD + busca)
11. Interface web para navegação e gerenciamento.
12. Create ? cadastro de novos itens.
13. Read ? listagem de todos os itens **e** detalhes de um item.
14. Update ? edição de um item.
15. Delete ? exclusão de um item.
16. Busca simples por título ou autor/diretor (SQL parametrizado).
17. Persistência em banco relacional.

### Segurança (secure by design, desde o início)
18. **SQL Injection:** só `PreparedStatement`. Zero concatenação de entrada em SQL.
19. **XSS:** toda saída de dado do usuário escapada com `<c:out>`. Sem exceção.
20. **Validação de entrada:** obrigatoriedade, tipo/faixa (`releaseYear` numérico), comprimento máximo.

### Modelagem e documentação (entregáveis)
21. Diagrama de Casos de Uso (UML).
22. Diagrama de Classes (UML).
23. DER + script de criação (como migration Flyway).
24. **Javadoc** conforme seção 3.
25. Relatório técnico em PDF + código-fonte.
26. Manual do usuário simplificado (1?2 páginas).

### Arquitetura
27. **MVC em camadas:** `Controller (Servlet)` ? `Service` ? `DAO` ? `Banco`; `View (JSP)`.
28. **DIP** nas fronteiras voláteis (provider de metadados e DAO): dependência sobre interface, injeção manual por construtor, grafo num único *composition root* (`ServletContextListener`).

---

## 2. Proibições ? o que NÃO fazer

### Frameworks e bibliotecas proibidos
- **Spring / Spring Boot / Spring MVC** ? esconde exatamente o que está sendo avaliado.
- **Hibernate / JPA / qualquer ORM** ? o CRUD deve ser JDBC explícito.
- **Frameworks de front** (React, Vue, Angular) ? a View é JSP server-rendered. Nada de SPA.
- **Lombok** ? esconde getters/setters/construtores (o encapsulamento precisa estar visível).
- **Bibliotecas "utilitárias" pesadas** sem necessidade real.

### Práticas de código proibidas
- **`Statement` com concatenação** para SQL ? sempre `PreparedStatement`.
- **Scriptlets em JSP** (`<% %>`, `<%= %>`) ? apresentação só com EL/JSTL.
- **Lógica de negócio na Servlet ou JSP** ? regra no Service, dados no DAO.
- **`System.out.println` como log** ? usar `java.util.logging` com nível adequado.
- **Engolir exceção** (`catch` vazio) ou capturar `Exception`/`Throwable` genérico sem motivo.
- **Vazar stack trace** ao usuário ? mensagem amigável na tela, detalhe no log.
- **Credenciais/API keys no código** ou no Git ? usar env/config fora do versionamento.
- **String de UI hardcoded na JSP** ? tudo por i18n (`<fmt:message>`).
- **Identificadores misturando PT/EN** ? código 100% inglês.

### Comentários e documentação ? evitar
- **Comentário que repete o código** (`i++; // incrementa i`).
- **Código comentado / dead code** ? apaga; o histórico está no Git.
- **Javadoc vazio ou que repete o nome do método**.
- **TODOs/FIXMEs esquecidos** na entrega final.
- **Blocos gigantes de comentário** explicando o óbvio.

### Escopo ? o que NÃO construir
- **Login/autenticação/multi-usuário** ? fora do escopo; catálogo single-user.
- **Microserviços, filas, cache distribuído, Kubernetes** ? over-engineering.
- **Abstrair tudo** ? interface só nas fronteiras DIP; nada de interface para POJO.
- **Recursos que estouram o cronograma** antes do núcleo 100%.

---

## 3. Padrão de Javadoc (à risca)

- **Obrigatório** em toda classe pública e método público ? atenção redobrada em DAO, Service e Servlets.
- **Primeira frase:** resumo conciso terminando em ponto; descreve *o que faz*, não *como*.
- **Voz/tempo:** terceira pessoa do presente. Documenta o **contrato**.
- **Tags quando aplicável:** `@param` para cada parâmetro; `@return` quando há retorno; `@throws` para cada exceção checada relevante.
- **Idioma:** Javadoc em **PT**, identificadores em **inglês**.
- **Getters/setters triviais:** sem Javadoc redundante.
- **`package-info.java`** por pacote.
- **`mvn javadoc:javadoc` sem warnings.**

```java
/**
 * Insere um novo item de mídia no catálogo.
 *
 * @param item item a persistir; não pode ser nulo e deve estar validado
 * @return o mesmo item com o id gerado pelo banco preenchido
 * @throws DAOException se ocorrer falha de acesso ao banco de dados
 */
public MediaItem insert(MediaItem item) throws DAOException { ... }
```

---

## 4. TDD e ordem de implementação

### Ordem: bottom-up (inversa ao runtime)
- **Runtime (fluxo da requisição):** `Controller ? Service ? DAO ? BD` (entra pelo topo).
- **Implementação (bottom-up):** `DAO ? Service ? Controller` ? ordem **inversa** à cadeia de chamadas.
- É uma escolha, não uma regra do MVC. A alternativa **outside-in** (mockist) começa pelo Controller mockando as camadas de baixo e segue a mesma direção do runtime. Adotamos **bottom-up** por ser mais simples para projeto solo e casar com a ordem das tasks.

### O que TDD É (e o que NÃO é)
- **NÃO é** escrever todos os testes do sistema e só depois implementar (isso é test-first waterfall ? retrabalho).
- **É** o ciclo curto por comportamento: **RED** (teste que falha) ? **GREEN** (código mínimo que passa) ? **REFACTOR** (melhora com testes verdes). "Teste primeiro" vale **por unidade/feature**.

### Test doubles por camada
- **DAO ? teste de integração** contra banco de teste (Flyway migra o schema de teste; ideal Testcontainers-MySQL, fallback schema dedicado). Precisa exercitar SQL/`PreparedStatement` reais.
- **Service ? teste unitário** com **DAO stub/mock** + `FakeMovieMetadataProvider` (sem banco, sem rede).
- **Controller/Web ? teste funcional** do fluxo (request?service?banco?view).

### Comportamentos das SPs viram testes explícitos
- SP1: `searchByTerm` com payload `'; DROP TABLE item_media; --` retorna vazio e não altera schema.
- SP2: `releaseYear` inválido ? `ValidationException` (app não quebra); falha no DAO ? rollback + `ServiceException`.
- SP3 (XSS): saída com caractere `<script>` renderizada como texto literal.

### Independência entre features = contratos primeiro
Definir **contratos antes de implementações** (interfaces `MediaItemDAO`/`MovieMetadataProvider`, modelo `MediaItem`, exceptions) permite que cada camada seja desenvolvida e testada contra *interface + test double*, sem depender do código concreto da outra. **É o que viabiliza branches paralelas.**

---

## 5. Fluxo de trabalho com Git

### Hierarquia de branches
```
main                         (protegida, sempre estável; recebe só via release)
?? release/pit-catalog       (integração final; agrega as entregas completas)
   ?? delivery/core          (branch PAI da entrega ? núcleo)     [de main]
   ?   ?? feature/contracts  (interfaces + modelo + exceptions)   ? 1º PR
   ?   ?? feature/infra      (compose, flyway, i18n, bootstrap)
   ?   ?? feature/dao-crud
   ?   ?? feature/dao-search
   ?   ?? feature/validation
   ?   ?? feature/service-crud
   ?   ?? feature/web-crud
   ?   ?? feature/layout
   ?? delivery/tmdb          (extensão)   [de release, após core]
   ?? delivery/frontend-home (extensão)   [de release, após core]
   ?? delivery/rating        (extensão)   [de release, após core]
```

### Regras do fluxo
1. **`delivery/*` nasce de `main`** quando é independente; **de `release`** quando precisa de uma entrega já mergeada (caso das extensões, que dependem do núcleo).
2. **`feature/*`, `fix/*`, `chore/*` nascem da `delivery/*`** correspondente e voltam pra ela via **PR**.
3. **Contratos primeiro:** o 1º PR de cada `delivery/*` é `feature/contracts` (interfaces, modelo, exceptions com stubs). Depois disso, as features das camadas rodam em paralelo contra os contratos.
4. **Ao concluir a entrega:** PR da `delivery/*` ? **`release/pit-catalog`**.
5. **Ao concluir tudo:** PR de `release/pit-catalog` ? **`main`** + tag (`v1.0`).

### TDD dentro da branch (mantém a entrega verde)
- Uma `feature/*` **por comportamento**. Dentro dela, o **commit do teste (RED) vem antes** do commit da implementação (GREEN), depois o refactor. O histórico preserva "teste primeiro" e a `delivery/*` **nunca fica vermelha**.
- Evitar mergear branch `test/*` isolada só com testes falhando na `delivery/*` (quebra o CI da entrega). Se quiser separar `test/*`, só mergeie quando estiver verde.

### Convenção de commits (Conventional Commits)
`test:` · `feat:` · `fix:` · `refactor:` · `docs:` · `chore:`
Ordem típica numa feature: `test: add failing test for search injection` ? `feat: implement parameterized searchByTerm` ? `refactor: extract row mapper`.

### Checklist de PR (Definition of Done por PR)
- [ ] CI verde: build + todos os testes.
- [ ] `mvn javadoc:javadoc` sem warnings.
- [ ] Sem scriptlet em JSP, sem framework proibido, sem secret commitado.
- [ ] Sem dead code / comentário redundante / TODO esquecido.
- [ ] Revisado contra as seções 1, 2 e 3.
- [ ] PR pequeno, um comportamento.

---

## 6. Decisões de arquitetura

- **Idioma:** identificadores em inglês; Javadoc/comentários em PT-BR; UI i18n (`ResourceBundle` + `<fmt:message>`, `messages_pt_BR` / `messages_en`).
- **Execução:** `docker compose up` sobe MySQL 8 + Tomcat. Flyway migra no startup (composition root), antes de servir requisições.
- **Visibilidade do startup:** o `ServletContextListener` loga a URL de acesso; `Makefile`/`run.sh` imprime a URL após o healthcheck.
- **Encoding:** MySQL `utf8mb4`; JDBC `useUnicode=true&characterEncoding=UTF-8` (evita mojibake).
- **Escopo:** núcleo (Tasks 0?3) é o avaliado; extensões (Tasks 4?6) depois.

---

## 7. Layout do front (básico e funcional)

CSS puro, sem framework. Clareza acima de estética.

- **Fundo:** branco `#FFFFFF`. **Bordas/superfícies:** `#E5E7EB`.
- **Texto:** primário `#111827`, secundário `#6B7280`.
- **Acento (laranja, só em detalhes):** `#F97316`; hover `#EA580C`. Em: item ativo do menu, botão primário, links, foco. Nunca fundo de tela inteira.
- **Fonte:** stack de sistema (`-apple-system, "Segoe UI", Roboto, sans-serif`).
- **Hierarquia:** H1 28/700 · H2 22/600 · H3 18/600 · corpo 16/400 · auxiliar 14 `#6B7280`.
- **Estrutura:** menu lateral fixo à esquerda (branco, borda direita cinza; itens Início / Gerenciar biblioteca / Adicionar; ativo em laranja) + conteúdo à direita.
- **Botões:** primário laranja sólido; secundário contornado; foco visível.

---

## 8. Modelo de domínio

`MediaItem`: `id`, `title`, `authorDirector`, `releaseYear`, `genre`, `synopsis`, `mediaType` (`MOVIE`|`SERIES`|`BOOK`), `posterUrl` *(ext)*, `externalId` *(ext)*, `rating` *(ext, 0?5)*, `comment` *(ext)*.

---

# NÚCLEO (avaliado) ? `delivery/core`

> Cada task abaixo é executada **test-first** (RED?GREEN?REFACTOR) e entregue via `feature/*` ? `delivery/core`. O 1º PR da entrega é sempre `feature/contracts`.

## feature/contracts ? Fundação de contratos
**Antes de qualquer camada.** Define `MediaItem`, interface `MediaItemDAO`, interface `MovieMetadataProvider`, exceptions (`DAOException`, `ValidationException`, `ServiceException`) com stubs. Habilita desenvolvimento paralelo das camadas.
**Aceite:** compila; interfaces/modelo/exceptions definidos; nenhuma impl concreta ainda.

## Task 0 ? Infra: Compose, MySQL 8, Flyway, i18n, startup URL
**Épico:** Infraestrutura · **Estimativa:** 5 pts · **Branch:** `feature/infra`

### Escopo
1. Maven `war`; pacotes `com.<seu>.catalog.{model,dao,service,servlet,infra,config}`.
2. `docker-compose.yml`: `db` (MySQL 8 `utf8mb4`, healthcheck) + `app` (Tomcat, WAR, `depends_on healthy`, `ports 8080:8080`).
3. **Flyway** + migrations `V1__create_item_media.sql`, `V2__seed_data.sql`.
4. Migração programática no startup (composition root).
5. i18n (`messages_pt_BR`/`messages_en`, taglib `fmt`).
6. Startup: listener loga `? http://localhost:8080<ctx>`; `make up` imprime URL após healthcheck.

### Aceite
- [ ] `docker compose up` sobe banco+app; Flyway aplica `V1`/`V2`.
- [ ] URL de acesso aparece no log e no `make up`.
- [ ] Home com seed, sem mojibake; **sem `init.sql`**.

## Task 1 ? Foundation: schema, entity e DAO seguro
**Épico:** Persistência · **Estimativa:** 8 pts · **Branches:** `feature/dao-crud`, `feature/dao-search`
**Cobre:** SP1 + parte da SP3

### Testes primeiro (RED)
- Integração do CRUD contra banco de teste; teste de injeção em `searchByTerm`.

### Escopo
1. Migration `V1` de `item_media` (colunas de extensão nuláveis).
2. `MediaItem` (POJO encapsulado; sem Lombok).
3. `ConnectionFactory` (credenciais fora do código).
4. `MySqlMediaItemDAO implements MediaItemDAO`: `insert`,`findAll`,`findById`,`update`,`delete`,`searchByTerm`.

```sql
-- V1__create_item_media.sql
CREATE TABLE item_media (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author_director VARCHAR(255),
    release_year INT,
    genre VARCHAR(100),
    synopsis TEXT,
    media_type VARCHAR(20) NOT NULL,
    poster_url VARCHAR(500), external_id VARCHAR(50), rating INT, comment TEXT,
    INDEX idx_title (title), INDEX idx_author_director (author_director)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
```java
String sql = "SELECT * FROM item_media WHERE title LIKE ? OR author_director LIKE ?";
try (Connection c = ConnectionFactory.get();
     PreparedStatement st = c.prepareStatement(sql)) {
    String like = "%" + term + "%";
    st.setString(1, like); st.setString(2, like);  // entrada como DADO
}
```

### Aceite
- [ ] CRUD + `searchByTerm`; zero concatenação em SQL.
- [ ] Teste com `'; DROP TABLE item_media; --` ? vazio, schema intacto.
- [ ] `try-with-resources` em todo recurso; Javadoc (PT) nos públicos.

## Task 2 ? Service, validação, exceções e fronteira DIP
**Épico:** Domínio · **Estimativa:** 6 pts · **Branches:** `feature/validation`, `feature/service-crud`
**Cobre:** SP2 + parte da SP3

### Testes primeiro (RED)
- Unit do `MediaItemValidator` (casos válidos/inválidos); unit do `CatalogService` com DAO stub e `FakeMovieMetadataProvider`; teste de rollback.

### Escopo
1. `MediaItemValidator` (obrigatoriedade, tipo/faixa de `releaseYear`, comprimentos).
2. `CatalogService` com `try-catch-finally` + transação.
3. (`MovieMetadataProvider` já veio de `feature/contracts`.)

```java
Connection c = null;
try { c = ConnectionFactory.get(); c.setAutoCommit(false); dao.insert(item, c); c.commit(); }
catch (SQLException | DAOException e) { rollbackQuietly(c); throw new ServiceException("Falha ao cadastrar item.", e); }
finally { closeQuietly(c); }  // finally SEMPRE libera
```

### Aceite
- [ ] `releaseYear="abc"` não derruba a app; erro legível.
- [ ] Falha no DAO ? rollback + `ServiceException` com `cause`.
- [ ] Conexão fechada no `finally`; service testável sem banco/rede.

## Task 3 ? Web: Controllers, JSP, i18n, anti-XSS, layout
**Épico:** Apresentação · **Estimativa:** 8 pts · **Branches:** `feature/web-crud`, `feature/web-search`, `feature/layout`
**Cobre:** fecha SP3 + segurança de saída

### Testes primeiro (RED)
- Funcional do fluxo cadastrar?listar?buscar; teste de escape XSS na view.

### Escopo
1. Servlet(s) controller (`new`,`save`,`list`,`detail`,`edit`,`update`,`delete`,`search`) ? só orquestração.
2. `home.jsp`, `manage.jsp`, `form.jsp` (reuso), `detail.jsp`; menu lateral via `<jsp:include>`.
3. CSS base (seção 7).
4. `AppBootstrap` (`@WebListener`): Flyway + grafo de dependências + log da URL.
5. Saída `<c:out>` + `<fmt:message>`; **sem scriptlet**.

### Aceite
- [ ] CRUD + busca pela UI com layout base.
- [ ] Servlet sem SQL/regra; nenhuma JSP com scriptlet.
- [ ] Toda saída via `<c:out>`; UI via `<fmt:message>` (PT/EN).
- [ ] `form.jsp` reaproveitado; teste E2E verde.

---

# EXTENSÕES (opcionais ? após o núcleo)

## Task 4 ? TMDB autofill · `delivery/tmdb`
**Estimativa:** 8 pts · **Depende de:** Tasks 2 e 3 · **Escopo:** filme/série (não livro).
- `TmdbMetadataProvider implements MovieMetadataProvider` (única classe que conhece HTTP/key/JSON); injeção no `AppBootstrap`; `/search/movie`, `/movie/{id}?append_to_response=credits`, gênero via `/genre/movie/list`, poster `w500`. Timeout+fallback; key fora do código; **atribuição TMDB**; chamada server-side.
- **Testes primeiro:** `search`/`findById` com `FakeMovieMetadataProvider`; fallback quando indisponível.

## Task 5 ? Home (grid/carrossel) + detalhe · `delivery/frontend-home`
**Estimativa:** 5 pts · **Depende de:** Task 3 (+4 p/ posters).
- `home.jsp` grid/carrossel de capas (JS vanilla leve); `detail.jsp` com banner/sinopse/ficha. 100% JSP/JSTL.

## Task 6 ? Avaliação (estrelas + comentário) · `delivery/rating`
**Estimativa:** 3 pts · **Depende de:** Task 5.
- Usa colunas `rating`/`comment` da `V1`; UI de estrelas + comentário; update no service revalidando 0?5.
- **Teste primeiro:** `rating` fora de 0?5 rejeitado.

---

# 9. Sequenciamento e mapa de branches

| Ordem | Entrega | Branch pai | Nasce de | Tasks |
|---|---|---|---|---|
| 1 | Núcleo | `delivery/core` | `main` | contracts, 0, 1, 2, 3 |
| 2 | TMDB | `delivery/tmdb` | `release` | 4 |
| 3 | Front home | `delivery/frontend-home` | `release` | 5 |
| 4 | Avaliação | `delivery/rating` | `release` | 6 |
| Fim | Release | `release/pit-catalog` ? `main` (tag `v1.0`) | ? | ? |

Dentro do núcleo, ordem de PRs: `feature/contracts` ? `feature/infra` ? (`feature/dao-*`) ? (`feature/validation`, `feature/service-crud`) ? (`feature/web-*`, `feature/layout`).

# 10. Cobertura das situações-problema
- **SP1** ? Task 1 (`PreparedStatement`, DDL/índices, teste de injeção).
- **SP2** ? Task 2 (`try-catch-finally`, validação, exceptions).
- **SP3** ? Tasks 2 e 3 (separação DAO/Service/Servlet, DIP, sem duplicação).

# 11. Entregáveis do relatório técnico
Diagramas (Casos de Uso, Classes, DER), migration de criação, arquitetura MVC+DIP, prints das telas, seção de segurança (SQL Injection + XSS), Javadoc gerado, manual do usuário, histórico Git (branches/PRs) como evidência de processo, e ? se usar Task 4 ? atribuição TMDB.
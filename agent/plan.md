# plan.md — PIT: Media Catalog

Plano de desenvolvimento do projeto integrador (PIT) — catálogo de mídia em Java web.

## Decisões de arquitetura

- **Stack (obrigatório pelo PDF):** Java 17+ · Servlets (Controller) · JSP + JSTL/EL (View) · JDBC · MySQL 8 · Apache Tomcat · JUnit 5 · Maven (`war`).
- **Arquitetura:** MVC — `Controller` = Servlets, `View` = JSPs, `Model` = domínio (`MediaItem`) + `Service` + `DAO`.
- **DIP:** fronteiras voláteis (TMDB, persistência) atrás de interface; injeção manual por construtor; grafo montado no *composition root* (`ServletContextListener`). Sem framework de DI.
- **Idioma:** identificadores de código em **inglês**; Javadoc e comentários em **PT-BR** (o PDF não restringe idioma de código, só exige Javadoc nos métodos públicos de DAO/Servlet); UI com **i18n** (`ResourceBundle` + `<fmt:message>`), `messages_pt_BR.properties` / `messages_en.properties`.
- **Execução:** `docker compose up` sobe MySQL 8 + Tomcat (WAR) + seed — um comando para o avaliador.
- **Encoding:** MySQL `utf8mb4`; URL JDBC com `useUnicode=true&characterEncoding=UTF-8` (evitar mojibake em título/sinopse).
- **Escopo:** núcleo (Tasks 0–3) é o que é **avaliado**. Extensões (Tasks 4–6) só depois do núcleo sólido; o próprio PDF marca avaliação/capa/filtro como opcionais.

## Modelo de domínio

`MediaItem`: `id`, `title`, `authorDirector`, `releaseYear`, `genre`, `synopsis`, `mediaType` (`MOVIE`|`SERIES`|`BOOK`), `posterUrl` *(ext)*, `externalId` *(ext, TMDB)*, `rating` *(ext, 0–5)*, `comment` *(ext)*.

---

# NÚCLEO (avaliado)

## Task 0 — Infra: Docker Compose, MySQL 8, i18n e seed
**Épico:** Infraestrutura · **Estimativa:** 5 pts · **Depende de:** —

### Objetivo
Ambiente reprodutível em um comando, com banco versionado, encoding correto e base de i18n pronta antes de escrever regra de negócio.

### Escopo / Subtarefas
1. Projeto Maven `packaging=war`; pacotes `com.<seu>.catalog.{model, dao, service, servlet, infra, config}`.
2. `docker-compose.yml` com dois services:
   - `db` (MySQL 8) — volume, `init.sql` montado, `healthcheck`.
   - `app` (Tomcat) — deploy do WAR, `depends_on: db (condition: service_healthy)`.
3. `init.sql` = schema + **seed** (5–10 itens) para a home não abrir vazia.
4. `db.properties` / variáveis de ambiente com a URL JDBC (`useUnicode=true&characterEncoding=UTF-8`).
5. Base de i18n: `messages_pt_BR.properties`, `messages_en.properties`, taglib `fmt` configurada.
6. `README` com "como rodar".

### Especificação técnica
```yaml
# docker-compose.yml (resumo)
services:
  db:
    image: mysql:8
    environment:
      MYSQL_DATABASE: catalog
      MYSQL_ROOT_PASSWORD: root
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    volumes:
      - ./db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-proot"]
      interval: 5s
      retries: 10
  app:
    build: .
    depends_on:
      db:
        condition: service_healthy
    ports: ["8080:8080"]
```
```
# URL JDBC
jdbc:mysql://db:3306/catalog?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
```

### Critérios de aceite
- [ ] `docker compose up` sobe banco + app; home acessível em `localhost:8080` com itens do seed.
- [ ] Tomcat não falha por subir antes do MySQL (healthcheck resolve).
- [ ] Título/sinopse com acento gravam e exibem sem mojibake.
- [ ] Bundles PT/EN carregam e alternam idioma.

### DoD
Ambiente sobe limpo em máquina zerada; `README` com o passo a passo; `init.sql` versionado.

---

## Task 1 — Foundation: schema, entity e DAO seguro
**Épico:** Persistência · **Estimativa:** 8 pts · **Depende de:** Task 0
**Cobre:** SP1 (Modelagem e Segurança) + parte da SP3 (DAO)

### Objetivo
CRUD + busca na camada de dados, testável isoladamente, com **100% `PreparedStatement`** (zero concatenação de entrada em SQL).

### Escopo / Subtarefas
1. DDL da tabela `item_media` (campos núcleo; colunas de extensão nascem nuláveis).
2. Entity `MediaItem` (POJO encapsulado: atributos `private`, getters/setters, construtores).
3. `ConnectionFactory` (credenciais fora do código).
4. Interface `MediaItemDAO` + impl `MySqlMediaItemDAO`: `insert`, `findAll`, `findById`, `update`, `delete`, `searchByTerm`.
5. `DAOException` (checked) com causa encadeada.
6. Testes de integração do DAO, incluindo caso de tentativa de injeção.

### Especificação técnica
```sql
CREATE TABLE item_media (
    id             INT PRIMARY KEY AUTO_INCREMENT,
    title          VARCHAR(255) NOT NULL,
    author_director VARCHAR(255),
    release_year   INT,
    genre          VARCHAR(100),
    synopsis       TEXT,
    media_type     VARCHAR(20) NOT NULL,     -- MOVIE | SERIES | BOOK
    poster_url     VARCHAR(500),             -- extensão (TMDB)
    external_id    VARCHAR(50),              -- extensão (TMDB)
    rating         INT,                      -- extensão (0..5)
    comment        TEXT,                     -- extensão
    INDEX idx_title (title),
    INDEX idx_author_director (author_director)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
```java
/** Busca por título ou autor/diretor. Entrada tratada como dado (anti-SQL Injection). */
public List<MediaItem> searchByTerm(String term) throws DAOException {
    String sql = "SELECT * FROM item_media WHERE title LIKE ? OR author_director LIKE ?";
    try (Connection c = ConnectionFactory.get();
         PreparedStatement st = c.prepareStatement(sql)) {
        String like = "%" + term + "%";
        st.setString(1, like);
        st.setString(2, like);
        try (ResultSet rs = st.executeQuery()) { /* map -> List<MediaItem> */ }
    } catch (SQLException e) { throw new DAOException("searchByTerm failed", e); }
}
```

### Critérios de aceite
- [ ] DAO expõe todas as operações CRUD + `searchByTerm`.
- [ ] Nenhuma query concatena entrada do usuário.
- [ ] Teste com termo `'; DROP TABLE item_media; --` retorna vazio e não altera schema.
- [ ] `try-with-resources` em todos os recursos JDBC.
- [ ] Javadoc (PT) nos métodos públicos do DAO.

### DoD
Testes de integração do DAO passando; `schema.sql` alinhado ao `init.sql`.

---

## Task 2 — Service, validation, exceptions e fronteira de metadados (DIP)
**Épico:** Domínio & robustez · **Estimativa:** 6 pts · **Depende de:** Task 1
**Cobre:** SP2 (Exceções e Validação) + parte da SP3 (Service)

### Objetivo
Regra de negócio isolada da Servlet, validação centralizada, exceções tratadas com liberação garantida de recurso. Definir a **abstração** de metadados (a impl TMDB fica na Task 4).

### Escopo / Subtarefas
1. Exceptions: `ValidationException` (lista de erros de campo), `ServiceException`.
2. `MediaItemValidator` — obrigatoriedade, tipo/faixa de `releaseYear`, comprimentos máximos.
3. `CatalogService` — orquestra validação + DAO com `try-catch-finally` e transação.
4. **Interface `MovieMetadataProvider`** (fronteira DIP; sem dependência de TMDB no domínio).
5. Testes unitários do validator e do service com DAO stub e `FakeMovieMetadataProvider`.

### Especificação técnica
```java
/** Abstração de fonte externa de metadados de filme/série (DIP). */
public interface MovieMetadataProvider {
    List<MovieSummary> search(String query);
    MovieDetails findById(String externalId);
}
```
```java
/** Cadastra um item validando antes e garantindo commit/rollback e fechamento de conexão. */
public void create(MediaItem item) throws ValidationException, ServiceException {
    List<String> errors = validator.validate(item);
    if (!errors.isEmpty()) throw new ValidationException(errors);

    Connection c = null;
    try {
        c = ConnectionFactory.get();
        c.setAutoCommit(false);
        dao.insert(item, c);
        c.commit();
    } catch (SQLException | DAOException e) {
        rollbackQuietly(c);
        throw new ServiceException("Falha ao cadastrar item.", e);
    } finally {
        closeQuietly(c);   // finally SEMPRE libera o recurso
    }
}
```

### Critérios de aceite
- [ ] `releaseYear = "abc"` não derruba a app; retorna erro de validação legível.
- [ ] Falha no DAO gera rollback e `ServiceException` com `cause`.
- [ ] Conexão fechada no `finally` em sucesso e erro.
- [ ] Service testável sem banco (DAO stub) e sem rede (`FakeMovieMetadataProvider`).
- [ ] Interface `MovieMetadataProvider` definida no domínio, sem menção a TMDB.

### DoD
Testes unitários (casos válidos/ inválidos) passando; Javadoc (PT) nas exceptions e service.

---

## Task 3 — Web: Controllers, JSP views, i18n e anti-XSS
**Épico:** Apresentação & integração · **Estimativa:** 8 pts · **Depende de:** Task 2
**Cobre:** fecha SP3 (Servlet enxuta, sem duplicação) + segurança de saída

### Objetivo
Fluxo completo no navegador com controllers finos, views JSP internacionalizadas e saída escapada.

### Escopo / Subtarefas
1. Servlet(s) controller roteando ações (`new`, `save`, `list`, `detail`, `edit`, `update`, `delete`, `search`). Servlet apenas orquestra — sem SQL/regra.
2. Views: `home.jsp`, `manage.jsp`, `form.jsp` (reuso cadastro/edição), `detail.jsp`; sidebar como fragmento via `<jsp:include>`.
3. Parse de `releaseYear` na borda com `try-catch`, reexibindo erros da `ValidationException`.
4. **Composition root** em `AppBootstrap` (`@WebListener`): monta service/DAO/provider e guarda no `ServletContext`.
5. Saída com `<c:out>` + `<fmt:message>` (i18n).
6. Teste de integração funcional: cadastrar → aparece na lista → busca encontra.

### Especificação técnica
```java
@WebListener
public class AppBootstrap implements ServletContextListener {
    /** Monta o grafo de dependências uma única vez no startup. */
    @Override public void contextInitialized(ServletContextEvent e) {
        MediaItemDAO dao = new MySqlMediaItemDAO();
        CatalogService service = new CatalogService(dao /*, provider na Task 4 */);
        e.getServletContext().setAttribute("catalogService", service);
    }
}
```
```jsp
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<td><c:out value="${item.title}"/></td>              <!-- escapa HTML/JS (anti-XSS) -->
<a href="items?action=edit&id=${item.id}"><fmt:message key="action.edit"/></a>
```

### Critérios de aceite
- [ ] Todas as 6 operações CRUD + busca acessíveis pela UI.
- [ ] Servlet sem SQL nem regra de negócio (revisão confirma).
- [ ] Toda saída de dado do usuário passa por `<c:out>`.
- [ ] Textos da UI via `<fmt:message>` (PT e EN funcionando).
- [ ] `form.jsp` reaproveitado em cadastro e edição.
- [ ] Teste E2E do fluxo passando.

### DoD
App sobe no Tomcat e roda o fluxo completo; screenshots para o relatório; Javadoc (PT) nas servlets.

---

# EXTENSÕES (opcionais — só após o núcleo)

## Task 4 — TMDB autofill (impl da fronteira DIP)
**Épico:** Extensão · **Estimativa:** 8 pts · **Depende de:** Tasks 2 e 3
**Nota de escopo:** TMDB cobre **filme/série, não livro** — adotar essa extensão implica focar em MOVIE/SERIES (permitido pelo PDF).

### Objetivo
Usuário busca por nome, escolhe o resultado e o formulário é pré-preenchido (título, ano, sinopse, gênero, elenco/equipe, poster).

### Escopo / Subtarefas
1. `TmdbMetadataProvider implements MovieMetadataProvider` na camada infra (única classe que conhece HTTP/API key/JSON).
2. Injetar a impl no `CatalogService` via `AppBootstrap` (troca o comentário da Task 3).
3. Endpoints: `/search/movie?query=`, `/movie/{id}?append_to_response=credits`; mapear gênero via `/genre/movie/list`; poster em `image.tmdb.org/t/p/w500{poster_path}`.
4. Elenco/equipe persistidos como texto/JSON em campo único (sem tabelas de pessoas).
5. Timeout + fallback (I/O externo) — reusa o padrão de exceções da Task 2.
6. API key fora do código (env); **atribuição TMDB** no rodapé e no relatório.
7. Endpoint interno (Servlet) que expõe a busca ao form; chamada **server-side**, nunca no JSP.

### Critérios de aceite
- [ ] Buscar por nome retorna candidatos; selecionar preenche o form.
- [ ] TMDB indisponível → mensagem amigável, form segue utilizável (fallback manual).
- [ ] API key não exposta no cliente/JSP.
- [ ] Atribuição TMDB presente.
- [ ] `CatalogService` continua sem dependência direta de TMDB (só a interface).

> Conferir a doc atual do TMDB para formato de key/token antes de implementar.

---

## Task 5 — Front: home (grid/carrossel) e página de detalhe
**Épico:** Extensão · **Estimativa:** 5 pts · **Depende de:** Task 3 (+ Task 4 p/ posters)

### Objetivo
Estrutura de apresentação inspirada em streaming, **simplificada dentro dos limites do JSP** (server-rendered; sem SPA).

### Escopo / Subtarefas
1. `home.jsp`: grid/carrossel de cards com poster (CSS + JS vanilla leve).
2. Sidebar (fragmento): Início / Gerenciar biblioteca / Adicionar.
3. `manage.jsp`: tabela id · título · ano · gênero · ações (editar/remover).
4. `detail.jsp`: banner + sinopse + elenco/equipe + ano.

### Critérios de aceite
- [ ] Home lista itens do catálogo com poster (fallback quando sem `posterUrl`).
- [ ] Navegação pela sidebar em todas as telas.
- [ ] Clicar num card abre o detalhe.
- [ ] 100% JSP/JSTL — sem framework de front separado.

---

## Task 6 — Avaliação: estrelas e comentário
**Épico:** Extensão · **Estimativa:** 3 pts · **Depende de:** Task 5

### Objetivo
Permitir nota (0–5) e comentário por item (catálogo single-user).

### Escopo / Subtarefas
1. Usar as colunas `rating` / `comment` já criadas na Task 1 (sem tabela de reviews).
2. UI de estrelas + campo de comentário em `detail.jsp`.
3. Endpoint/ação de update no service (revalida faixa 0–5).

### Critérios de aceite
- [ ] Nota e comentário persistem e reexibem no detalhe.
- [ ] `rating` fora de 0–5 é rejeitado pela validação.

---

# Sequenciamento

| # | Task | Camada | Status |
|---|------|--------|--------|
| 0 | Infra (Compose, MySQL 8, i18n, seed) | Infra | Núcleo |
| 1 | Foundation / DAO seguro | Persistência | Núcleo |
| 2 | Service / validação / exceções / DIP | Domínio | Núcleo |
| 3 | Web / Servlets / JSP / i18n | Apresentação | Núcleo |
| 4 | TMDB autofill | Infra ext. | Opcional |
| 5 | Home + detalhe (streaming-style) | Front ext. | Opcional |
| 6 | Avaliação (estrelas + comentário) | Ext. | Opcional |

# Cobertura das situações-problema (PDF)
- **SP1** → Task 1 (`PreparedStatement`, DDL/índices, teste de injeção).
- **SP2** → Task 2 (`try-catch-finally`, validação, exceptions).
- **SP3** → Tasks 2 e 3 (separação DAO/Service/Servlet, DIP, sem duplicação).

# Entregáveis do relatório técnico
Diagramas (Casos de Uso, Classes, DER), `schema.sql`, descrição da arquitetura MVC + DIP, prints das telas, seção de segurança (SQL Injection + XSS), Javadoc, manual do usuário, e — se usar Task 4 — atribuição TMDB.

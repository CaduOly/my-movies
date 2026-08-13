# plan — Rota de Documentação `/app/about`

Plano de implementação de uma página de documentação/apresentação **dentro da aplicação**, para o avaliador acessar sem sair do app rodando. Complementa (não substitui) o relatório técnico exigido pelo PDF.

## Objetivo
Servir, em `/app/about`, uma página "Sobre" que explique o projeto (objetivo, funcionalidades, arquitetura, modelo de dados, como rodar/testar, segurança), **com screenshots reais das telas** capturados via navegador. Adicionar o item "Sobre/About" no menu lateral e um destaque no topo do README apontando para essa rota.

## Decisões e convenções (ler antes)
- **Stack sem novidade:** apenas Servlet + JSP + JSTL + imagens estáticas. **Nenhuma lib nova** (respeita a proibição de dependências desnecessárias). Nada de renderizar Markdown em runtime.
- **Rota:** `about` no `MediaController` (base `/app/*`) → URL final `/app/about`.
- **View:** `src/main/webapp/WEB-INF/jsp/about.jsp`, usando `<t:layout>` (mesmo menu/estilo/i18n do resto).
- **Imagens:** `src/main/webapp/img/docs/` (servidas pelo default servlet do Tomcat, como o `/css/` já é). Referência via `<c:url value='/img/docs/NOME.png'/>`.
- **i18n (decisão consciente):** o **rótulo do menu** e os **títulos de seção** entram nos bundles (`app.about`, `about.*`). O **corpo descritivo longo** fica em PT no `about.jsp` — é uma página informativa acadêmica em português; internacionalizar prosa longa incharia os bundles sem ganho real. Registrado aqui para não parecer descuido.
- **Fonte única de verdade:** os fatos da página (modelo de dados, funcionalidades) devem bater com README e relatório. Manter factual e estável; não criar uma terceira versão divergente.
- **Screenshots são reais:** capturados do app rodando com o seed (V2), nunca mockados.
- **Branch:** `feature/docs-route` a partir de `fix/review-adjustments` (ou da `release` já com o fix mergeado). PR ao final.

---

## Task 1 — Action `about` no MediaController
**Objetivo:** rotear `/app/about` para a nova view, sem dependência de banco (a página deve abrir mesmo se o DB estiver fora).

**Escopo:**
1. Em `doGet`, adicionar `case "about": handleAbout(req, resp); break;`.
2. Implementar `handleAbout` que apenas faz `forward` para `/WEB-INF/jsp/about.jsp`. Opcional: `req.setAttribute("appVersion", "1.0.0")` para exibir a versão.
3. Javadoc (PT) no método, conforme padrão do projeto.

**Aceite:**
- [ ] `GET /app/about` responde 200 e renderiza a página.
- [ ] A rota funciona mesmo com o banco indisponível (não chama `service`).
- [ ] `mvn javadoc:javadoc` segue sem warnings (método documentado).

---

## Task 2 — View `about.jsp` (estrutura e conteúdo)
**Objetivo:** página enxuta e escaneável, reusando `layout.tag`.

**Seções (nesta ordem):**
1. **Cabeçalho** — nome do projeto (PIT) + objetivo em 2–3 linhas.
2. **Funcionalidades** — lista objetiva: CRUD completo, busca por título/ano/autor, avaliação por estrelas + comentário, autofill via TMDB, i18n PT/EN. *(Interleave screenshots aqui — Task 5.)*
3. **Arquitetura** — fluxo `View (JSP) → Controller (Servlet) → Service → DAO → MySQL`, com nota sobre DIP (`MovieMetadataProvider`) e o composition root (`AppBootstrap` publicando no `ServletContext`). Usar um diagrama leve em HTML/CSS (caixas) — sem imagem/lib externa.
4. **Modelo de dados** — tabela `item_media` (colunas e tipos) + menção às migrations Flyway `V1`/`V2`.
5. **Tecnologias** — Java 17, Jakarta Servlets/JSP, JSTL/EL, JDBC + PreparedStatement, MySQL 8, Flyway, JUnit 5/Mockito/Testcontainers, Docker, Maven.
6. **Como executar e testar** — `make start` (subir), `make test` / `mvn clean test` (testes; nota: integração usa Testcontainers e exige Docker), URL de acesso.
7. **Segurança** — SQL Injection mitigado com `PreparedStatement` (com teste dedicado), XSS mitigado com `<c:out>` em toda saída.
8. **Sobre o `.env` / TMDB** — reaproveitar o aviso do README (token descartável para avaliação).
9. **Créditos/atribuição TMDB** — "Este produto usa a API do TMDB, mas não é endossado nem certificado pelo TMDB." (exigência de atribuição do TMDB).

**Regras:**
- Toda saída dinâmica (ex.: `appVersion`) via `<c:out>`.
- Estilos reaproveitando os tokens do `style.css` (fundo branco, detalhes laranja, hierarquia de fontes). Evitar CSS inline extenso; se precisar, adicionar classes em `style.css`.

**Aceite:**
- [ ] Página renderiza com o menu lateral e o estilo padrão.
- [ ] Todas as seções presentes e factualmente alinhadas ao README/relatório.
- [ ] Sem scriptlet; saída dinâmica escapada.

---

## Task 3 — Item de menu "Sobre/About" + chaves i18n
**Objetivo:** navegação para a nova rota.

**Escopo:**
1. Em `layout.tag`, adicionar na `<nav>`: `<li><a href="<c:url value='/app/about'/>" class="nav-link"><fmt:message key="app.about"/></a></li>`.
2. Adicionar chaves nos bundles:
   - `messages_pt_BR.properties`: `app.about=Sobre` e as `about.*` de títulos de seção (ex.: `about.features=Funcionalidades`, `about.architecture=Arquitetura`, `about.datamodel=Modelo de Dados`, `about.tech=Tecnologias`, `about.run=Como Executar`, `about.security=Segurança`).
   - `messages_en.properties`: `app.about=About` e as equivalentes.

**Aceite:**
- [ ] "Sobre" aparece no menu em todas as telas e leva a `/app/about`.
- [ ] Rótulo e títulos trocam corretamente entre PT e EN.
- [ ] Nenhuma chave i18n ausente (evitar `???key???` na tela).

---

## Task 4 — Captura de screenshots (instruções ao Antigravity / navegador)
**Objetivo:** gerar imagens reais das telas para embutir na página. Executar com a aplicação no ar e o seed (V2) carregado.

**Pré-condições:**
1. Subir a app: `make start` (confirmar no log a linha de acesso). Banco com seed V2 (Inception = `id=1`).
2. Viewport do navegador fixo em **1440×900**, tema claro, janela sem extensões/overlays.

**Instrução ao agente (Antigravity) — usar a ferramenta de navegador para cada item abaixo; navegar até a URL, aguardar carregar e salvar o screenshot no caminho indicado:**

| Arquivo destino | URL | O que enquadrar |
|---|---|---|
| `src/main/webapp/img/docs/home.png` | `http://localhost:8080/app/home` | grid/carrossel do catálogo |
| `src/main/webapp/img/docs/list.png` | `http://localhost:8080/app/list` | tabela de gerenciamento (CRUD) |
| `src/main/webapp/img/docs/detail.png` | `http://localhost:8080/app/detail?id=1` | banner, sinopse e estrelas |
| `src/main/webapp/img/docs/form-new.png` | `http://localhost:8080/app/new` | formulário + botão "Buscar TMDB" |
| `src/main/webapp/img/docs/form-edit.png` | `http://localhost:8080/app/edit?id=1` | formulário preenchido (edição) |
| `src/main/webapp/img/docs/search.png` | `http://localhost:8080/app/search?term=matrix` | resultados de busca |
| `src/main/webapp/img/docs/tmdb-autofill.png` | `http://localhost:8080/app/new` → digitar "Inception" no campo Título → clicar "Buscar TMDB" → aguardar o dropdown | dropdown de sugestões do TMDB aberto |

**Padrões de captura:**
- Nomes de arquivo exatamente como na tabela (a `about.jsp` vai referenciá-los).
- Página inteira (full page) quando couber; senão, enquadrar a área principal de conteúdo.
- Se alguma tela estiver vazia, verificar se o seed V2 foi aplicado antes de recapturar.

**Aceite:**
- [ ] Os 7 arquivos existem em `src/main/webapp/img/docs/` com os nomes exatos.
- [ ] Cada imagem mostra a tela correta, legível, sem sobreposições.

---

## Task 5 — Embutir screenshots na página + rebuild + verificação
**Objetivo:** conectar as imagens capturadas à `about.jsp` e validar a renderização.

**Escopo:**
1. Na `about.jsp`, inserir cada `<img src="<c:url value='/img/docs/NOME.png'/>" alt="..."/>` junto da seção correspondente (Funcionalidades e Telas). Usar `alt` descritivo; largura responsiva (ex.: `max-width:100%`).
2. Como o WAR empacota `src/main/webapp/` no build, **rebuildar**: `make start` (ou `mvn clean package` + subir) para as imagens entrarem no artefato.
3. Abrir `/app/about` e verificar que todas as imagens carregam (sem 404 de imagem).

**Aceite:**
- [ ] Todas as imagens aparecem na página, sem quebra (nenhum 404 em `/img/docs/*`).
- [ ] Layout continua limpo (fundo branco, detalhes laranja, hierarquia mantida).

---

## Task 6 — Atualizar o README (destaque no topo para o avaliador)
**Objetivo:** o avaliador precisa descobrir a rota facilmente.

**Escopo:**
1. **No topo do README** (logo após o título), um bloco de destaque, por exemplo:
   > 📖 **Para avaliadores:** suba a aplicação com `make start` e acesse **http://localhost:8080/app/about** para a documentação navegável (visão geral, arquitetura, telas e como testar).
2. **Corrigir a inconsistência** `make up` × `make start`: alinhar o README ao alvo real do `Makefile` (verificar qual existe e padronizar; hoje o texto cita `make up`, mas o alvo é `make start`).
3. **Mover** TMDB, carrossel e avaliação de "Próximos Passos" para **Funcionalidades** (já estão implementados) — evita subvender o trabalho.

**Aceite:**
- [ ] Destaque com o link para `/app/about` visível no topo do README.
- [ ] Comando de subida no README corresponde ao alvo real do Makefile.
- [ ] "Próximos Passos" não lista mais features já entregues.

---

## Task 7 — Teste e verificação final
**Objetivo:** garantir que a rota não quebrou nada e segue o padrão de testes.

**Escopo:**
1. Teste funcional simples do controller: `GET about` faz `forward` para `/WEB-INF/jsp/about.jsp` (no estilo do `MediaControllerTest` já existente, com mocks de request/response/dispatcher).
2. Rodar `mvn clean test javadoc:javadoc` — tudo verde, 0 warnings (o `doclint=all` cobre o novo método).

**Aceite:**
- [ ] Teste da action `about` passa.
- [ ] Build + testes + javadoc sem warnings.

---

## Git
- Branch `feature/docs-route` a partir de `fix/review-adjustments`.
- Commits no padrão Conventional Commits, ex.: `feat(docs): add about route and view` → `feat(docs): add screenshots and menu link` → `docs(readme): add evaluator callout and fix run command`.
- PR de `feature/docs-route` → `release` ao final.

## Definition of Done (geral)
- [ ] `/app/about` acessível pelo menu, com todas as seções e screenshots reais.
- [ ] README com destaque no topo apontando para a rota; comando de subida correto.
- [ ] Sem lib nova; sem scriptlet; saída dinâmica escapada; i18n de rótulos/títulos.
- [ ] `mvn clean test javadoc:javadoc` verde (0 warnings).
- [ ] Fatos da página conferem com README/relatório.

## Observação de escopo
A rota `/app/about` é um **plus de apresentação**. Ela **não** substitui os artefatos formais que o PDF exige (diagramas de caso de uso, de classes, DER, e o relatório técnico em PDF). Os screenshots capturados aqui, porém, podem ser reaproveitados na seção "Resultados" do relatório.
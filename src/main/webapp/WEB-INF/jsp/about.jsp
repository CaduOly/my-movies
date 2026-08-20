<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitleKey="app.about">
    <div class="docs-container" style="max-width: 900px; margin: 0 auto; line-height: 1.6; padding-bottom: 40px;">
        <header style="margin-bottom: 30px; border-bottom: 1px solid #ccc; padding-bottom: 10px;">
            <h1 style="color: var(--primary-color, #e50914);"><fmt:message key="app.title" /></h1>
            <p style="font-size: 1.2em; color: #555;">Catálogo de Filmes e Séries em Java (Servlets, JSP, JDBC, MySQL) para o Projeto Integrador.</p>
            <c:if test="${not empty appVersion}">
                <p><small>Versão: <c:out value="${appVersion}"/></small></p>
            </c:if>
        </header>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.features" /></h2>
            <ul style="list-style-type: disc; margin-left: 20px; margin-bottom: 20px;">
                <li><strong>CRUD Completo:</strong> Gerenciamento de itens de mídia (criar, ler, atualizar, excluir).</li>
                <li><strong>Busca:</strong> Pesquisa por título, diretor/autor ou ano.</li>
                <li><strong>Avaliação:</strong> Sistema de nota por estrelas (0 a 5) e comentários.</li>
                <li><strong>Integração TMDB:</strong> Preenchimento automático de dados (sinopse, ano, poster) consultando a API do The Movie Database (TMDB).</li>
                <li><strong>Internacionalização (i18n):</strong> Suporte aos idiomas Português (PT-BR) e Inglês (EN) na estrutura principal (layout, menus e mensagens de sistema), enquanto as views de conteúdo (formulários e detalhes) permanecem fixas no idioma base.</li>
            </ul>
            
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/home.png'/>" alt="Página inicial com catálogo" loading="lazy"/>
                <figcaption >Página inicial (Home) com grid de filmes.</figcaption>
            </figure>
            
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/list.png'/>" alt="Tabela de gerenciamento CRUD" loading="lazy"/>
                <figcaption >Tabela de gerenciamento listando itens do catálogo.</figcaption>
            </figure>
            
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/detail.png'/>" alt="Detalhes do item com avaliação" loading="lazy"/>
                <figcaption >Detalhes do filme, com sinopse, capa, e sistema de avaliação.</figcaption>
            </figure>
            
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/search.png'/>" alt="Resultados de busca" loading="lazy"/>
                <figcaption >Resultados de pesquisa no catálogo.</figcaption>
            </figure>
            
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/form-new.png'/>" alt="Formulário de cadastro" loading="lazy"/>
                <figcaption >Formulário para adicionar novos itens.</figcaption>
            </figure>
            
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/tmdb-autofill.png'/>" alt="Busca no TMDB com dropdown" loading="lazy"/>
                <figcaption >Busca na API do TMDB para autocompletar os dados.</figcaption>
            </figure>
            
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/form-edit.png'/>" alt="Formulário de edição" loading="lazy"/>
                <figcaption >Edição de um item existente (autofill pelo TMDB).</figcaption>
            </figure>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.architecture" /></h2>
            <p>O projeto segue o padrão <strong>MVC (Model-View-Controller)</strong> tradicional em Java Web puro (modelo que separa a interface, a regra de negócio e os dados):</p>
            <div style="background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 5px; padding: 15px; font-family: monospace; text-align: center; margin-bottom: 15px;">
                Navegador &larr;&rarr; JSP (Telas) &larr;&rarr; Servlet (Controlador) &larr;&rarr; Service (Regras) &larr;&rarr; DAO (Banco) &larr;&rarr; MySQL (Dados)
            </div>
            <p>O <code>MediaController</code> atua como Front Controller (controlador principal), centralizando o recebimento de todas as requisições do sistema e direcionando-as para a ação correta. Há forte aplicação do <strong>DIP (Princípio de Inversão de Dependência)</strong>, exemplificado na interface <code>MovieMetadataProvider</code>, que permite que o sistema use interfaces em vez de depender diretamente de classes concretas de provedores de dados. O arquivo <code>AppBootstrap.java</code> (via <code>ServletContextListener</code>) funciona como o ponto centralizador (Composition Root), criando e interligando os DAOs e Services antes que os Servlets comecem a receber requisições.</p>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.datamodel" /></h2>
            <p>O armazenamento dos dados (persistência) é baseado em um banco de dados relacional. Existe a tabela principal <code>item_media</code> para guardar todos os registros:</p>
            <table class="docs-table">
                <thead>
                    <tr>
                        <th>Coluna</th>
                        <th>Tipo</th>
                        <th>Descrição</th>
                    </tr>
                </thead>
                <tbody>
                    <tr><td><code>id</code></td><td>INT PK AUTO_INCREMENT</td><td>Chave primária.</td></tr>
                    <tr><td><code>title</code></td><td>VARCHAR(255)</td><td>Título da obra.</td></tr>
                    <tr><td><code>media_type</code></td><td>VARCHAR(20)</td><td>MOVIE, SERIES</td></tr>
                    <tr><td><code>release_year</code></td><td>INT</td><td>Ano de lançamento.</td></tr>
                    <tr><td><code>genre</code></td><td>VARCHAR(100)</td><td>Gênero.</td></tr>
                    <tr><td><code>author_director</code></td><td>VARCHAR(255)</td><td>Diretor ou Autor.</td></tr>
                    <tr><td><code>synopsis</code></td><td>TEXT</td><td>Sinopse descritiva.</td></tr>
                    <tr><td><code>poster_url</code></td><td>VARCHAR(500)</td><td>Link para a imagem da capa/poster.</td></tr>
                    <tr><td><code>rating</code></td><td>INT</td><td>Nota de 0 a 5.</td></tr>
                    <tr><td><code>comment</code></td><td>TEXT</td><td>Comentário do usuário.</td></tr>

                <tr><td><code>created_at</code></td><td>TIMESTAMP</td><td>Data de criação.</td></tr>
                    <tr><td><code>updated_at</code></td><td>TIMESTAMP</td><td>Data da última atualização.</td></tr>
                </tbody>
            </table>
            <p>A estrutura do banco é gerenciada pelo <strong>Flyway</strong>. As migrações (<code>V1</code> cria tabela, <code>V2</code> popula dados seed) garantem reprodutibilidade instantânea.</p>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.tech" /></h2>
            <ul style="list-style-type: square; margin-left: 20px;">
                <li><strong>Backend:</strong> Java 17, Jakarta Servlets, JSP, JSTL/EL.</li>
                <li><strong>Banco de Dados:</strong> MySQL 8, JDBC (PreparedStatement), Flyway Migrations.</li>
                <li><strong>Testes:</strong> JUnit 5, Mockito, Testcontainers (para integração com BD real em containers efêmeros).</li>
                <li><strong>Build e Infra:</strong> Maven, Docker, Docker Compose, Tomcat 10.1.</li>
            </ul>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.run" /></h2>
            <p>O ambiente de execução principal depende do <strong>Docker</strong> e <strong>Docker Compose</strong>.</p>
            <ul style="margin-left: 20px; margin-bottom: 10px;">
                <li><strong>Subir a aplicação:</strong> Execute <code>make start</code> no terminal. Acesse <code>http://localhost:8080</code>.</li>
                <li><strong>Parar a aplicação:</strong> Execute <code>make down</code>.</li>
                <li><strong>Rodar os testes:</strong> Execute <code>make test</code> ou <code>mvn clean test</code> (necessário Docker ativo na máquina para o Testcontainers).</li>
            </ul>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.security" /></h2>
            <ul style="list-style-type: circle; margin-left: 20px;">
                <li><strong>Invasão de Banco de Dados (SQL Injection):</strong> Risco completamente evitado no DAO ao utilizarmos exclusivamente <code>PreparedStatement</code>. Ele separa o comando SQL dos dados digitados pelo usuário (parametrização segura). O projeto possui testes automáticos utilizando <code>Testcontainers</code> (que criam um banco temporário no Docker durante a execução dos testes) para comprovar essa proteção.</li>
                <li><strong>Injeção de Código nas Telas (XSS):</strong> Completamente mitigado através do uso da tag <code>&lt;c:out value="..." /&gt;</code> da biblioteca JSTL, que transforma caracteres especiais como &lt; e &gt; em texto comum, e da criação segura de nós DOM no autocomplete do TMDB (evitando a execução de scripts arbitrários no navegador).</li>
            </ul>
        </section>

        <section style="background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; padding: 15px; border-radius: 5px; margin-bottom: 40px;">
            <h3 style="margin-top: 0;">Sobre o TMDB e .env</h3>
            <p>O projeto utiliza um arquivo <code>.env</code> com uma chave de API read-only do TMDB comitada no repositório propositalmente, visando facilitar a avaliação sem necessidade de cadastros adicionais. Esta chave só tem permissão de leitura pública.</p>
        </section>
        
        <footer style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #ccc; font-size: 0.9em; color: #777;">
            <p><fmt:message key="about.credits" /></p>
            <p style="font-style: italic;">"Este produto usa a API do TMDB, mas não é endossado nem certificado pelo TMDB."</p>
        </footer>
    </div>
</t:layout>

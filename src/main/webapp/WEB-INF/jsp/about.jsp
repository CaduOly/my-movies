<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitle="Sobre">
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
                <li><strong>Internacionalização (i18n):</strong> Suporte aos idiomas Português (PT-BR) e Inglês (EN).</li>
            </ul>
            
            <figure style="margin-top: 20px; text-align: center;">
                <img src="<c:url value='/img/docs/home.png'/>" alt="Página inicial com catálogo" style="max-width:100%; border:1px solid #ddd; border-radius:8px; box-shadow:0 4px 6px rgba(0,0,0,0.1);"/>
                <figcaption style="color: #777; font-size: 0.9em; margin-top: 8px;">Página inicial (Home) com grid de filmes.</figcaption>
            </figure>
            
            <figure style="margin-top: 20px; text-align: center;">
                <img src="<c:url value='/img/docs/list.png'/>" alt="Tabela de gerenciamento CRUD" style="max-width:100%; border:1px solid #ddd; border-radius:8px; box-shadow:0 4px 6px rgba(0,0,0,0.1);"/>
                <figcaption style="color: #777; font-size: 0.9em; margin-top: 8px;">Tabela de gerenciamento listando itens do catálogo.</figcaption>
            </figure>
            
            <figure style="margin-top: 20px; text-align: center;">
                <img src="<c:url value='/img/docs/detail.png'/>" alt="Detalhes do item com avaliação" style="max-width:100%; border:1px solid #ddd; border-radius:8px; box-shadow:0 4px 6px rgba(0,0,0,0.1);"/>
                <figcaption style="color: #777; font-size: 0.9em; margin-top: 8px;">Detalhes do filme, com sinopse, capa, e sistema de avaliação.</figcaption>
            </figure>
            
            <figure style="margin-top: 20px; text-align: center;">
                <img src="<c:url value='/img/docs/search.png'/>" alt="Resultados de busca" style="max-width:100%; border:1px solid #ddd; border-radius:8px; box-shadow:0 4px 6px rgba(0,0,0,0.1);"/>
                <figcaption style="color: #777; font-size: 0.9em; margin-top: 8px;">Resultados de pesquisa no catálogo.</figcaption>
            </figure>
            
            <figure style="margin-top: 20px; text-align: center;">
                <img src="<c:url value='/img/docs/form-new.png'/>" alt="Formulário de cadastro" style="max-width:100%; border:1px solid #ddd; border-radius:8px; box-shadow:0 4px 6px rgba(0,0,0,0.1);"/>
                <figcaption style="color: #777; font-size: 0.9em; margin-top: 8px;">Formulário para adicionar novos itens.</figcaption>
            </figure>
            
            <figure style="margin-top: 20px; text-align: center;">
                <img src="<c:url value='/img/docs/tmdb-autofill.png'/>" alt="Busca no TMDB com dropdown" style="max-width:100%; border:1px solid #ddd; border-radius:8px; box-shadow:0 4px 6px rgba(0,0,0,0.1);"/>
                <figcaption style="color: #777; font-size: 0.9em; margin-top: 8px;">Busca na API do TMDB para autocompletar os dados.</figcaption>
            </figure>
            
            <figure style="margin-top: 20px; text-align: center;">
                <img src="<c:url value='/img/docs/form-edit.png'/>" alt="Formulário de edição" style="max-width:100%; border:1px solid #ddd; border-radius:8px; box-shadow:0 4px 6px rgba(0,0,0,0.1);"/>
                <figcaption style="color: #777; font-size: 0.9em; margin-top: 8px;">Edição de um item existente (autofill pelo TMDB).</figcaption>
            </figure>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.architecture" /></h2>
            <p>O projeto segue o padrão <strong>MVC (Model-View-Controller)</strong> tradicional em Java Web puro:</p>
            <div style="background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 5px; padding: 15px; font-family: monospace; text-align: center; margin-bottom: 15px;">
                Browser &larr;&rarr; JSP (View) &larr;&rarr; Servlet (Controller) &larr;&rarr; Service &larr;&rarr; DAO &larr;&rarr; MySQL
            </div>
            <p>O <code>MediaController</code> atua como Front Controller roteando as requisições. Há forte aplicação do <strong>DIP (Dependency Inversion Principle)</strong>, exemplificado na interface <code>MovieMetadataProvider</code>. O arquivo <code>AppBootstrap.java</code> (via <code>ServletContextListener</code>) funciona como o <em>Composition Root</em>, injetando as dependências de DAOs e Services e disponibilizando-os para os Servlets no contexto da aplicação.</p>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.datamodel" /></h2>
            <p>A persistência é baseada em banco de dados relacional. Existe a tabela central <code>item_media</code> para armazenar todos os itens:</p>
            <table style="width: 100%; border-collapse: collapse; margin-top: 10px; margin-bottom: 10px; border: 1px solid #ddd;">
                <thead>
                    <tr style="background-color: #f2f2f2;">
                        <th style="border: 1px solid #ddd; padding: 8px; text-align: left;">Coluna</th>
                        <th style="border: 1px solid #ddd; padding: 8px; text-align: left;">Tipo</th>
                        <th style="border: 1px solid #ddd; padding: 8px; text-align: left;">Descrição</th>
                    </tr>
                </thead>
                <tbody>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>id</code></td><td style="border: 1px solid #ddd; padding: 8px;">INT PK AUTO_INCREMENT</td><td style="border: 1px solid #ddd; padding: 8px;">Chave primária.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>title</code></td><td style="border: 1px solid #ddd; padding: 8px;">VARCHAR(255)</td><td style="border: 1px solid #ddd; padding: 8px;">Título da obra.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>media_type</code></td><td style="border: 1px solid #ddd; padding: 8px;">VARCHAR(20)</td><td style="border: 1px solid #ddd; padding: 8px;">MOVIE, SERIES</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>release_year</code></td><td style="border: 1px solid #ddd; padding: 8px;">INT</td><td style="border: 1px solid #ddd; padding: 8px;">Ano de lançamento.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>genre</code></td><td style="border: 1px solid #ddd; padding: 8px;">VARCHAR(100)</td><td style="border: 1px solid #ddd; padding: 8px;">Gênero.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>author_director</code></td><td style="border: 1px solid #ddd; padding: 8px;">VARCHAR(255)</td><td style="border: 1px solid #ddd; padding: 8px;">Diretor ou Autor.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>synopsis</code></td><td style="border: 1px solid #ddd; padding: 8px;">TEXT</td><td style="border: 1px solid #ddd; padding: 8px;">Sinopse descritiva.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>poster_url</code></td><td style="border: 1px solid #ddd; padding: 8px;">VARCHAR(500)</td><td style="border: 1px solid #ddd; padding: 8px;">Link para a imagem da capa/poster.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>rating</code></td><td style="border: 1px solid #ddd; padding: 8px;">INT</td><td style="border: 1px solid #ddd; padding: 8px;">Nota de 0 a 5.</td></tr>
                    <tr><td style="border: 1px solid #ddd; padding: 8px;"><code>comment</code></td><td style="border: 1px solid #ddd; padding: 8px;">TEXT</td><td style="border: 1px solid #ddd; padding: 8px;">Comentário do usuário.</td></tr>

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
                <li><strong>SQL Injection:</strong> Completamente mitigado no DAO através do uso exclusivo de <code>PreparedStatement</code>, que efetua a parametrização segura das queries. O projeto conta com testes de integração (Testcontainers) que comprovam isso.</li>
                <li><strong>Cross-Site Scripting (XSS):</strong> Mitigado nas views através do uso da JSTL core tag <code>&lt;c:out value="..." /&gt;</code>, que efetua o escape automático de entidades HTML.</li>
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

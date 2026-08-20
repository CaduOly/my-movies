<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout pageTitleKey="app.about">
    <div class="docs-container" style="max-width: 900px; margin: 0 auto; line-height: 1.6; padding-bottom: 40px;">
        <header style="margin-bottom: 30px; border-bottom: 1px solid #ccc; padding-bottom: 10px;">
            <h1 style="color: var(--primary-color, #e50914);"><fmt:message key="app.title" /></h1>
            <p style="font-size: 1.2em; color: #555;"><fmt:message key="about.subtitle" /></p>
            <c:if test="${not empty appVersion}">
                <p><small><fmt:message key="about.version" /> <c:out value="${appVersion}"/></small></p>
            </c:if>
        </header>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.features" /></h2>
            <ul style="list-style-type: disc; margin-left: 20px; margin-bottom: 20px;">
                <li><strong><fmt:message key="about.feature.crud.title" /></strong> <fmt:message key="about.feature.crud.desc" /></li>
                <li><strong><fmt:message key="about.feature.search.title" /></strong> <fmt:message key="about.feature.search.desc" /></li>
                <li><strong><fmt:message key="about.feature.rating.title" /></strong> <fmt:message key="about.feature.rating.desc" /></li>
                <li><strong><fmt:message key="about.feature.tmdb.title" /></strong> <fmt:message key="about.feature.tmdb.desc" /></li>
                <li><strong><fmt:message key="about.feature.i18n.title" /></strong> <fmt:message key="about.feature.i18n.desc" /></li>
            </ul>
            
            <fmt:message key="about.fig.home.alt" var="figHomeAlt" />
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/home.png'/>" alt="${figHomeAlt}" loading="lazy"/>
                <figcaption><fmt:message key="about.fig.home.caption" /></figcaption>
            </figure>
            
            <fmt:message key="about.fig.list.alt" var="figListAlt" />
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/list.png'/>" alt="${figListAlt}" loading="lazy"/>
                <figcaption><fmt:message key="about.fig.list.caption" /></figcaption>
            </figure>
            
            <fmt:message key="about.fig.detail.alt" var="figDetailAlt" />
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/detail.png'/>" alt="${figDetailAlt}" loading="lazy"/>
                <figcaption><fmt:message key="about.fig.detail.caption" /></figcaption>
            </figure>
            
            <fmt:message key="about.fig.search.alt" var="figSearchAlt" />
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/search.png'/>" alt="${figSearchAlt}" loading="lazy"/>
                <figcaption><fmt:message key="about.fig.search.caption" /></figcaption>
            </figure>
            
            <fmt:message key="about.fig.form_new.alt" var="figFormNewAlt" />
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/form-new.png'/>" alt="${figFormNewAlt}" loading="lazy"/>
                <figcaption><fmt:message key="about.fig.form_new.caption" /></figcaption>
            </figure>
            
            <fmt:message key="about.fig.tmdb.alt" var="figTmdbAlt" />
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/tmdb-autofill.png'/>" alt="${figTmdbAlt}" loading="lazy"/>
                <figcaption><fmt:message key="about.fig.tmdb.caption" /></figcaption>
            </figure>
            
            <fmt:message key="about.fig.form_edit.alt" var="figFormEditAlt" />
            <figure class="docs-figure">
                <img src="<c:url value='/img/docs/form-edit.png'/>" alt="${figFormEditAlt}" loading="lazy"/>
                <figcaption><fmt:message key="about.fig.form_edit.caption" /></figcaption>
            </figure>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.architecture" /></h2>
            <p><fmt:message key="about.arch.intro" /></p>
            <div style="background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 5px; padding: 15px; font-family: monospace; text-align: center; margin-bottom: 15px;">
                <fmt:message key="about.arch.diagram" />
            </div>
            <p><fmt:message key="about.arch.details" /></p>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.datamodel" /></h2>
            <p><fmt:message key="about.data.intro" /></p>
            <table class="docs-table">
                <thead>
                    <tr>
                        <th><fmt:message key="about.data.col.name" /></th>
                        <th><fmt:message key="about.data.col.type" /></th>
                        <th><fmt:message key="about.data.col.desc" /></th>
                    </tr>
                </thead>
                <tbody>
                    <tr><td><code>id</code></td><td>INT PK AUTO_INCREMENT</td><td><fmt:message key="about.data.field.id" /></td></tr>
                    <tr><td><code>title</code></td><td>VARCHAR(255)</td><td><fmt:message key="about.data.field.title" /></td></tr>
                    <tr><td><code>media_type</code></td><td>VARCHAR(20)</td><td><fmt:message key="about.data.field.media_type" /></td></tr>
                    <tr><td><code>release_year</code></td><td>INT</td><td><fmt:message key="about.data.field.release_year" /></td></tr>
                    <tr><td><code>genre</code></td><td>VARCHAR(100)</td><td><fmt:message key="about.data.field.genre" /></td></tr>
                    <tr><td><code>author_director</code></td><td>VARCHAR(255)</td><td><fmt:message key="about.data.field.author_director" /></td></tr>
                    <tr><td><code>synopsis</code></td><td>TEXT</td><td><fmt:message key="about.data.field.synopsis" /></td></tr>
                    <tr><td><code>poster_url</code></td><td>VARCHAR(500)</td><td><fmt:message key="about.data.field.poster_url" /></td></tr>
                    <tr><td><code>rating</code></td><td>INT</td><td><fmt:message key="about.data.field.rating" /></td></tr>
                    <tr><td><code>comment</code></td><td>TEXT</td><td><fmt:message key="about.data.field.comment" /></td></tr>
                    <tr><td><code>created_at</code></td><td>TIMESTAMP</td><td><fmt:message key="about.data.field.created_at" /></td></tr>
                    <tr><td><code>updated_at</code></td><td>TIMESTAMP</td><td><fmt:message key="about.data.field.updated_at" /></td></tr>
                </tbody>
            </table>
            <p><fmt:message key="about.data.flyway" /></p>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.tech" /></h2>
            <ul style="list-style-type: square; margin-left: 20px;">
                <li><strong><fmt:message key="about.tech.backend.title" /></strong> <fmt:message key="about.tech.backend.desc" /></li>
                <li><strong><fmt:message key="about.tech.db.title" /></strong> <fmt:message key="about.tech.db.desc" /></li>
                <li><strong><fmt:message key="about.tech.tests.title" /></strong> <fmt:message key="about.tech.tests.desc" /></li>
                <li><strong><fmt:message key="about.tech.build.title" /></strong> <fmt:message key="about.tech.build.desc" /></li>
            </ul>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.run" /></h2>
            <p><fmt:message key="about.run.intro" /></p>
            <ul style="margin-left: 20px; margin-bottom: 10px;">
                <li><fmt:message key="about.run.start" /></li>
                <li><fmt:message key="about.run.stop" /></li>
                <li><fmt:message key="about.run.test" /></li>
            </ul>
        </section>

        <section style="margin-bottom: 40px;">
            <h2><fmt:message key="about.security" /></h2>
            <ul style="list-style-type: circle; margin-left: 20px;">
                <li><strong><fmt:message key="about.sec.sqli.title" /></strong> <fmt:message key="about.sec.sqli.desc" /></li>
                <li><strong><fmt:message key="about.sec.xss.title" /></strong> <fmt:message key="about.sec.xss.desc" /></li>
                <li><strong><fmt:message key="about.sec.csrf.title" /></strong> <fmt:message key="about.sec.csrf.desc" /></li>
            </ul>
        </section>

        <section style="background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; padding: 15px; border-radius: 5px; margin-bottom: 40px;">
            <h3 style="margin-top: 0;"><fmt:message key="about.tmdb.title" /></h3>
            <p><fmt:message key="about.tmdb.desc" /></p>
        </section>
        
        <footer style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #ccc; font-size: 0.9em; color: #777;">
            <p><fmt:message key="about.credits" /></p>
            <p style="font-style: italic;"><fmt:message key="about.tmdb.disclaimer" /></p>
        </footer>
    </div>
</t:layout>

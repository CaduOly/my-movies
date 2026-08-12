# Task 0: Infraestrutura (Compose, MySQL, Flyway, i18n, Bootstrap)

**Entrega:** `delivery/core`  
**Branch:** `feature/infra`  
**Estimativa:** 5 pontos  
**Prioridade:** 🔴 BLOQUEADOR  
**Depende:** `feature/contracts` (merged)  
**Status:** Não iniciado

---

## Objetivo

Estabelecer a **infraestrutura foundacional** do projeto:
- Build Maven com estrutura WAR
- Ambiente Docker (MySQL 8 + Tomcat)
- Flyway para versionamento de schema
- Inicialização automática no startup (composition root + listeners)
- Internacionalização (i18n) pronta
- URL de acesso logada

> Sem essa task, nada roda. **Este é o fundamento.**

---

## Escopo

### 1. Estrutura Maven (`pom.xml`)

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.seu</groupId>
    <artifactId>my-movies</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- Jakarta EE / Servlet API -->
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- JSTL + EL -->
        <dependency>
            <groupId>org.apache.taglibs</groupId>
            <artifactId>taglibs-standard-spec</artifactId>
            <version>1.2.5</version>
        </dependency>
        <dependency>
            <groupId>org.apache.taglibs</groupId>
            <artifactId>taglibs-standard-impl</artifactId>
            <version>1.2.5</version>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>

        <!-- Flyway -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
            <version>9.22.3</version>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-mysql</artifactId>
            <version>9.22.3</version>
        </dependency>

        <!-- JUnit 5 (testes) -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>

        <!-- Mockito -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.3.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>5.3.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>

            <!-- WAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>

            <!-- Javadoc -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <doclint>all</doclint>
                    <encoding>UTF-8</encoding>
                    <docencoding>UTF-8</docencoding>
                </configuration>
            </plugin>

            <!-- Surefire (testes) -->\n            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**Checklist Maven:**
- [ ] Versão Java = 17+
- [ ] `packaging=war`
- [ ] `maven-compiler-plugin` configurado
- [ ] `maven-war-plugin` sem falha se faltar web.xml (Servlet 6.0+)
- [ ] Dependências corretas (Jakarta EE, JSTL, MySQL, Flyway, JUnit 5)
- [ ] Javadoc plugin com `<doclint>all</doclint>`

---

### 2. Docker Compose

Arquivo: `docker-compose.yml`

```yaml
version: '3.9'

services:
  db:
    image: mysql:8.0.34
    container_name: my-movies-db
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: my_movies
      MYSQL_USER: app
      MYSQL_PASSWORD: app123
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 5s
      timeout: 10s
      retries: 5
    volumes:
      - db_volume:/var/lib/mysql

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: my-movies-app
    environment:
      JDBC_URL: jdbc:mysql://db:3306/my_movies?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
      JDBC_USER: app
      JDBC_PASSWORD: app123
    ports:
      - "8080:8080"
    depends_on:
      db:
        condition: service_healthy
    volumes:
      - ./target/my-movies-1.0.0.war:/usr/local/tomcat/webapps/ROOT.war

volumes:
  db_volume:
```

**Checklist Docker:**
- [ ] MySQL 8.0+, charset UTF-8
- [ ] Healthcheck no DB (aguarda ping)
- [ ] Tomcat aguarda DB saudável (`depends_on healthy`)
- [ ] Variáveis de ambiente: JDBC_URL, JDBC_USER, JDBC_PASSWORD
- [ ] Port 3306 (DB) e 8080 (Tomcat) expostos

---

### 3. Dockerfile

```dockerfile
FROM tomcat:10.1-jdk17

# Remove app default
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copia WAR
COPY target/my-movies-1.0.0.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
```

---

### 4. Flyway Migrations

#### `src/main/resources/db/migration/V1__create_item_media.sql`

```sql
-- Criar tabela de itens de mídia
CREATE TABLE item_media (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author_director VARCHAR(255),
    release_year INT,
    genre VARCHAR(100),
    synopsis TEXT,
    media_type VARCHAR(20) NOT NULL,
    poster_url VARCHAR(500),
    external_id VARCHAR(50),
    rating INT CHECK (rating >= 0 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_author_director (author_director)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### `src/main/resources/db/migration/V2__seed_data.sql`

```sql
-- Seed de dados de exemplo
INSERT INTO item_media (title, author_director, release_year, genre, synopsis, media_type) VALUES
('Inception', 'Christopher Nolan', 2010, 'Sci-Fi', 'Um ladrão que rouba segredos corporativos através da tecnologia de sonho compartilhado.', 'MOVIE'),
('The Matrix', 'Lana Wachowski, Lilly Wachowski', 1999, 'Sci-Fi', 'Um hacker descobre que a realidade em que vive é uma simulação.', 'MOVIE'),
('Breaking Bad', 'Vince Gilligan', 2008, 'Drama', 'Um professor de química se torna produtor de metanfetamina.', 'SERIES'),
('1984', 'George Orwell', 1949, 'Distopia', 'Um romance sobre um regime totalitário.', 'BOOK');
```

**Checklist Flyway:**
- [ ] Migrations em `src/main/resources/db/migration/`
- [ ] V1: cria `item_media` com charset `utf8mb4`
- [ ] V2: seed de dados (ex: 3-4 itens)
- [ ] Nomes: `V1__nome`, `V2__nome` (sem espaço após `__`)
- [ ] Sem `init.sql`; Flyway é a fonte de verdade

---

### 5. Configuração JDBC

#### `com.seu.catalog.infra.ConnectionFactory` (classe Java)

```java
package com.seu.catalog.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory para conexões JDBC.
 * Lê credenciais de variáveis de ambiente.
 */
public class ConnectionFactory {
    private static final String JDBC_URL = System.getenv("JDBC_URL") != null 
        ? System.getenv("JDBC_URL")
        : "jdbc:mysql://localhost:3306/my_movies?useUnicode=true&characterEncoding=UTF-8";
    
    private static final String JDBC_USER = System.getenv("JDBC_USER") != null
        ? System.getenv("JDBC_USER")
        : "app";
    
    private static final String JDBC_PASSWORD = System.getenv("JDBC_PASSWORD") != null
        ? System.getenv("JDBC_PASSWORD")
        : "app123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado", e);
        }
    }

    /**
     * Retorna uma nova conexão JDBC.
     *
     * @return conexão com o banco
     * @throws SQLException se não conseguir conectar
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}
```

**Checklist ConnectionFactory:**
- [ ] Lê variáveis de ambiente (JDBC_URL, JDBC_USER, JDBC_PASSWORD)
- [ ] Fallback para localhost se não definidas
- [ ] Registra driver MySQL no static block
- [ ] Javadoc PT-BR
- [ ] Sem credenciais hardcoded

---

### 6. Internacionalização (i18n)

#### `src/main/resources/messages_pt_BR.properties`

```properties
app.title=Catálogo de Mídia
app.home=Início
app.manage=Gerenciar Biblioteca
app.add=Adicionar Mídia
app.list=Lista de Itens
app.new=Novo Item
app.edit=Editar
app.delete=Deletar
app.search=Buscar
app.search.placeholder=Digite título ou autor...
app.save=Salvar
app.cancel=Cancelar
app.back=Voltar

item.id=ID
item.title=Título
item.authorDirector=Autor/Diretor
item.releaseYear=Ano de Lançamento
item.genre=Gênero
item.synopsis=Sinopse
item.mediaType=Tipo
item.posterUrl=URL do Pôster
item.rating=Avaliação
item.comment=Comentário

type.movie=Filme
type.series=Série
type.book=Livro

error.title_required=Título é obrigatório
error.type_required=Tipo é obrigatório
error.invalid_year=Ano deve ser um número válido
error.db_error=Erro ao acessar o banco de dados
error.validation=Dados inválidos
```

#### `src/main/resources/messages_en.properties`

```properties
app.title=Media Catalog
app.home=Home
app.manage=Manage Library
app.add=Add Media
app.list=Item List
app.new=New Item
app.edit=Edit
app.delete=Delete
app.search=Search
app.search.placeholder=Enter title or author...
app.save=Save
app.cancel=Cancel
app.back=Back

item.id=ID
item.title=Title
item.authorDirector=Author/Director
item.releaseYear=Release Year
item.genre=Genre
item.synopsis=Synopsis
item.mediaType=Type
item.posterUrl=Poster URL
item.rating=Rating
item.comment=Comment

type.movie=Movie
type.series=Series
type.book=Book

error.title_required=Title is required
error.type_required=Type is required
error.invalid_year=Year must be a valid number
error.db_error=Database error
error.validation=Invalid data
```

**Checklist i18n:**
- [ ] `messages_pt_BR.properties` criado
- [ ] `messages_en.properties` criado
- [ ] Ambos em `src/main/resources/`
- [ ] Mesmas keys em ambos (use find/grep para validar)

---

### 7. Application Bootstrap (`AppBootstrap` / Listener)

#### `com.seu.catalog.infra.AppBootstrap`

```java
package com.seu.catalog.infra;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Listener que executa na inicialização da aplicação.
 * Responsabilidades:
 * - Executar Flyway (migrations)
 * - Construir o grafo de dependências (injeção manual)
 * - Logar a URL de acesso
 */
@WebListener
public class AppBootstrap implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(AppBootstrap.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 1. Testar conexão com o banco
            LOG.info("Testando conexão com o banco de dados...");
            try (Connection conn = ConnectionFactory.get()) {
                LOG.info("✓ Conexão bem-sucedida");
            }

            // 2. Executar Flyway
            LOG.info("Executando Flyway (migrations)...");
            String dbUrl = System.getenv("JDBC_URL") != null 
                ? System.getenv("JDBC_URL")
                : "jdbc:mysql://localhost:3306/my_movies?useUnicode=true&characterEncoding=UTF-8";
            String dbUser = System.getenv("JDBC_USER") != null 
                ? System.getenv("JDBC_USER")
                : "app";
            String dbPass = System.getenv("JDBC_PASSWORD") != null 
                ? System.getenv("JDBC_PASSWORD")
                : "app123";

            Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPass)
                .load();

            int migrationsApplied = flyway.migrate();
            LOG.info("✓ Flyway: " + migrationsApplied + " migration(s) aplicada(s)");

            // 3. Logar contexto
            String contextPath = sce.getServletContext().getContextPath();
            String appUrl = "http://localhost:8080" + (contextPath.isEmpty() ? "/" : contextPath);
            LOG.info("═══════════════════════════════════════════════════════════");
            LOG.info("✓ Aplicação pronta!");
            LOG.info("✓ Acesso: " + appUrl);
            LOG.info("═══════════════════════════════════════════════════════════");

            // 4. Armazenar no contexto (para Servlets usarem)
            // Mais tarde, quando tivermos Services: sce.getServletContext().setAttribute("catalogService", service);

        } catch (Exception e) {
            LOG.severe("Erro na inicialização: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Aplicação finalizada");
    }
}
```

**Checklist AppBootstrap:**
- [ ] Anotação `@WebListener` (Servlet 3.0+)
- [ ] Testa conexão JDBC
- [ ] Executa Flyway (migra schema)
- [ ] Loga URL de acesso
- [ ] Usa `java.util.logging`, não `System.out.println`
- [ ] Javadoc PT-BR

---

### 8. Script para Facilitar Startup (`Makefile` ou `run.sh`)

Arquivo: `Makefile`

```makefile
.PHONY: help build up down logs clean

help:
	@echo "Comandos disponíveis:"
	@echo "  make build   - Compila o projeto (mvn clean package)"
	@echo "  make up      - Sobe containers (docker compose up)"
	@echo "  make down    - Derruba containers"
	@echo "  make logs    - Mostra logs do app"
	@echo "  make clean   - Remove containers e volumes"

build:
	mvn clean package

up: build
	@echo "Iniciando Docker Compose..."
	docker compose up -d
	@echo "Aguardando app estar pronto..."
	@sleep 5
	@echo ""
	@echo "✓ App pronto em http://localhost:8080"
	@docker compose logs app | grep -i "acesso:"

down:
	docker compose down

logs:
	docker compose logs -f app

clean:
	docker compose down -v
	rm -rf target
```

---

## Travas (Constraints Críticas)

### 🔴 TRAVA 1: Sem Schema Direto no Banco
- ❌ Não execute `init.sql` manualmente
- ❌ Não crie tabelas fora do Flyway
- ✅ Flyway é a fonte de verdade

**Por quê?** Quando a app sobe em outro ambiente (CI, produção), o schema precisa ser reproduzível. Flyway garante isso.

### 🔴 TRAVA 2: Credenciais Nunca Hardcoded
- ❌ Sem senhas em `pom.xml`, `docker-compose.yml`, ou código Java
- ✅ Variáveis de ambiente (`JDBC_URL`, `JDBC_USER`, `JDBC_PASSWORD`)

**Em CI/CD:** Injete secrets como env vars, não em arquivos.

### 🔴 TRAVA 3: Charset UTF-8 em Tudo
- MySQL: `CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci`
- JDBC: `useUnicode=true&characterEncoding=UTF-8`
- Maven: `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`

**Por quê?** Caracteres acentuados não podem vazar como `?????`. Isso é "mojibake".

### 🔴 TRAVA 4: Flyway Versiona o Schema
- V1: cria schema
- V2+: só acrescenta (nunca deleta em prod)

Se cometer erro em V1, crie V3 com `ALTER TABLE` corrigindo.

### 🔴 TRAVA 5: Docker Compose Aguarda Healthcheck
`depends_on` com `service_healthy` é obrigatório. O Tomcat não pode iniciar antes do MySQL estar pronto.

---

## Critérios de Aceite

### Build & Execução
- [ ] `mvn clean package` compila WAR sem erros
- [ ] `docker compose up` sobe MySQL + Tomcat
- [ ] Flyway aplica V1 + V2 no startup
- [ ] Healthcheck do DB passa (MySQL pronto em < 10s)
- [ ] App está acessível em `http://localhost:8080` (ou `/ROOT`)

### Banco de Dados
- [ ] Tabela `item_media` criada com campos corretos
- [ ] Seed data inserido (pelo menos 3-4 itens)
- [ ] Charset `utf8mb4` ativo
- [ ] Índices em `title` e `author_director`

### Código
- [ ] `ConnectionFactory` carrega credenciais de env vars com fallback
- [ ] `AppBootstrap` listener testa conexão + roda Flyway + loga URL
- [ ] `pom.xml` aponta Java 17, packaging war, dependências corretas
- [ ] i18n: `messages_pt_BR.properties` e `messages_en.properties` com mesmas keys

### Logs
- [ ] `docker compose logs app` mostra "✓ Acesso: http://localhost:8080"
- [ ] Sem warnings Maven/Flyway/MySQL
- [ ] Sem stack traces de erro no startup

### Checklist de Código
- [ ] `mvn javadoc:javadoc` sem warnings
- [ ] Sem credenciais em Git (revisar com `git log -p`)
- [ ] Sem `System.out.println`; usar `java.util.logging`
- [ ] Sem imports não usados

---

## Estratégia de Teste

**Teste Manual:**

```bash
# 1. Build
mvn clean package

# 2. Suba containers
docker compose up -d

# 3. Aguarde ~10s
sleep 10

# 4. Verifique logs
docker compose logs app | grep "Acesso:"

# 5. Acesse no navegador
curl http://localhost:8080/

# 6. Valide MySQL
docker exec my-movies-db mysql -u app -papp123 my_movies -e "SELECT COUNT(*) FROM item_media;"

# 7. Derrube
docker compose down
```

**Resultado esperado:**
- Logs mostram "✓ Acesso: http://localhost:8080"
- `SELECT COUNT(*)` retorna 4 (items de seed)
- HTTP 200 na home (ainda vazia, mas sem erro 500)

---

## Checkpoints Durante a Implementação

1. **Maven compilável**
   - [ ] `pom.xml` bem-formado
   - [ ] `mvn compile` sucede

2. **Estrutura Docker**
   - [ ] `docker-compose.yml` e `Dockerfile` criados
   - [ ] `docker compose config` valida YAML

3. **Flyway migrations**
   - [ ] Arquivos V1, V2 em `src/main/resources/db/migration/`
   - [ ] Nomes corretos (sem espaço)

4. **ConnectionFactory + AppBootstrap**
   - [ ] Classes compilam
   - [ ] Listener está anotado com `@WebListener`

5. **i18n**
   - [ ] Properties files em `src/main/resources/`
   - [ ] Mesmas keys (validar com diff)

6. **Startup**
   - [ ] `docker compose up` sobe sem erro
   - [ ] Flyway loga mensagens
   - [ ] URL aparece no console

---

## Definition of Done

Uma PR `feature/infra` só é mergeable se:

- [ ] `mvn clean verify` passa (compile + testes + Javadoc)
- [ ] `docker compose up` sobe banco + app sem erros
- [ ] `docker compose logs app` mostra "✓ Acesso:" com URL correta
- [ ] Flyway aplica V1 + V2; banco tem dados de seed
- [ ] `ConnectionFactory` lê env vars (testável via logs)
- [ ] `AppBootstrap` testa conexão + loga hora
- [ ] i18n: `messages_pt_BR` + `messages_en` com mesmas chaves
- [ ] Sem credenciais commitadas (revisar `git show --name-status`)
- [ ] Javadoc PT-BR em classe + métodos públicos, sem warnings
- [ ] Commits: `chore: add pom.xml`, `feat: add docker-compose`, `feat: add flyway migrations`, etc.
- [ ] PR pequena, um objetivo (infrastructure setup)
- [ ] Revisado contra seção 1 (Pontos Inegociáveis) + seção 2 (Proibições)

---

## Próximos Passos

Após merge:
- ✅ Task 1 (DAO) pode começar (banco está pronto)
- ✅ Task 2 (Service) pode começar (contratos + infra prontos)

**Bloqueadores removidos.**

---

## Notas

- **`.env` local:** Se quiser sobrescrever env vars localmente, use `.env` e adicione ao `.gitignore`
- **Fallback em ConnectionFactory:** É só para dev local. Em produção, env vars **são obrigatórias**.
- **Volume MySQL:** Os dados persistem entre `docker compose down`/`up`. Se quiser resetar: `docker compose down -v`
- **Contexto WAR:** Se rodar em Tomcat com contexto diferente de `/`, ajuste o URL no AppBootstrap.

---

**Versão 1.0 | 2026-08-12**

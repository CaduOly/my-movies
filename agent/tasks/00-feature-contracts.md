# Feature: Contracts (Fundação)

**Entrega:** `delivery/core`  
**Branch:** `feature/contracts`  
**Estimativa:** 3 pontos  
**Prioridade:** 🔴 BLOQUEADOR  
**Status:** Não iniciado

---

## Objetivo

Estabelecer **contratos** (interfaces, modelo, exceções) para que as demais camadas (DAO, Service, Web) sejam desenvolvidas **em paralelo**, com **independência de implementação**.

> Essa é a tarefa **mais crítica**. Sem os contratos bem definidos, o paralelismo não funciona e qualquer mudança posterior cascateia por todo o código.

---

## Escopo

### 1. Modelo de Domínio

#### `MediaItem.java` (POJO encapsulado)

```java
package com.seu.catalog.model;

public class MediaItem {
    private Integer id;
    private String title;
    private String authorDirector;
    private Integer releaseYear;
    private String genre;
    private String synopsis;
    private MediaType mediaType;
    private String posterUrl;         // ext: nullable
    private String externalId;        // ext: nullable
    private Integer rating;           // ext: nullable, 0-5
    private String comment;           // ext: nullable

    // Constructor (mín.) com args obrigatórios: title, mediaType
    public MediaItem(String title, MediaType mediaType) { ... }
    
    // Getters/setters públicos; nenhum Javadoc redundante
    public Integer getId() { ... }
    public String getTitle() { ... }
    public void setTitle(String title) { ... }
    // ... resto dos getters/setters
    
    // toString, equals, hashCode (opt. por agora)
}
```

**Javadoc obrigatório:**
- [ ] Classe: descreve o que é um MediaItem
- [ ] Constructor: indica quais campos são obrigatórios
- [ ] Métodos que quebram padrão (ex: getters custom): descrever

#### `MediaType.java` (Enum)

```java
package com.seu.catalog.model;

public enum MediaType {
    MOVIE,
    SERIES,
    BOOK
}
```

---

### 2. Interfaces de Contrato

#### `MediaItemDAO.java` (interface)

```java
package com.seu.catalog.dao;

import com.seu.catalog.model.MediaItem;
import java.util.List;

/**
 * Define operações de acesso a dados para itens de mídia.
 * Implementadores (MySqlMediaItemDAO, etc.) garantem persistência segura.
 */
public interface MediaItemDAO {
    /**
     * Insere um novo item de mídia no catálogo.
     *
     * @param item item a persistir; não pode ser nulo
     * @return o mesmo item com o id gerado pelo banco preenchido
     * @throws DAOException se ocorrer falha de acesso ao banco
     */
    MediaItem insert(MediaItem item) throws DAOException;

    /**
     * Retorna todos os itens de mídia cadastrados.
     *
     * @return lista de itens (vazio se nenhum); nunca nulo
     * @throws DAOException se ocorrer falha de acesso ao banco
     */
    List<MediaItem> findAll() throws DAOException;

    /**
     * Retorna um item de mídia pelo id.
     *
     * @param id id do item
     * @return item encontrado, ou null se não existir
     * @throws DAOException se ocorrer falha de acesso ao banco
     */
    MediaItem findById(Integer id) throws DAOException;

    /**
     * Atualiza um item de mídia existente.
     *
     * @param item item com as alterações; deve ter id preenchido
     * @return true se foi atualizado, false se o id não existe
     * @throws DAOException se ocorrer falha de acesso ao banco
     */
    boolean update(MediaItem item) throws DAOException;

    /**
     * Deleta um item de mídia pelo id.
     *
     * @param id id do item a deletar
     * @return true se foi deletado, false se o id não existe
     * @throws DAOException se ocorrer falha de acesso ao banco
     */
    boolean delete(Integer id) throws DAOException;

    /**
     * Busca itens por termo (título ou autor/diretor).
     * A busca é tolerante a case e parcial (LIKE).
     *
     * @param term termo de busca (não nulo)
     * @return lista de itens encontrados (vazio se nenhum); nunca nulo
     * @throws DAOException se ocorrer falha de acesso ao banco
     */
    List<MediaItem> searchByTerm(String term) throws DAOException;
}
```

**Nota:** O método `insert` recebe `Connection` ou apenas `MediaItem`?
- **Decisão:** Por enquanto, **apenas `MediaItem`**. A transação é gerenciada no Service.
- **Versão future:** Pode haver overload `insert(MediaItem, Connection)` se o Service precisar gerenciar conexão manualmente.

#### `MovieMetadataProvider.java` (interface, para ext TMDB)

```java
package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;

/**
 * Define contrato para busca de metadados de filmes/séries.
 * Implementadores (FakeMovieMetadataProvider, TmdbMetadataProvider) encapsulam
 * a complexidade de HTTP, parsing JSON, timeout, etc.
 */
public interface MovieMetadataProvider {
    /**
     * Busca metadados de um filme/série por termo.
     *
     * @param term termo de busca (ex: "Inception")
     * @return item populado com metadados (pôster, gênero, sinopse),
     *         ou null se não encontrado ou erro na busca
     */
    MediaItem searchByTitle(String term);

    /**
     * Retorna metadados de um filme/série pelo id externo (ex: TMDB id).
     *
     * @param externalId id no provedor externo
     * @return item populado, ou null se não encontrado
     */
    MediaItem findById(String externalId);
}
```

---

### 3. Exceções Customizadas

#### `DAOException.java`

```java
package com.seu.catalog.exception;

/**
 * Exceção lançada quando há falha em operação de persistência (banco de dados).
 */
public class DAOException extends Exception {
    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### `ValidationException.java`

```java
package com.seu.catalog.exception;

/**
 * Exceção lançada quando um item falha na validação semântica.
 * Exemplo: title vazio, releaseYear não é número, rating > 5.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### `ServiceException.java`

```java
package com.seu.catalog.exception;

/**
 * Exceção lançada quando há falha em operação de negócio (Service).
 * Exemplo: falha em transação após tentar inserir no DAO.
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

### 4. Stubs (implementações mínimas para testes)

#### `FakeMovieMetadataProvider.java` (para testes do Service)

```java
package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;

/**
 * Provider fake (sem rede, sem TMDB) para testes.
 */
public class FakeMovieMetadataProvider implements MovieMetadataProvider {
    @Override
    public MediaItem searchByTitle(String term) {
        // Retorna null ou um item fake dependendo do termo
        return null;
    }

    @Override
    public MediaItem findById(String externalId) {
        return null;
    }
}
```

---

## Travas (Constraints Críticas)

### 🔴 TRAVA 1: Sem Implementação Concreta Aqui
- ❌ Nenhum DAO concreto (`MySqlMediaItemDAO`)
- ❌ Nenhuma classe que toque em banco/HTTP/arquivo
- ✅ Apenas interfaces, POJO, exceções, stubs

**Por quê?** Se você começar a implementar `MySqlMediaItemDAO` aqui, a Task 1 fica bloqueada esperando. Cores definidas = paralelismo.

### 🔴 TRAVA 2: Imutabilidade de Contrato
Uma vez que os contratos saem dessa branch, **não altere assinatura de métodos** sem avisar o time. Se descobrir que precisa de um método extra ou mudar parâmetros:
1. Abra issue/discussão na Task que depende
2. Implemente no `feature/contracts` **primeiro**
3. Depois redeploy das dependências

**Exemplo:** Se Task 1 (DAO) descobrir que precisa de `getConnection()`, essa mudança deve vir de volta aqui antes de commitar na Task 1.

### 🔴 TRAVA 3: Nomes de Pacotes Fixos
Os nomes dos pacotes definidos aqui **são canônicos**:
- `com.seu.catalog.model`
- `com.seu.catalog.dao`
- `com.seu.catalog.service`
- `com.seu.catalog.exception`
- (mais virão em Task 0: `com.seu.catalog.servlet`, `com.seu.catalog.infra`)

Qualquer mudança quebra o contrato.

### 🔴 TRAVA 4: Nenhuma Dependência de Task 0
`feature/contracts` **não depende de Flyway, Docker, ou MySQL**. É puro Java. Se você precisar de configuração, isso vai para Task 0.

---

## Critérios de Aceite

- [ ] Compila: `mvn compile` sucede
- [ ] Pacotes criados: `model`, `dao`, `service`, `exception`
- [ ] `MediaItem` é POJO encapsulado (getters/setters públicos, sem Lombok)
- [ ] `MediaType` enum com MOVIE, SERIES, BOOK
- [ ] `MediaItemDAO` interface com 6 métodos (insert, findAll, findById, update, delete, searchByTerm)
- [ ] `MovieMetadataProvider` interface com 2 métodos (searchByTitle, findById)
- [ ] `DAOException`, `ValidationException`, `ServiceException` criadas
- [ ] `FakeMovieMetadataProvider` implementação stub
- [ ] **Javadoc completo** em PT-BR (classe + métodos públicos, sem warnings)
- [ ] Nenhuma classe .class gerada por Lombok (certifique com `javap`)
- [ ] `mvn javadoc:javadoc` roda sem warnings

---

## Estratégia de Teste

**Não há testes de unidade aqui** (interfaces e POJO não testáveis isoladamente).

Se quiser verificar manualmente:
```bash
# Compile
mvn compile

# Gere Javadoc
mvn javadoc:javadoc

# Verifique se as classes estão no JAR
jar tvf target/my-movies-X.X.jar | grep "model/MediaItem"
```

---

## Checkpoints Durante a Implementação

1. **Crie `MediaItem` + `MediaType`**
   - Compila?
   - Todos os getters/setters funcionam?

2. **Crie `MediaItemDAO`, `MovieMetadataProvider`**
   - Interfaces compilam?
   - Nomes de métodos estão corretos?

3. **Crie exceções + FakeMovieMetadataProvider**
   - Exceções têm construtor com message + cause?
   - FakeMovieMetadataProvider implementa a interface?

4. **Gere Javadoc**
   - Warnings? Corrija antes do commit.

---

## Definition of Done

Uma PR `feature/contracts` só é mergeable se:

- [ ] `mvn clean verify` passa (compile + Javadoc sem warnings)
- [ ] Nenhuma dependência externa (apenas `junit-api` se houver)
- [ ] Javadoc PT-BR em toda classe/método público, sem frases redundantes
- [ ] Sem código comentado / dead code
- [ ] Sem TODOs/FIXMEs
- [ ] Commits claros: `feat: add MediaItem model`, `feat: add DAO interface`, etc.
- [ ] PR descrição explica o contrato e lista os identificadores chave
- [ ] Revisado: checklist do plan.md seção 3 (Javadoc) e seção 2 (Proibições)

---

## Próximos Passos

Após merge dessa task:
- ✅ Task 0 (Infra) **pode começar** (precisa só de Maven + Docker)
- ✅ Task 1 (DAO) **pode começar** (implementa contra `MediaItemDAO`)
- ✅ Task 2 (Service) **pode começar** (implementa `CatalogService` contra DAO + Provider)

**Não bloqueiam um ao outro.**

---

## Notas

- **Não tenha pressa em "fazer certo":** os contratos evoluem com os testes. Se Task 1 descobrir que `searchByTerm` precisa de overload, volte aqui e adicione.
- **Mensagens de erro:** As exceções vão ter mensagens genéricas aqui (ex: "DAO error"). Mensagens amigáveis saem do Service/Servlet.
- **Versionamento:** Não há schema/migrations aqui. Isso é Task 0.

---

**Versão 1.0 | 2026-08-12**

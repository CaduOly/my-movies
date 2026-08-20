package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;
import java.util.List;

/**
 * Define contrato para busca de metadados de filmes/séries.
 * Implementadores encapsulam a complexidade de rede/APIs externas.
 */
public interface MovieMetadataProvider {
    
    /**
     * Busca metadados de um filme/série por termo utilizando o idioma padrão (pt-BR).
     *
     * @param term termo de busca (ex: "Inception")
     * @return lista de itens populados com metadados (pôster, gênero, sinopse),
     *         ou lista vazia se não encontrado ou erro na busca
     */
    default List<MediaItem> searchByTitle(String term) {
        return searchByTitle(term, "pt-BR");
    }

    /**
     * Busca metadados de um filme/série por termo e idioma especificado.
     *
     * @param term termo de busca
     * @param language código do idioma (ex: "pt-BR", "en-US")
     * @return lista de itens populados com metadados
     */
    List<MediaItem> searchByTitle(String term, String language);

    /**
     * Retorna metadados de um filme/série pelo id externo no idioma padrão (pt-BR).
     *
     * @param externalId id no provedor externo
     * @return item populado, ou null se não encontrado
     */
    default MediaItem findById(String externalId) {
        return findById(externalId, "pt-BR");
    }

    /**
     * Retorna metadados de um filme/série pelo id externo e idioma especificado.
     *
     * @param externalId id no provedor externo
     * @param language código do idioma (ex: "pt-BR", "en-US")
     * @return item populado, ou null se não encontrado
     */
    MediaItem findById(String externalId, String language);
}


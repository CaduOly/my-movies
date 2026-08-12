package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;

/**
 * Define contrato para busca de metadados de filmes/séries.
 * Implementadores encapsulam a complexidade de rede/APIs externas.
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
     * Retorna metadados de um filme/série pelo id externo.
     *
     * @param externalId id no provedor externo
     * @return item populado, ou null se não encontrado
     */
    MediaItem findById(String externalId);
}

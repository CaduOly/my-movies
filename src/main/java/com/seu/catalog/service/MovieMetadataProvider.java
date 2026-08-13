package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;
import java.util.List;

/**
 * Define contrato para busca de metadados de filmes/séries.
 * Implementadores encapsulam a complexidade de rede/APIs externas.
 */
public interface MovieMetadataProvider {
    
    /**
     * Busca metadados de um filme/série por termo.
     *
     * @param term termo de busca (ex: "Inception")
     * @return lista de itens populados com metadados (pôster, gênero, sinopse),
     *         ou lista vazia se não encontrado ou erro na busca
     */
    List<MediaItem> searchByTitle(String term);

    /**
     * Retorna metadados de um filme/série pelo id externo.
     *
     * @param externalId id no provedor externo
     * @return item populado, ou null se não encontrado
     */
    MediaItem findById(String externalId);
}

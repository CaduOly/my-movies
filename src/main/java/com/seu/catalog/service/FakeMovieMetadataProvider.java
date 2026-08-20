package com.seu.catalog.service;

import com.seu.catalog.model.MediaItem;
import java.util.List;
import java.util.Collections;

/**
 * Provider fake (sem rede, sem TMDB) para testes isolados do Service.
 */
public class FakeMovieMetadataProvider implements MovieMetadataProvider {
    
    /**
     * Retorna sempre uma lista vazia, simulando um provider offline ou sem resultados.
     *
     * @param term o termo buscado
     * @return lista vazia
     */
    @Override
    public List<MediaItem> searchByTitle(String term) {
        return Collections.emptyList();
    }

    @Override
    public List<MediaItem> searchByTitle(String term, String language) {
        return Collections.emptyList();
    }

    /**
     * Retorna sempre nulo para id externo consultado.
     *
     * @param externalId id externo
     * @return null indicando que não encontrou
     */
    @Override
    public MediaItem findById(String externalId) {
        return null;
    }

    @Override
    public MediaItem findById(String externalId, String language) {
        return null;
    }
}

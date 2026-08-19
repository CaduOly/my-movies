package com.seu.catalog.dao;

import com.seu.catalog.model.MediaItem;
import com.seu.catalog.exception.DAOException;
import java.util.List;

/**
 * Define operações de acesso a dados para itens de mídia.
 * Implementadores garantem persistência segura.
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
     * Busca itens por termo utilizando busca parcial (LIKE) que ignora letras maiúsculas e minúsculas
     * nos campos título, autor/diretor e ano de lançamento.
     *
     * @param term termo de busca (não nulo)
     * @return lista de itens encontrados (vazio se nenhum)
     * @throws DAOException se ocorrer erro de persistência
     */
    List<MediaItem> searchByTerm(String term) throws DAOException;
}

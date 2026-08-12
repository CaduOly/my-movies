package com.seu.catalog.service;

import com.seu.catalog.dao.MediaItemDAO;
import com.seu.catalog.exception.DAOException;
import com.seu.catalog.exception.ServiceException;
import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Serviço de negócio para gerenciar itens de mídia.
 * Responsabilidades:
 * - Validar dados de entrada
 * - Coordenar transações (DAO + Provider)
 * - Traduzir exceções técnicas em semânticas
 *
 * Injeção: construtor recebe DAO e Provider (DIP).
 */
public class CatalogService {
    private static final Logger LOG = Logger.getLogger(CatalogService.class.getName());
    
    private final MediaItemDAO dao;
    private final MovieMetadataProvider metadataProvider;
    private final MediaItemValidator validator;

    /**
     * Cria um serviço de catálogo.
     *
     * @param dao DAO de persistência (não nulo)
     * @param metadataProvider provider de metadados externos (pode ser null para dev local)
     */
    public CatalogService(MediaItemDAO dao, MovieMetadataProvider metadataProvider) {
        this.dao = dao;
        this.metadataProvider = metadataProvider;
        this.validator = new MediaItemValidator();
    }

    /**
     * Cria um novo item de mídia no catálogo.
     *
     * @param item item a cadastrar (validado antes de persistir)
     * @return item com id gerado preenchido
     * @throws ValidationException se item falhar na validação
     * @throws ServiceException se falhar na persistência
     */
    public MediaItem createItem(MediaItem item) throws ValidationException, ServiceException {
        // 1. Validar
        validator.validate(item);

        // 2. Persistir (sem transação explícita aqui; DAO gerencia)
        try {
            return dao.insert(item);
        } catch (DAOException e) {
            LOG.severe("Erro ao inserir item: " + e.getMessage());
            throw new ServiceException("Falha ao cadastrar item no banco de dados", e);
        }
    }

    /**
     * Lista todos os itens do catálogo.
     *
     * @return lista de itens (vazio se nenhum)
     * @throws ServiceException se falhar na persistência
     */
    public List<MediaItem> listAllItems() throws ServiceException {
        try {
            return dao.findAll();
        } catch (DAOException e) {
            LOG.severe("Erro ao listar itens: " + e.getMessage());
            throw new ServiceException("Falha ao listar itens", e);
        }
    }

    /**
     * Retorna um item pelo id.
     *
     * @param id id do item
     * @return item encontrado, ou null se não existir
     * @throws ServiceException se falhar na persistência
     */
    public MediaItem getItemById(Integer id) throws ServiceException {
        try {
            return dao.findById(id);
        } catch (DAOException e) {
            LOG.severe("Erro ao buscar item: " + e.getMessage());
            throw new ServiceException("Falha ao buscar item", e);
        }
    }

    /**
     * Atualiza um item existente.
     *
     * @param item item com alterações (validado antes de persistir)
     * @throws ValidationException se item falhar na validação
     * @throws ServiceException se falhar na persistência ou se item não existir
     */
    public void updateItem(MediaItem item) throws ValidationException, ServiceException {
        // 1. Validar
        validator.validate(item);

        // 2. Verificar existência
        try {
            MediaItem existing = dao.findById(item.getId());
            if (existing == null) {
                throw new ServiceException("Item com id " + item.getId() + " não existe");
            }
        } catch (DAOException e) {
            throw new ServiceException("Falha ao verificar item", e);
        }

        // 3. Atualizar
        try {
            boolean updated = dao.update(item);
            if (!updated) {
                throw new ServiceException("Item com id " + item.getId() + " não pode ser atualizado");
            }
        } catch (DAOException e) {
            LOG.severe("Erro ao atualizar item: " + e.getMessage());
            throw new ServiceException("Falha ao atualizar item no banco de dados", e);
        }
    }

    /**
     * Deleta um item.
     *
     * @param id id do item a deletar
     * @throws ServiceException se falhar na persistência ou se item não existir
     */
    public void deleteItem(Integer id) throws ServiceException {
        try {
            // Verificar existência
            MediaItem existing = dao.findById(id);
            if (existing == null) {
                throw new ServiceException("Item com id " + id + " não existe");
            }

            // Deletar
            boolean deleted = dao.delete(id);
            if (!deleted) {
                throw new ServiceException("Falha ao deletar item");
            }
        } catch (DAOException e) {
            LOG.severe("Erro ao deletar item: " + e.getMessage());
            throw new ServiceException("Falha ao deletar item do banco de dados", e);
        }
    }

    /**
     * Busca itens por termo (título ou autor/diretor).
     *
     * @param term termo de busca
     * @return lista de itens encontrados
     * @throws ServiceException se falhar na persistência
     */
    public List<MediaItem> searchItems(String term) throws ServiceException {
        if (term == null || term.trim().isEmpty()) {
            throw new ServiceException("Termo de busca não pode estar vazio");
        }

        try {
            return dao.searchByTerm(term);
        } catch (DAOException e) {
            LOG.severe("Erro ao buscar por termo: " + e.getMessage());
            throw new ServiceException("Falha ao buscar itens", e);
        }
    }
}

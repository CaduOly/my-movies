package com.seu.catalog.exception;

/**
 * Exceção lançada quando há falha em operação de negócio (Service).
 * Exemplo: falha em transação após tentar inserir no DAO.
 */
public class ServiceException extends Exception {

    /**
     * Constrói uma nova ServiceException com a mensagem especificada.
     *
     * @param message a mensagem de detalhe
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constrói uma nova ServiceException com a mensagem e causa especificadas.
     *
     * @param message a mensagem de detalhe
     * @param cause   a causa base
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

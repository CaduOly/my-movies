package com.seu.catalog.exception;

/**
 * Exceção lançada quando há falha em operação de persistência (banco de dados).
 */
public class DAOException extends Exception {

    /**
     * Constrói uma nova DAOException com a mensagem especificada.
     *
     * @param message a mensagem de detalhe
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Constrói uma nova DAOException com a mensagem e causa especificadas.
     *
     * @param message a mensagem de detalhe
     * @param cause   a causa base
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}

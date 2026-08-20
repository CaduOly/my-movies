package com.seu.catalog.exception;

/**
 * Exceção lançada quando um item falha na validação semântica.
 * Exemplo: title vazio, releaseYear não é número, rating maior que 5.
 */
public class ValidationException extends Exception {

    /**
     * Constrói uma nova ValidationException com a mensagem especificada.
     *
     * @param message a mensagem de detalhe
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constrói uma nova ValidationException com a mensagem e causa especificadas.
     *
     * @param message a mensagem de detalhe
     * @param cause   a causa base
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

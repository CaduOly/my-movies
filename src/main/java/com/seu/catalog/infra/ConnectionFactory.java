package com.seu.catalog.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Factory para conexões JDBC.
 * Lê credenciais de variáveis de ambiente com fallback para desenvolvimento local.
 */
public class ConnectionFactory {
    
    private static final String JDBC_URL = System.getenv("JDBC_URL") != null 
        ? System.getenv("JDBC_URL")
        : "jdbc:mysql://localhost:3306/my_movies?useUnicode=true&characterEncoding=UTF-8";
    
    private static final String JDBC_USER = System.getenv("JDBC_USER") != null
        ? System.getenv("JDBC_USER")
        : "app";
    
    private static final String JDBC_PASSWORD = System.getenv("JDBC_PASSWORD") != null
        ? System.getenv("JDBC_PASSWORD")
        : "app123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado", e);
        }
    }

    /**
     * Retorna uma nova conexão JDBC.
     *
     * @return conexão com o banco
     * @throws SQLException se não conseguir conectar
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}

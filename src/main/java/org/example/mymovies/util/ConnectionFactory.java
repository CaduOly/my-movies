package org.example.mymovies.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões para o banco de dados da aplicação.
 * Fornece conexões JDBC configuradas através de variáveis de ambiente ou valores padrão.
 */
public class ConnectionFactory {
    /**
     * Fornece uma conexão JDBC configurada.
     * Utiliza as variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD caso presentes,
     * ou valores padrão locais para acesso ao banco MySQL.
     * 
     * @return Uma nova conexão ativa com o banco de dados.
     * @throws SQLException Se o driver não for encontrado ou as credenciais forem inválidas.
     * @since 1.0
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = System.getenv("DB_URL");
            if (url == null || url.isEmpty()) url = "jdbc:mysql://localhost:3306/mymovies";
            
            String user = System.getenv("DB_USER");
            if (user == null || user.isEmpty()) user = "root";
            
            String password = System.getenv("DB_PASSWORD");
            if (password == null || password.isEmpty()) password = "root";
            
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }
}

package org.example.mymovies.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
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

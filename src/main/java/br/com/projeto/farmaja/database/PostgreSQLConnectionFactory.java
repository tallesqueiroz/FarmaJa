package br.com.projeto.farmaja.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnectionFactory implements ConnectionFactory {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/farmaja";
    private static final String DB_USER = "postgres";  // Usuário
    private static final String DB_PASSWORD = "Postgresql@!8125"; // Senha

    private Connection connection = null;

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Conexão com PostgreSQL estabelecida.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL não encontrado", e);
            }
        }
        return connection;
    }

    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
            System.out.println("Conexão com PostgreSQL fechada.");
        }
    }
}
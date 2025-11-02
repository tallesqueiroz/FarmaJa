package br.com.projeto.farmaja.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionFactory implements ConnectionFactory {
    private static final String DB_URL = "jdbc:h2:./farmaja";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private Connection connection = null;

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Conexão com banco de dados H2 estabelecida.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver H2 não encontrado", e);
            }
        }
        return connection;
    }

    @Override
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
            System.out.println("Conexão com banco de dados H2 fechada.");
        }
    }
}
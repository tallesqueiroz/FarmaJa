package br.com.projeto.farmaja.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
    Connection getConnection() throws SQLException;
    void closeConnection() throws SQLException;
}
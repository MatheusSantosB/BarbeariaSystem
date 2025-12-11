package com.barbearia.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private static Connection connection;

    public static Connection getConexao() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:h2:./database/barbearia;DB_CLOSE_DELAY=-1";
                String user = "sa";
                String password = "";

                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return connection;
    }

    public static void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
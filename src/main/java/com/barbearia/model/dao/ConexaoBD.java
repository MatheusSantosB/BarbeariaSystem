package com.barbearia.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private static Connection connection;

    // Método getConexao (em português para manter compatibilidade)
    public static Connection getConexao() {
        try {
            // Verifica se é nulo OU se a conexão caiu/fechou
            if (connection == null || connection.isClosed()) {
                // DB_CLOSE_DELAY=-1 é obrigatório para o H2 não apagar os dados sozinho
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
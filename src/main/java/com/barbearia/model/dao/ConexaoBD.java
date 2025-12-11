package com.barbearia.model.dao;

import com.barbearia.util.LogUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private static Connection connection;

    // Nome mantido como 'getConexao' para não quebrar seus DAOs
    public static Connection getConexao() {
        try {
            // Verifica se é nulo OU se está fechada
            if (connection == null || connection.isClosed()) {
                // Adicionado DB_CLOSE_DELAY=-1
                String url = "jdbc:h2:./database/barbearia;DB_CLOSE_DELAY=-1";
                String user = "sa";
                String password = "";

                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            LogUtils.gravarErro("ERRO DE CONEXÃO COM BANCO DE DADOS", e);
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return connection;
    }

    // Nome mantido como 'fecharConexao'
    public static void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LogUtils.gravarErro("ERRO AO FECHAR CONEXÃO", e);
            e.printStackTrace();
        }
    }
}
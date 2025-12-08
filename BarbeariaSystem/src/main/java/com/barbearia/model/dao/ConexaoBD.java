package com.barbearia.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL = "jdbc:h2:./database/barbearia;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection conexao;

    private ConexaoBD() {}

    public static Connection getConexao() {
        if (conexao == null) {
            try {
                conexao = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexão estabelecida com sucesso!");
            } catch (SQLException e) {
                System.err.println("Erro ao conectar ao banco: " + e.getMessage());
                throw new RuntimeException("Erro na conexão com o banco", e);
            }
        }
        return conexao;
    }

    public static void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
                conexao = null;
                System.out.println("Conexão fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
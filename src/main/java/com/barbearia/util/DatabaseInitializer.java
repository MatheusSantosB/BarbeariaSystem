package com.barbearia.util;

import com.barbearia.model.dao.ConexaoBD;
import com.barbearia.util.LogUtils; // Importante
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        criarTabelas();
        popularDadosIniciais();
    }

    private static void criarTabelas() {
        String[] scripts = {
                "CREATE TABLE IF NOT EXISTS cliente (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, telefone VARCHAR(20), email VARCHAR(100), data_cadastro DATE DEFAULT CURRENT_DATE)",
                "CREATE TABLE IF NOT EXISTS profissional (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, especialidade VARCHAR(50), telefone VARCHAR(20), ativo BOOLEAN DEFAULT true)",
                "CREATE TABLE IF NOT EXISTS servico (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(50) NOT NULL, descricao VARCHAR(200), preco DECIMAL(10,2) NOT NULL, duracao_min INT DEFAULT 30)",
                "CREATE TABLE IF NOT EXISTS agendamento (id INT AUTO_INCREMENT PRIMARY KEY, cliente_id INT, profissional_id INT, data DATE NOT NULL, hora TIME NOT NULL, status VARCHAR(20) DEFAULT 'AGENDADO', observacoes TEXT, FOREIGN KEY (cliente_id) REFERENCES cliente(id), FOREIGN KEY (profissional_id) REFERENCES profissional(id))",
                "CREATE TABLE IF NOT EXISTS agendamento_servico (agendamento_id INT, servico_id INT, PRIMARY KEY (agendamento_id, servico_id), FOREIGN KEY (agendamento_id) REFERENCES agendamento(id), FOREIGN KEY (servico_id) REFERENCES servico(id))",
                "CREATE TABLE IF NOT EXISTS pagamento (id INT AUTO_INCREMENT PRIMARY KEY, agendamento_id INT UNIQUE, valor DECIMAL(10,2) NOT NULL, forma_pagamento VARCHAR(20), status VARCHAR(20) DEFAULT 'PENDENTE', data_pagamento TIMESTAMP, FOREIGN KEY (agendamento_id) REFERENCES agendamento(id))"
        };

        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement()) {
            for (String script : scripts) {
                stmt.execute(script);
            }
        } catch (Exception e) {
            LogUtils.gravarErro("Erro ao criar tabelas", e);
            throw new RuntimeException("Erro na inicialização do banco", e);
        }
    }

    private static void popularDadosIniciais() {
        // Implementação simplificada para garantir funcionamento
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement()) {
            // Seus inserts aqui (opcional) ou deixe vazio se já tiver dados
        } catch (Exception e) {
            // Ignora erro de duplicidade
        }
    }
}
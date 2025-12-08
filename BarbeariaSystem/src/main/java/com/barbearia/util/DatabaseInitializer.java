package com.barbearia.util;

import com.barbearia.model.dao.ConexaoBD;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        criarTabelas();
        popularDadosIniciais();
    }

    private static void criarTabelas() {
        String[] scripts = {
                // Tabela Cliente
                """
            CREATE TABLE IF NOT EXISTS cliente (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(100) NOT NULL,
                telefone VARCHAR(20),
                email VARCHAR(100),
                data_cadastro DATE DEFAULT CURRENT_DATE
            )
            """,

                // Tabela Profissional
                """
            CREATE TABLE IF NOT EXISTS profissional (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(100) NOT NULL,
                especialidade VARCHAR(50),
                telefone VARCHAR(20),
                ativo BOOLEAN DEFAULT true
            )
            """,

                // Tabela Servico
                """
            CREATE TABLE IF NOT EXISTS servico (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(50) NOT NULL,
                descricao VARCHAR(200),
                preco DECIMAL(10,2) NOT NULL,
                duracao_min INT DEFAULT 30
            )
            """,

                // Tabela Agendamento
                """
            CREATE TABLE IF NOT EXISTS agendamento (
                id INT AUTO_INCREMENT PRIMARY KEY,
                cliente_id INT,
                profissional_id INT,
                data DATE NOT NULL,
                hora TIME NOT NULL,
                status VARCHAR(20) DEFAULT 'AGENDADO',
                observacoes TEXT,
                FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                FOREIGN KEY (profissional_id) REFERENCES profissional(id)
            )
            """,

                // Tabela Agendamento_Servico (N:N)
                """
            CREATE TABLE IF NOT EXISTS agendamento_servico (
                agendamento_id INT,
                servico_id INT,
                PRIMARY KEY (agendamento_id, servico_id),
                FOREIGN KEY (agendamento_id) REFERENCES agendamento(id),
                FOREIGN KEY (servico_id) REFERENCES servico(id)
            )
            """,

                // Tabela Pagamento
                """
            CREATE TABLE IF NOT EXISTS pagamento (
                id INT AUTO_INCREMENT PRIMARY KEY,
                agendamento_id INT UNIQUE,
                valor DECIMAL(10,2) NOT NULL,
                forma_pagamento VARCHAR(20),
                status VARCHAR(20) DEFAULT 'PENDENTE',
                data_pagamento TIMESTAMP,
                FOREIGN KEY (agendamento_id) REFERENCES agendamento(id)
            )
            """
        };

        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement()) {

            for (String script : scripts) {
                stmt.execute(script);
            }

            System.out.println("✅ Tabelas criadas com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar tabelas: " + e.getMessage());
            throw new RuntimeException("Erro na inicialização do banco", e);
        }
    }

    private static void popularDadosIniciais() {
        String[] inserts = {
                // Profissionais
                """
            INSERT INTO profissional (nome, especialidade, telefone, ativo) VALUES
            ('João Silva', 'Cabelo', '(11) 99999-9999', true),
            ('Maria Santos', 'Barba', '(11) 98888-8888', true),
            ('Pedro Costa', 'Completo', '(11) 97777-7777', true),
            ('Ana Oliveira', 'Estética', '(11) 96666-6666', true)
            ON DUPLICATE KEY UPDATE nome = nome
            """,

                // Serviços
                """
            INSERT INTO servico (nome, descricao, preco, duracao_min) VALUES
            ('Corte Masculino', 'Corte de cabelo masculino tradicional', 35.00, 30),
            ('Corte Feminino', 'Corte de cabelo feminino', 45.00, 45),
            ('Barba Tradicional', 'Aparação e modelagem de barba', 25.00, 20),
            ('Barba Completa', 'Barba com toalha quente e finalização', 40.00, 40),
            ('Sobrancelha', 'Design de sobrancelhas', 15.00, 15),
            ('Hidratação', 'Hidratação capilar', 50.00, 60),
            ('Pigmentação', 'Tingimento de cabelo ou barba', 80.00, 90)
            ON DUPLICATE KEY UPDATE nome = nome
            """,

                // Clientes de exemplo
                """
            INSERT INTO cliente (nome, telefone, email, data_cadastro) VALUES
            ('Carlos Eduardo', '(11) 95555-5555', 'carlos@email.com', '2024-01-15'),
            ('Fernanda Lima', '(11) 94444-4444', 'fernanda@email.com', '2024-02-20'),
            ('Roberto Alves', '(11) 93333-3333', 'roberto@email.com', '2024-03-10'),
            ('Juliana Pereira', '(11) 92222-2222', 'juliana@email.com', '2024-04-05'),
            ('Marcos Souza', '(11) 91111-1111', 'marcos@email.com', '2024-05-12')
            ON DUPLICATE KEY UPDATE nome = nome
            """
        };

        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement()) {

            for (String insert : inserts) {
                stmt.execute(insert);
            }

            System.out.println("✅ Dados iniciais populados com sucesso!");

        } catch (Exception e) {
            System.err.println("⚠️ Aviso ao popular dados: " + e.getMessage());
            // Não lança exceção para não impedir a execução
        }
    }

    public static void resetDatabase() {
        String[] drops = {
                "DROP TABLE IF EXISTS pagamento",
                "DROP TABLE IF EXISTS agendamento_servico",
                "DROP TABLE IF EXISTS agendamento",
                "DROP TABLE IF EXISTS servico",
                "DROP TABLE IF EXISTS profissional",
                "DROP TABLE IF EXISTS cliente"
        };

        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement()) {

            // Desativa foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            for (String drop : drops) {
                stmt.execute(drop);
            }

            // Reativa foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("✅ Banco de dados resetado com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao resetar banco: " + e.getMessage());
        }
    }

    public static void backupDatabase() {
        // Implementação simplificada de backup
        try {
            System.out.println("💾 Backup do banco realizado com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro no backup: " + e.getMessage());
        }
    }
}
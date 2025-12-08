package com.barbearia.model.dao;

import com.barbearia.model.entity.Pagamento;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    public void salvar(Pagamento pagamento) {
        String sql = "INSERT INTO pagamento (agendamento_id, valor, forma_pagamento, status, data_pagamento) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pagamento.getAgendamento().getId());
            stmt.setDouble(2, pagamento.getValor());
            stmt.setString(3, pagamento.getFormaPagamento().name());
            stmt.setString(4, pagamento.getStatus().name());

            if (pagamento.getDataPagamento() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(pagamento.getDataPagamento()));
            } else {
                stmt.setTimestamp(5, null);
            }

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pagamento.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pagamento: " + e.getMessage(), e);
        }
    }

    public void atualizar(Pagamento pagamento) {
        String sql = "UPDATE pagamento SET valor = ?, forma_pagamento = ?, status = ?, data_pagamento = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, pagamento.getValor());
            stmt.setString(2, pagamento.getFormaPagamento().name());
            stmt.setString(3, pagamento.getStatus().name());

            if (pagamento.getDataPagamento() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(pagamento.getDataPagamento()));
            } else {
                stmt.setTimestamp(4, null);
            }

            stmt.setInt(5, pagamento.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pagamento: " + e.getMessage(), e);
        }
    }

    public Pagamento buscarPorAgendamentoId(Integer agendamentoId) {
        String sql = "SELECT * FROM pagamento WHERE agendamento_id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, agendamentoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPagamento(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pagamento: " + e.getMessage(), e);
        }

        return null;
    }

    // FILTROS
    public List<Pagamento> buscarPorFormaPagamento(String formaPagamento) {
        String sql = "SELECT * FROM pagamento WHERE forma_pagamento = ? ORDER BY data_pagamento DESC";
        return executarConsultaComFiltro(sql, formaPagamento);
    }

    public List<Pagamento> buscarPorStatus(String status) {
        String sql = "SELECT * FROM pagamento WHERE status = ? ORDER BY data_pagamento DESC";
        return executarConsultaComFiltro(sql, status);
    }

    public List<Pagamento> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT * FROM pagamento WHERE data_pagamento BETWEEN ? AND ? ORDER BY data_pagamento DESC";
        List<Pagamento> pagamentos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pagamentos.add(mapearPagamento(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return pagamentos;
    }

    public List<Pagamento> buscarPagamentosPendentes() {
        String sql = "SELECT * FROM pagamento WHERE status = 'PENDENTE' ORDER BY data_pagamento";
        return executarConsultaComFiltro(sql, null);
    }

    public List<Pagamento> listarTodos() {
        String sql = "SELECT * FROM pagamento ORDER BY data_pagamento DESC";
        return executarConsultaComFiltro(sql, null);
    }

    private List<Pagamento> executarConsultaComFiltro(String sql, String parametro) {
        List<Pagamento> pagamentos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (parametro != null) {
                stmt.setString(1, parametro);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pagamentos.add(mapearPagamento(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return pagamentos;
    }

    private Pagamento mapearPagamento(ResultSet rs) throws SQLException {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(rs.getInt("id"));
        pagamento.setValor(rs.getDouble("valor"));

        // Forma de pagamento
        String formaPagamentoStr = rs.getString("forma_pagamento");
        if (formaPagamentoStr != null) {
            try {
                pagamento.setFormaPagamento(Pagamento.FormaPagamento.valueOf(formaPagamentoStr));
            } catch (IllegalArgumentException e) {
                pagamento.setFormaPagamento(Pagamento.FormaPagamento.DINHEIRO);
            }
        }

        // Status
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                pagamento.setStatus(Pagamento.StatusPagamento.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                pagamento.setStatus(Pagamento.StatusPagamento.PENDENTE);
            }
        }

        // Data do pagamento
        Timestamp dataPagamento = rs.getTimestamp("data_pagamento");
        if (dataPagamento != null) {
            pagamento.setDataPagamento(dataPagamento.toLocalDateTime());
        }

        // Nota: agendamento não é mapeado aqui para evitar recursividade
        // Será carregado separadamente se necessário

        return pagamento;
    }
}
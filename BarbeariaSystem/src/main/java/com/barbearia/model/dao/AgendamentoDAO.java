package com.barbearia.model.dao;

import com.barbearia.model.entity.Agendamento;
import com.barbearia.model.entity.Cliente;
import com.barbearia.model.entity.Profissional;
import com.barbearia.model.entity.Servico;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {

    public void salvar(Agendamento agendamento) {
        String sql = "INSERT INTO agendamento (cliente_id, profissional_id, data, hora, status, observacoes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, agendamento.getCliente().getId());
            stmt.setInt(2, agendamento.getProfissional().getId());
            stmt.setDate(3, Date.valueOf(agendamento.getData()));
            stmt.setTime(4, Time.valueOf(agendamento.getHora()));
            stmt.setString(5, agendamento.getStatus().name());
            stmt.setString(6, agendamento.getObservacoes());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int agendamentoId = rs.getInt(1);
                agendamento.setId(agendamentoId);

                // Salvar serviços do agendamento
                salvarServicosAgendamento(agendamentoId, agendamento.getServicos(), conn);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar agendamento: " + e.getMessage(), e);
        }
    }

    private void salvarServicosAgendamento(int agendamentoId, List<Servico> servicos, Connection conn) throws SQLException {
        String sql = "INSERT INTO agendamento_servico (agendamento_id, servico_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Servico servico : servicos) {
                stmt.setInt(1, agendamentoId);
                stmt.setInt(2, servico.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void atualizar(Agendamento agendamento) {
        String sql = "UPDATE agendamento SET cliente_id = ?, profissional_id = ?, data = ?, hora = ?, status = ?, observacoes = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, agendamento.getCliente().getId());
            stmt.setInt(2, agendamento.getProfissional().getId());
            stmt.setDate(3, Date.valueOf(agendamento.getData()));
            stmt.setTime(4, Time.valueOf(agendamento.getHora()));
            stmt.setString(5, agendamento.getStatus().name());
            stmt.setString(6, agendamento.getObservacoes());
            stmt.setInt(7, agendamento.getId());

            stmt.executeUpdate();

            // Atualizar serviços
            atualizarServicosAgendamento(agendamento.getId(), agendamento.getServicos(), conn);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar agendamento: " + e.getMessage(), e);
        }
    }

    private void atualizarServicosAgendamento(int agendamentoId, List<Servico> servicos, Connection conn) throws SQLException {
        // Primeiro remove os serviços antigos
        String deleteSql = "DELETE FROM agendamento_servico WHERE agendamento_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, agendamentoId);
            deleteStmt.executeUpdate();
        }

        // Depois insere os novos
        salvarServicosAgendamento(agendamentoId, servicos, conn);
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM agendamento WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir agendamento: " + e.getMessage(), e);
        }
    }

    public Agendamento buscarPorId(Integer id) {
        String sql = """
            SELECT a.*, c.nome as cliente_nome, c.telefone as cliente_telefone, 
                   p.nome as profissional_nome, p.especialidade
            FROM agendamento a
            JOIN cliente c ON a.cliente_id = c.id
            JOIN profissional p ON a.profissional_id = p.id
            WHERE a.id = ?
            """;

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Agendamento agendamento = mapearAgendamento(rs);

                // Buscar serviços do agendamento
                List<Servico> servicos = buscarServicosPorAgendamento(id, conn);
                agendamento.setServicos(servicos);

                return agendamento;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agendamento: " + e.getMessage(), e);
        }

        return null;
    }

    // FILTROS (mínimo 3)
    public List<Agendamento> buscarPorData(LocalDate data) {
        String sql = """
            SELECT a.*, c.nome as cliente_nome, c.telefone as cliente_telefone, 
                   p.nome as profissional_nome, p.especialidade
            FROM agendamento a
            JOIN cliente c ON a.cliente_id = c.id
            JOIN profissional p ON a.profissional_id = p.id
            WHERE a.data = ?
            ORDER BY a.hora
            """;

        return executarConsultaComFiltro(sql, data, null, null);
    }

    public List<Agendamento> buscarPorCliente(Integer clienteId) {
        String sql = """
            SELECT a.*, c.nome as cliente_nome, c.telefone as cliente_telefone, 
                   p.nome as profissional_nome, p.especialidade
            FROM agendamento a
            JOIN cliente c ON a.cliente_id = c.id
            JOIN profissional p ON a.profissional_id = p.id
            WHERE a.cliente_id = ?
            ORDER BY a.data DESC, a.hora
            """;

        return executarConsultaComFiltro(sql, null, clienteId, null);
    }

    public List<Agendamento> buscarPorProfissional(Integer profissionalId) {
        String sql = """
            SELECT a.*, c.nome as cliente_nome, c.telefone as cliente_telefone, 
                   p.nome as profissional_nome, p.especialidade
            FROM agendamento a
            JOIN cliente c ON a.cliente_id = c.id
            JOIN profissional p ON a.profissional_id = p.id
            WHERE a.profissional_id = ?
            ORDER BY a.data, a.hora
            """;

        return executarConsultaComFiltro(sql, null, null, profissionalId);
    }

    public List<Agendamento> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT a.*, c.nome as cliente_nome, c.telefone as cliente_telefone, 
                   p.nome as profissional_nome, p.especialidade
            FROM agendamento a
            JOIN cliente c ON a.cliente_id = c.id
            JOIN profissional p ON a.profissional_id = p.id
            WHERE a.data BETWEEN ? AND ?
            ORDER BY a.data, a.hora
            """;

        List<Agendamento> agendamentos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Agendamento agendamento = mapearAgendamento(rs);

                // Buscar serviços
                List<Servico> servicos = buscarServicosPorAgendamento(agendamento.getId(), conn);
                agendamento.setServicos(servicos);

                agendamentos.add(agendamento);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return agendamentos;
    }

    public List<Agendamento> buscarPorStatus(String status) {
        String sql = """
            SELECT a.*, c.nome as cliente_nome, c.telefone as cliente_telefone, 
                   p.nome as profissional_nome, p.especialidade
            FROM agendamento a
            JOIN cliente c ON a.cliente_id = c.id
            JOIN profissional p ON a.profissional_id = p.id
            WHERE a.status = ?
            ORDER BY a.data DESC
            """;

        return executarConsultaComFiltro(sql, null, null, null, status);
    }

    public List<Agendamento> listarTodos() {
        String sql = """
            SELECT a.*, c.nome as cliente_nome, c.telefone as cliente_telefone, 
                   p.nome as profissional_nome, p.especialidade
            FROM agendamento a
            JOIN cliente c ON a.cliente_id = c.id
            JOIN profissional p ON a.profissional_id = p.id
            ORDER BY a.data DESC, a.hora
            """;

        return executarConsultaComFiltro(sql, null, null, null);
    }

    private List<Agendamento> executarConsultaComFiltro(String sql, LocalDate data, Integer clienteId, Integer profissionalId) {
        return executarConsultaComFiltro(sql, data, clienteId, profissionalId, null);
    }

    private List<Agendamento> executarConsultaComFiltro(String sql, LocalDate data, Integer clienteId, Integer profissionalId, String status) {
        List<Agendamento> agendamentos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (data != null) {
                stmt.setDate(paramIndex++, Date.valueOf(data));
            }
            if (clienteId != null) {
                stmt.setInt(paramIndex++, clienteId);
            }
            if (profissionalId != null) {
                stmt.setInt(paramIndex++, profissionalId);
            }
            if (status != null) {
                stmt.setString(paramIndex, status);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Agendamento agendamento = mapearAgendamento(rs);

                // Buscar serviços
                List<Servico> servicos = buscarServicosPorAgendamento(agendamento.getId(), conn);
                agendamento.setServicos(servicos);

                agendamentos.add(agendamento);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return agendamentos;
    }

    private List<Servico> buscarServicosPorAgendamento(int agendamentoId, Connection conn) throws SQLException {
        List<Servico> servicos = new ArrayList<>();
        String sql = """
            SELECT s.* 
            FROM servico s
            JOIN agendamento_servico ag ON s.id = ag.servico_id
            WHERE ag.agendamento_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agendamentoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Servico servico = new Servico();
                servico.setId(rs.getInt("id"));
                servico.setNome(rs.getString("nome"));
                servico.setDescricao(rs.getString("descricao"));
                servico.setPreco(rs.getDouble("preco"));
                servico.setDuracaoMinutos(rs.getInt("duracao_min"));
                servicos.add(servico);
            }
        }

        return servicos;
    }

    private Agendamento mapearAgendamento(ResultSet rs) throws SQLException {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(rs.getInt("id"));

        // Data e hora
        Date data = rs.getDate("data");
        if (data != null) {
            agendamento.setData(data.toLocalDate());
        }

        Time hora = rs.getTime("hora");
        if (hora != null) {
            agendamento.setHora(hora.toLocalTime());
        }

        // Status
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                agendamento.setStatus(Agendamento.StatusAgendamento.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                agendamento.setStatus(Agendamento.StatusAgendamento.AGENDADO);
            }
        }

        agendamento.setObservacoes(rs.getString("observacoes"));

        // Cliente
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setNome(rs.getString("cliente_nome"));
        cliente.setTelefone(rs.getString("cliente_telefone"));
        agendamento.setCliente(cliente);

        // Profissional
        Profissional profissional = new Profissional();
        profissional.setId(rs.getInt("profissional_id"));
        profissional.setNome(rs.getString("profissional_nome"));
        profissional.setEspecialidade(rs.getString("especialidade"));
        agendamento.setProfissional(profissional);

        return agendamento;
    }
}
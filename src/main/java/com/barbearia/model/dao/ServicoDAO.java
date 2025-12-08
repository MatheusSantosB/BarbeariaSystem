package com.barbearia.model.dao;

import com.barbearia.model.entity.Servico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    public void salvar(Servico servico) {
        String sql = "INSERT INTO servico (nome, descricao, preco, duracao_min) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setDouble(3, servico.getPreco());
            stmt.setInt(4, servico.getDuracaoMinutos());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                servico.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar serviço: " + e.getMessage(), e);
        }
    }

    public void atualizar(Servico servico) {
        String sql = "UPDATE servico SET nome = ?, descricao = ?, preco = ?, duracao_min = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servico.getNome());
            stmt.setString(2, servico.getDescricao());
            stmt.setDouble(3, servico.getPreco());
            stmt.setInt(4, servico.getDuracaoMinutos());
            stmt.setInt(5, servico.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar serviço: " + e.getMessage(), e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM servico WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir serviço: " + e.getMessage(), e);
        }
    }

    public Servico buscarPorId(Integer id) {
        String sql = "SELECT * FROM servico WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearServico(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar serviço: " + e.getMessage(), e);
        }

        return null;
    }

    // FILTROS
    public List<Servico> buscarPorNome(String nome) {
        String sql = "SELECT * FROM servico WHERE nome LIKE ? ORDER BY nome";
        return executarConsultaComFiltro(sql, "%" + nome + "%");
    }

    public List<Servico> buscarPorPrecoMaximo(Double precoMaximo) {
        String sql = "SELECT * FROM servico WHERE preco <= ? ORDER BY preco";
        List<Servico> servicos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, precoMaximo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                servicos.add(mapearServico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return servicos;
    }

    public List<Servico> buscarMaisPopulares(int limite) {
        String sql = """
            SELECT s.*, COUNT(ag.agendamento_id) as quantidade 
            FROM servico s 
            LEFT JOIN agendamento_servico ag ON s.id = ag.servico_id 
            GROUP BY s.id 
            ORDER BY quantidade DESC 
            LIMIT ?
            """;

        List<Servico> servicos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                servicos.add(mapearServico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return servicos;
    }

    public List<Servico> listarTodos() {
        String sql = "SELECT * FROM servico ORDER BY nome";
        return executarConsultaComFiltro(sql, null);
    }

    private List<Servico> executarConsultaComFiltro(String sql, String parametro) {
        List<Servico> servicos = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (parametro != null) {
                stmt.setString(1, parametro);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                servicos.add(mapearServico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return servicos;
    }

    private Servico mapearServico(ResultSet rs) throws SQLException {
        Servico servico = new Servico();
        servico.setId(rs.getInt("id"));
        servico.setNome(rs.getString("nome"));
        servico.setDescricao(rs.getString("descricao"));
        servico.setPreco(rs.getDouble("preco"));
        servico.setDuracaoMinutos(rs.getInt("duracao_min"));
        return servico;
    }
}
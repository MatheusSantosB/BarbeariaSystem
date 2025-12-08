package com.barbearia.model.dao;

import com.barbearia.model.entity.Profissional;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfissionalDAO {

    public void salvar(Profissional profissional) {
        String sql = "INSERT INTO profissional (nome, especialidade, telefone, ativo) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, profissional.getNome());
            stmt.setString(2, profissional.getEspecialidade());
            stmt.setString(3, profissional.getTelefone());
            stmt.setBoolean(4, profissional.isAtivo());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                profissional.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar profissional: " + e.getMessage(), e);
        }
    }

    public void atualizar(Profissional profissional) {
        String sql = "UPDATE profissional SET nome = ?, especialidade = ?, telefone = ?, ativo = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profissional.getNome());
            stmt.setString(2, profissional.getEspecialidade());
            stmt.setString(3, profissional.getTelefone());
            stmt.setBoolean(4, profissional.isAtivo());
            stmt.setInt(5, profissional.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar profissional: " + e.getMessage(), e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM profissional WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir profissional: " + e.getMessage(), e);
        }
    }

    public Profissional buscarPorId(Integer id) {
        String sql = "SELECT * FROM profissional WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearProfissional(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar profissional: " + e.getMessage(), e);
        }

        return null;
    }

    // FILTROS
    public List<Profissional> buscarPorNome(String nome) {
        String sql = "SELECT * FROM profissional WHERE nome LIKE ? ORDER BY nome";
        return executarConsultaComFiltro(sql, "%" + nome + "%");
    }

    public List<Profissional> buscarPorEspecialidade(String especialidade) {
        String sql = "SELECT * FROM profissional WHERE especialidade LIKE ? ORDER BY nome";
        return executarConsultaComFiltro(sql, "%" + especialidade + "%");
    }

    public List<Profissional> buscarAtivos() {
        String sql = "SELECT * FROM profissional WHERE ativo = true ORDER BY nome";
        return executarConsultaComFiltro(sql, null);
    }

    public List<Profissional> listarTodos() {
        String sql = "SELECT * FROM profissional ORDER BY nome";
        return executarConsultaComFiltro(sql, null);
    }

    private List<Profissional> executarConsultaComFiltro(String sql, String parametro) {
        List<Profissional> profissionais = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (parametro != null) {
                stmt.setString(1, parametro);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                profissionais.add(mapearProfissional(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return profissionais;
    }

    private Profissional mapearProfissional(ResultSet rs) throws SQLException {
        Profissional profissional = new Profissional();
        profissional.setId(rs.getInt("id"));
        profissional.setNome(rs.getString("nome"));
        profissional.setEspecialidade(rs.getString("especialidade"));
        profissional.setTelefone(rs.getString("telefone"));
        profissional.setAtivo(rs.getBoolean("ativo"));
        return profissional;
    }
}
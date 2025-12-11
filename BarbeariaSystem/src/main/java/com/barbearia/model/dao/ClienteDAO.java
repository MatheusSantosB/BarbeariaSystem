package com.barbearia.model.dao;

import com.barbearia.model.entity.Cliente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, telefone, email, data_cadastro) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEmail());
            stmt.setDate(4, Date.valueOf(cliente.getDataCadastro()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cliente.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage(), e);
        }
    }

    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getTelefone());
            stmt.setString(3, cliente.getEmail());
            stmt.setInt(4, cliente.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void excluir(Integer id) {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorId(Integer id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage(), e);
        }

        return null;
    }

    // FILTROS (mínimo 3)
    public List<Cliente> buscarPorNome(String nome) {
        String sql = "SELECT * FROM cliente WHERE nome LIKE ? ORDER BY nome";
        return executarConsultaComFiltro(sql, "%" + nome + "%");
    }

    public List<Cliente> buscarPorTelefone(String telefone) {
        String sql = "SELECT * FROM cliente WHERE telefone LIKE ? ORDER BY nome";
        return executarConsultaComFiltro(sql, "%" + telefone + "%");
    }

    public List<Cliente> buscarPorPeriodoCadastro(LocalDate inicio, LocalDate fim) {
        String sql = "SELECT * FROM cliente WHERE data_cadastro BETWEEN ? AND ? ORDER BY data_cadastro";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return clientes;
    }

    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        return executarConsultaComFiltro(sql, null);
    }

    private List<Cliente> executarConsultaComFiltro(String sql, String parametro) {
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (parametro != null) {
                stmt.setString(1, parametro);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na consulta: " + e.getMessage(), e);
        }

        return clientes;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setEmail(rs.getString("email"));

        Date dataCadastro = rs.getDate("data_cadastro");
        if (dataCadastro != null) {
            cliente.setDataCadastro(dataCadastro.toLocalDate());
        }

        return cliente;
    }
}
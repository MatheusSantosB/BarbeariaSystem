package com.barbearia.model.service;

import com.barbearia.model.dao.ClienteDAO;
import com.barbearia.model.entity.Cliente;
import java.time.LocalDate;
import java.util.List;

public class ClienteService {
    private ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    public void cadastrarCliente(Cliente cliente) throws IllegalArgumentException {
        validarCliente(cliente);
        clienteDAO.salvar(cliente);
    }

    public void atualizarCliente(Cliente cliente) throws IllegalArgumentException {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente não possui ID para atualização");
        }
        validarCliente(cliente);
        clienteDAO.atualizar(cliente);
    }

    public void excluirCliente(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        clienteDAO.excluir(id);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }

        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do cliente é obrigatório");
        }

        // Validação básica de telefone (pelo menos 10 dígitos)
        String telefoneLimpo = cliente.getTelefone().replaceAll("[^0-9]", "");
        if (telefoneLimpo.length() < 10) {
            throw new IllegalArgumentException("Telefone inválido (mínimo 10 dígitos)");
        }

        // Validação de email (opcional)
        if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
            if (!cliente.getEmail().contains("@") || !cliente.getEmail().contains(".")) {
                throw new IllegalArgumentException("Email inválido");
            }
        }
    }

    // Consultas
    public Cliente buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteDAO.buscarPorNome(nome);
    }

    public List<Cliente> buscarPorTelefone(String telefone) {
        return clienteDAO.buscarPorTelefone(telefone);
    }

    public List<Cliente> buscarPorPeriodoCadastro(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Datas de início e fim são obrigatórias");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data de início não pode ser após data de fim");
        }
        return clienteDAO.buscarPorPeriodoCadastro(inicio, fim);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    // Métodos de negócio específicos
    public int contarTotalClientes() {
        return clienteDAO.listarTodos().size();
    }

    public List<Cliente> buscarClientesFieis(int limite) {
        // Lógica para buscar clientes com mais agendamentos
        // Implementação simplificada
        List<Cliente> todos = clienteDAO.listarTodos();
        return todos.size() > limite ? todos.subList(0, Math.min(limite, todos.size())) : todos;
    }

    public double calcularTaxaRetencao() {
        // Lógica para calcular taxa de retenção de clientes
        // Implementação simplificada
        List<Cliente> clientes = clienteDAO.listarTodos();
        if (clientes.isEmpty()) return 0.0;

        long clientesRecentes = clientes.stream()
                .filter(c -> c.getDataCadastro().isAfter(LocalDate.now().minusMonths(3)))
                .count();

        return (double) clientesRecentes / clientes.size() * 100;
    }
}
package com.barbearia.model.service;

import com.barbearia.model.dao.ServicoDAO;
import com.barbearia.model.entity.Servico;
import java.util.List;

public class ServicoService {
    private ServicoDAO servicoDAO;

    public ServicoService() {
        this.servicoDAO = new ServicoDAO();
    }

    public void cadastrarServico(Servico servico) throws IllegalArgumentException {
        validarServico(servico);
        servicoDAO.salvar(servico);
    }

    public void atualizarServico(Servico servico) throws IllegalArgumentException {
        if (servico.getId() == null) {
            throw new IllegalArgumentException("Serviço não possui ID para atualização");
        }
        validarServico(servico);
        servicoDAO.atualizar(servico);
    }

    public void excluirServico(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do serviço não pode ser nulo");
        }
        servicoDAO.excluir(id);
    }

    private void validarServico(Servico servico) {
        if (servico.getNome() == null || servico.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do serviço é obrigatório");
        }

        if (servico.getPreco() == null || servico.getPreco() <= 0) {
            throw new IllegalArgumentException("Preço do serviço deve ser maior que zero");
        }

        if (servico.getDuracaoMinutos() == null || servico.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException("Duração do serviço deve ser maior que zero");
        }

        // Validação de duração máxima (4 horas)
        if (servico.getDuracaoMinutos() > 240) {
            throw new IllegalArgumentException("Duração do serviço não pode exceder 4 horas");
        }
    }

    // Consultas
    public Servico buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return servicoDAO.buscarPorId(id);
    }

    public List<Servico> buscarPorNome(String nome) {
        return servicoDAO.buscarPorNome(nome);
    }

    public List<Servico> buscarPorPrecoMaximo(Double precoMaximo) {
        if (precoMaximo == null || precoMaximo <= 0) {
            throw new IllegalArgumentException("Preço máximo deve ser maior que zero");
        }
        return servicoDAO.buscarPorPrecoMaximo(precoMaximo);
    }

    public List<Servico> buscarMaisPopulares(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("Limite deve ser maior que zero");
        }
        return servicoDAO.buscarMaisPopulares(limite);
    }

    public List<Servico> listarTodos() {
        return servicoDAO.listarTodos();
    }

    // Métodos de negócio específicos
    public double calcularValorMedioServicos() {
        List<Servico> servicos = servicoDAO.listarTodos();
        if (servicos.isEmpty()) return 0.0;

        double soma = servicos.stream()
                .mapToDouble(Servico::getPreco)
                .sum();

        return soma / servicos.size();
    }

    public Servico buscarServicoMaisCaro() {
        List<Servico> servicos = servicoDAO.listarTodos();
        if (servicos.isEmpty()) return null;

        return servicos.stream()
                .max((s1, s2) -> Double.compare(s1.getPreco(), s2.getPreco()))
                .orElse(null);
    }

    public Servico buscarServicoMaisBarato() {
        List<Servico> servicos = servicoDAO.listarTodos();
        if (servicos.isEmpty()) return null;

        return servicos.stream()
                .min((s1, s2) -> Double.compare(s1.getPreco(), s2.getPreco()))
                .orElse(null);
    }

    public double calcularFaturamentoEstimado() {
        // Simulação: faturamento estimado baseado nos serviços
        List<Servico> servicos = servicoDAO.listarTodos();
        double faturamento = 0.0;

        for (Servico servico : servicos) {
            // Cada serviço é estimado em 10 vendas por mês
            faturamento += servico.getPreco() * 10;
        }

        return faturamento;
    }
}
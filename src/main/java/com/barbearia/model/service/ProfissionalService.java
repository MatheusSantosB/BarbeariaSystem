package com.barbearia.model.service;

import com.barbearia.model.dao.ProfissionalDAO;
import com.barbearia.model.entity.Profissional;
import java.util.List;

public class ProfissionalService {
    private ProfissionalDAO profissionalDAO;

    public ProfissionalService() {
        this.profissionalDAO = new ProfissionalDAO();
    }

    public void cadastrarProfissional(Profissional profissional) throws IllegalArgumentException {
        validarProfissional(profissional);
        profissionalDAO.salvar(profissional);
    }

    public void atualizarProfissional(Profissional profissional) throws IllegalArgumentException {
        if (profissional.getId() == null) {
            throw new IllegalArgumentException("Profissional não possui ID para atualização");
        }
        validarProfissional(profissional);
        profissionalDAO.atualizar(profissional);
    }

    public void excluirProfissional(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do profissional não pode ser nulo");
        }
        profissionalDAO.excluir(id);
    }

    public void desativarProfissional(Integer id) {
        Profissional profissional = buscarPorId(id);
        if (profissional != null) {
            profissional.setAtivo(false);
            profissionalDAO.atualizar(profissional);
        }
    }

    public void ativarProfissional(Integer id) {
        Profissional profissional = buscarPorId(id);
        if (profissional != null) {
            profissional.setAtivo(true);
            profissionalDAO.atualizar(profissional);
        }
    }

    private void validarProfissional(Profissional profissional) {
        if (profissional.getNome() == null || profissional.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do profissional é obrigatório");
        }

        if (profissional.getEspecialidade() == null || profissional.getEspecialidade().trim().isEmpty()) {
            throw new IllegalArgumentException("Especialidade do profissional é obrigatória");
        }

        if (profissional.getTelefone() == null || profissional.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do profissional é obrigatório");
        }
    }

    // Consultas
    public Profissional buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return profissionalDAO.buscarPorId(id);
    }

    public List<Profissional> buscarPorNome(String nome) {
        return profissionalDAO.buscarPorNome(nome);
    }

    public List<Profissional> buscarPorEspecialidade(String especialidade) {
        return profissionalDAO.buscarPorEspecialidade(especialidade);
    }

    public List<Profissional> buscarAtivos() {
        return profissionalDAO.buscarAtivos();
    }

    public List<Profissional> listarTodos() {
        return profissionalDAO.listarTodos();
    }

    // Métodos de negócio específicos
    public int contarProfissionaisAtivos() {
        return profissionalDAO.buscarAtivos().size();
    }

    public List<Profissional> buscarTopProfissionais(int limite) {
        // Lógica para buscar profissionais mais bem avaliados ou com mais agendamentos
        List<Profissional> ativos = profissionalDAO.buscarAtivos();
        return ativos.size() > limite ? ativos.subList(0, Math.min(limite, ativos.size())) : ativos;
    }

    public double calcularOcupacaoMedia() {
        // Lógica para calcular ocupação média dos profissionais
        // Implementação simplificada
        List<Profissional> ativos = profissionalDAO.buscarAtivos();
        if (ativos.isEmpty()) return 0.0;

        // Simulação: retorna valor entre 60% e 90%
        return 60.0 + (Math.random() * 30);
    }
}
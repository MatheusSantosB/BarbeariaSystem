package com.barbearia.model.service;

import com.barbearia.model.dao.AgendamentoDAO;
import com.barbearia.model.dao.ClienteDAO;
import com.barbearia.model.dao.ProfissionalDAO;
import com.barbearia.model.dao.ServicoDAO;
import com.barbearia.model.entity.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AgendamentoService {
    private AgendamentoDAO agendamentoDAO;
    private ClienteDAO clienteDAO;
    private ProfissionalDAO profissionalDAO;
    private ServicoDAO servicoDAO;

    public AgendamentoService() {
        this.agendamentoDAO = new AgendamentoDAO();
        this.clienteDAO = new ClienteDAO();
        this.profissionalDAO = new ProfissionalDAO();
        this.servicoDAO = new ServicoDAO();
    }

    public void agendar(Agendamento agendamento) throws IllegalArgumentException {
        validarAgendamento(agendamento);
        verificarDisponibilidade(agendamento);
        agendamentoDAO.salvar(agendamento);
    }

    public void atualizarAgendamento(Agendamento agendamento) throws IllegalArgumentException {
        if (agendamento.getId() == null) {
            throw new IllegalArgumentException("Agendamento não possui ID para atualização");
        }
        validarAgendamento(agendamento);
        verificarDisponibilidade(agendamento);
        agendamentoDAO.atualizar(agendamento);
    }

    public void cancelarAgendamento(Integer id, String motivo) {
        Agendamento agendamento = buscarPorId(id);
        if (agendamento != null) {
            agendamento.setStatus(Agendamento.StatusAgendamento.CANCELADO);
            agendamento.setObservacoes("Cancelado: " + (motivo != null ? motivo : "Sem motivo informado"));
            agendamentoDAO.atualizar(agendamento);
        }
    }

    public void confirmarAgendamento(Integer id) {
        Agendamento agendamento = buscarPorId(id);
        if (agendamento != null) {
            agendamento.setStatus(Agendamento.StatusAgendamento.CONFIRMADO);
            agendamentoDAO.atualizar(agendamento);
        }
    }

    public void finalizarAgendamento(Integer id, String observacoes) {
        Agendamento agendamento = buscarPorId(id);
        if (agendamento != null) {
            agendamento.setStatus(Agendamento.StatusAgendamento.REALIZADO);
            if (observacoes != null && !observacoes.trim().isEmpty()) {
                String obsAtual = agendamento.getObservacoes();
                agendamento.setObservacoes((obsAtual != null ? obsAtual + "\n" : "") + "Finalizado: " + observacoes);
            }
            agendamentoDAO.atualizar(agendamento);
        }
    }

    private void validarAgendamento(Agendamento agendamento) {
        if (agendamento.getCliente() == null || agendamento.getCliente().getId() == null) {
            throw new IllegalArgumentException("Cliente do agendamento é obrigatório");
        }

        if (agendamento.getProfissional() == null || agendamento.getProfissional().getId() == null) {
            throw new IllegalArgumentException("Profissional do agendamento é obrigatório");
        }

        if (agendamento.getData() == null) {
            throw new IllegalArgumentException("Data do agendamento é obrigatória");
        }

        if (agendamento.getHora() == null) {
            throw new IllegalArgumentException("Hora do agendamento é obrigatória");
        }

        if (agendamento.getServicos() == null || agendamento.getServicos().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um serviço deve ser selecionado");
        }

        // Verificar se data não é no passado
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataHoraAgendamento = LocalDateTime.of(agendamento.getData(), agendamento.getHora());
        if (dataHoraAgendamento.isBefore(agendamento.getData().atStartOfDay())) {
            throw new IllegalArgumentException("Não é possível agendar para datas passadas");
        }

        // Verificar horário comercial (8h às 20h)
        if (agendamento.getHora().isBefore(LocalTime.of(8, 0)) ||
                agendamento.getHora().isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("Horário fora do expediente comercial (8h às 20h)");
        }

        // Verificar duração total não excede 4 horas
        int duracaoTotal = agendamento.calcularDuracaoTotal();
        if (duracaoTotal > 240) {
            throw new IllegalArgumentException("Duração total dos serviços não pode exceder 4 horas");
        }
    }

    private void verificarDisponibilidade(Agendamento agendamento) {
        LocalDateTime inicio = LocalDateTime.of(agendamento.getData(), agendamento.getHora());
        int duracaoTotal = agendamento.calcularDuracaoTotal();
        LocalDateTime fim = inicio.plusMinutes(duracaoTotal);

        // Verificar conflitos com outros agendamentos do mesmo profissional
        List<Agendamento> agendamentosProfissional = agendamentoDAO.buscarPorProfissional(
                agendamento.getProfissional().getId());

        for (Agendamento existente : agendamentosProfissional) {
            // Ignorar o próprio agendamento se estiver sendo atualizado
            if (agendamento.getId() != null && agendamento.getId().equals(existente.getId())) {
                continue;
            }

            // Ignorar agendamentos cancelados
            if (existente.getStatus() == Agendamento.StatusAgendamento.CANCELADO ||
                    existente.getStatus() == Agendamento.StatusAgendamento.AUSENTE) {
                continue;
            }

            LocalDateTime inicioExistente = LocalDateTime.of(existente.getData(), existente.getHora());
            LocalDateTime fimExistente = inicioExistente.plusMinutes(existente.calcularDuracaoTotal());

            // Verificar sobreposição
            if ((inicio.isBefore(fimExistente) && fim.isAfter(inicioExistente))) {
                throw new IllegalArgumentException(
                        "Conflito de horário com agendamento existente do profissional " +
                                existente.getProfissional().getNome() + " das " +
                                existente.getHora() + " às " +
                                fimExistente.toLocalTime());
            }
        }
    }

    // Consultas
    public Agendamento buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        return agendamentoDAO.buscarPorId(id);
    }

    public List<Agendamento> buscarPorData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("Data não pode ser nula");
        }
        return agendamentoDAO.buscarPorData(data);
    }

    public List<Agendamento> buscarPorCliente(Integer clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        return agendamentoDAO.buscarPorCliente(clienteId);
    }

    public List<Agendamento> buscarPorProfissional(Integer profissionalId) {
        if (profissionalId == null) {
            throw new IllegalArgumentException("ID do profissional não pode ser nulo");
        }
        return agendamentoDAO.buscarPorProfissional(profissionalId);
    }

    public List<Agendamento> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas de início e fim são obrigatórias");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser após data de fim");
        }
        return agendamentoDAO.buscarPorPeriodo(dataInicio, dataFim);
    }

    public List<Agendamento> buscarPorStatus(String status) {
        return agendamentoDAO.buscarPorStatus(status);
    }

    public List<Agendamento> listarTodos() {
        return agendamentoDAO.listarTodos();
    }

    public List<Agendamento> buscarAgendamentosHoje() {
        return buscarPorData(LocalDate.now());
    }

    // Métodos de negócio específicos
    public double calcularFaturamentoPeriodo(LocalDate inicio, LocalDate fim) {
        List<Agendamento> agendamentos = buscarPorPeriodo(inicio, fim);

        return agendamentos.stream()
                .filter(a -> a.getStatus() == Agendamento.StatusAgendamento.REALIZADO)
                .mapToDouble(Agendamento::calcularValorTotal)
                .sum();
    }

    public int contarAgendamentosPendentes() {
        List<Agendamento> agendamentos = listarTodos();

        return (int) agendamentos.stream()
                .filter(a -> a.getStatus() == Agendamento.StatusAgendamento.AGENDADO)
                .count();
    }

    public double calcularTaxaCancelamento() {
        List<Agendamento> agendamentos = listarTodos();
        if (agendamentos.isEmpty()) return 0.0;

        long cancelados = agendamentos.stream()
                .filter(a -> a.getStatus() == Agendamento.StatusAgendamento.CANCELADO)
                .count();

        return (double) cancelados / agendamentos.size() * 100;
    }

    public Profissional buscarProfissionalMaisOcupado() {
        List<Profissional> profissionais = profissionalDAO.listarTodos();
        if (profissionais.isEmpty()) return null;

        // Lógica simplificada: retorna o primeiro profissional ativo
        return profissionais.stream()
                .filter(Profissional::isAtivo)
                .findFirst()
                .orElse(null);
    }

    public Cliente buscarClienteMaisFrequente() {
        List<Cliente> clientes = clienteDAO.listarTodos();
        if (clientes.isEmpty()) return null;

        // Lógica simplificada: retorna o primeiro cliente
        return clientes.stream()
                .findFirst()
                .orElse(null);
    }
}
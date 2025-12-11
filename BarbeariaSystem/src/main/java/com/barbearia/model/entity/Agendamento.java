package com.barbearia.model.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Agendamento {
    private Integer id;
    private LocalDate data;
    private LocalTime hora;
    private StatusAgendamento status;
    private String observacoes;
    private Cliente cliente;
    private Profissional profissional;
    private List<Servico> servicos;

    public enum StatusAgendamento {
        AGENDADO, CONFIRMADO, CANCELADO, REALIZADO, AUSENTE
    }

    public Agendamento() {
        this.servicos = new ArrayList<>();
        this.status = StatusAgendamento.AGENDADO;
    }

    public Agendamento(LocalDate data, LocalTime hora, Cliente cliente, Profissional profissional) {
        this();
        this.data = data;
        this.hora = hora;
        this.cliente = cliente;
        this.profissional = profissional;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }

    public void addServico(Servico servico) {
        this.servicos.add(servico);
    }

    public Double calcularValorTotal() {
        return servicos.stream()
                .mapToDouble(Servico::getPreco)
                .sum();
    }

    public Integer calcularDuracaoTotal() {
        return servicos.stream()
                .mapToInt(Servico::getDuracaoMinutos)
                .sum();
    }

    @Override
    public String toString() {
        return data + " " + hora + " - " + cliente.getNome() + " com " + profissional.getNome();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agendamento that = (Agendamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
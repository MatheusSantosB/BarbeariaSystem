package com.barbearia.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente extends Pessoa {
    private LocalDate dataCadastro;
    private List<Agendamento> agendamentos;

    public Cliente() {
        super();
        this.agendamentos = new ArrayList<>();
        this.dataCadastro = LocalDate.now();
    }

    public Cliente(String nome, String telefone, String email) {
        super(nome, telefone, email);
        this.agendamentos = new ArrayList<>();
        this.dataCadastro = LocalDate.now();
    }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public List<Agendamento> getAgendamentos() { return agendamentos; }
    public void setAgendamentos(List<Agendamento> agendamentos) { this.agendamentos = agendamentos; }

    public void addAgendamento(Agendamento agendamento) {
        this.agendamentos.add(agendamento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nome + " - " + telefone; }
}
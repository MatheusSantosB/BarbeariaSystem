package com.barbearia.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Pagamento {
    private Integer id;
    private Double valor;
    private FormaPagamento formaPagamento;
    private StatusPagamento status;
    private LocalDateTime dataPagamento;
    private Agendamento agendamento;

    public enum FormaPagamento {
        DINHEIRO, CARTAO_DEBITO, CARTAO_CREDITO, PIX, TRANSFERENCIA
    }

    public enum StatusPagamento {
        PENDENTE, PAGO, CANCELADO, ESTORNADO
    }

    public Pagamento() {
        this.status = StatusPagamento.PENDENTE;
        this.dataPagamento = LocalDateTime.now();
    }

    public Pagamento(Double valor, FormaPagamento formaPagamento, Agendamento agendamento) {
        this();
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.agendamento = agendamento;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public Agendamento getAgendamento() { return agendamento; }
    public void setAgendamento(Agendamento agendamento) { this.agendamento = agendamento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(id, pagamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
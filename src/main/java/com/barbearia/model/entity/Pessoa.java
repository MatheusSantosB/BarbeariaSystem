package com.barbearia.model.entity;

public class Profissional extends Pessoa {
    private String especialidade;
    private boolean ativo;

    public Profissional() {
        super();
        this.ativo = true;
    }

    public Profissional(String nome, String especialidade, String telefone, String email) {
        super(nome, telefone, email);
        this.especialidade = especialidade;
        this.ativo = true;
    }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
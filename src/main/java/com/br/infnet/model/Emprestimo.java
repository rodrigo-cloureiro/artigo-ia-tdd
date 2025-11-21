package com.br.infnet.model;

import java.time.LocalDate;

public class Emprestimo {
    private final int id;
    private final int livroId;
    private LocalDate dataEmprestimo;
    private LocalDate dataEstimadaDevolucao;
    private LocalDate dataEfetivaDevolucao;
    private int prazoDevolucao;
    private double multa;

    public Emprestimo (int id, int livroId, LocalDate dataEmprestimo, LocalDate dataEstimadaDevolucao, int prazoDevolucao, double multa) {
        this.id = id;
        this.livroId = livroId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataEstimadaDevolucao = dataEstimadaDevolucao;
        this.dataEfetivaDevolucao = null;
        this.prazoDevolucao = prazoDevolucao;
        this.multa = multa;
    }

    public int getId() {
        return id;
    }

    public int getLivroId() {
        return livroId;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataEstimadaDevolucao() {
        return dataEstimadaDevolucao;
    }

    public void setDataEstimadaDevolucao(LocalDate dataDevolucao) {
        this.dataEstimadaDevolucao = dataDevolucao;
    }

    public LocalDate getDataEfetivaDevolucao() {
        return dataEfetivaDevolucao;
    }

    public void setDataEfetivaDevolucao(LocalDate dataEfetivaDevolucao) {
        this.dataEfetivaDevolucao = dataEfetivaDevolucao;
    }

    public int getPrazoDevolucao() {
        return prazoDevolucao;
    }

    public void setPrazoDevolucao(int prazoDevolucao) {
        if (prazoDevolucao < 0) {
            throw new IllegalArgumentException("Prazo de devolução não pode ser negativo");
        }
        this.prazoDevolucao = prazoDevolucao;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        if (multa < 0) {
            throw new IllegalArgumentException("Multa não pode ser negativa");
        }
        this.multa = multa;
    }
}
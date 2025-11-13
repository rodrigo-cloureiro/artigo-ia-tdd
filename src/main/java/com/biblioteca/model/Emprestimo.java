package com.biblioteca.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Emprestimo {
    private Long id;
    private Livro livro;
    private String usuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;
    private boolean devolvido;
    private boolean multaPaga;
    private static long contadorId = 1;

    public Emprestimo() {
        this.id = contadorId++;
        this.dataEmprestimo = LocalDate.now();
        this.devolvido = false;
        this.multaPaga = false;
    }

    public Emprestimo(Livro livro, String usuario, int prazoDias) {
        this();
        setLivro(livro);
        setUsuario(usuario);
        setPrazoDias(prazoDias);
    }

    // Getters
    public Long getId() { return id; }
    public Livro getLivro() { return livro; }
    public String getUsuario() { return usuario; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public LocalDate getDataDevolucaoReal() { return dataDevolucaoReal; }
    public boolean isDevolvido() { return devolvido; }
    public boolean isMultaPaga() { return multaPaga; }

    // Setters
    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID não pode ser nulo ou menor que 1");
        }
        this.id = id;
    }

    public void setLivro(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo");
        }
        this.livro = livro;
    }

    public void setUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuário não pode ser nulo ou vazio");
        }
        this.usuario = usuario.trim();
    }

    public void setPrazoDias(int prazoDias) {
        if (prazoDias <= 0) {
            throw new IllegalArgumentException("Prazo deve ser maior que zero");
        }
        this.dataDevolucaoPrevista = dataEmprestimo.plusDays(prazoDias);
    }

    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public void setDevolvido(boolean devolvido) {
        this.devolvido = devolvido;
    }

    public void setMultaPaga(boolean multaPaga) {
        this.multaPaga = multaPaga;
    }

    // Métodos de negócio
    public boolean estaAtrasado() {
        if (devolvido) {
            return dataDevolucaoReal != null &&
                    dataDevolucaoReal.isAfter(dataDevolucaoPrevista);
        }
        return LocalDate.now().isAfter(dataDevolucaoPrevista);
    }

    public int getDiasAtraso() {
        if (!estaAtrasado()) {
            return 0;
        }

        LocalDate dataReferencia = devolvido ? dataDevolucaoReal : LocalDate.now();
        long diasAtraso = ChronoUnit.DAYS.between(dataDevolucaoPrevista, dataReferencia);
        return Math.max(0, (int) diasAtraso);
    }

    public double calcularMulta() {
        int diasAtraso = getDiasAtraso();
        if (diasAtraso <= 0) {
            return 0.0;
        }

        // R$5,00 fixos + R$0,50 por dia a partir do 11º dia
        double multa = 5.0; // Valor fixo
        if (diasAtraso > 10) {
            multa += (diasAtraso - 10) * 0.5;
        }
        return multa;
    }

    public boolean podeDevolver() {
        if (devolvido) {
            return false; // Já foi devolvido
        }

        double multa = calcularMulta();
        return multa == 0 || multaPaga;
    }

    public void registrarDevolucao() {
        if (!podeDevolver()) {
            throw new IllegalStateException("Não é possível devolver o livro. Multa pendente: R$ " + calcularMulta());
        }

        this.dataDevolucaoReal = LocalDate.now();
        this.devolvido = true;
    }

    public void pagarMulta() {
        if (!estaAtrasado()) {
            throw new IllegalStateException("Não há multa para pagar");
        }
        this.multaPaga = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emprestimo that = (Emprestimo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Emprestimo{id=%d, livro=%s, usuario='%s', dataEmprestimo=%s, dataDevolucaoPrevista=%s, devolvido=%s}",
                id, livro.getTitulo(), usuario, dataEmprestimo, dataDevolucaoPrevista, devolvido);
    }
}
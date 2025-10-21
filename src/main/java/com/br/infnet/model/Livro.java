package com.br.infnet.model;

import java.time.LocalDate;

public class Livro {
    private final int id;
    private String titulo;
    private String autor;
    private String isbn;
    private LocalDate dataEmprestimo;
    private LocalDate dataEstimadaDevolucao;
    private LocalDate dataEfetivaDevolucao;
    private int prazoDevolucao;
    private boolean disponivel;
    double multa;

    public Livro(int id, String titulo, String autor, String isbn) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título inválido");
        }

        if (titulo.length() < 3) {
            throw new IllegalArgumentException("Título deve ter pelo menos 3 caracteres");
        }

        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("Autor inválido");
        }

        if (autor.length() < 3) {
            throw new IllegalArgumentException("Autor deve ter pelo menos 3 caracteres");
        }

        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN não pode ser nulo ou vazio");
        }

        if (isbn.length() != 13) {
            throw new IllegalArgumentException("ISBN tem exatos 13 caracteres");
        }

        if(!isbn.matches("\\d+")) {
            throw new IllegalArgumentException("ISBN deve conter apenas números");
        }

        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.dataEmprestimo = null;
        this.prazoDevolucao = 0;
        this.dataEstimadaDevolucao = null;
        this.disponivel = true;
        this.multa = 0;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if(titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título inválido");
        }
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if(autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("Autor inválido");
        }
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN não pode ser nulo ou vazio");
        }
        if (isbn.length() < 13) {
            throw new IllegalArgumentException("ISBN deve ter pelo menos 13 caracteres");
        }
        if(!isbn.matches("\\d+")) {
            throw new IllegalArgumentException("ISBN deve conter apenas números");
        }
        this.isbn = isbn;
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

    public boolean isDisponivel() {
        return this.disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
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



    @Override
    public String toString() {
        return "ID = " + id +
                ", Título: '" + titulo + '\'' +
                ", Autor: '" + autor + '\'' +
                ", ISBN: '" + isbn + '\'' +
                ", Disponível? " + disponivel;
    }


}
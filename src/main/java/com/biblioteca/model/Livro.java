package com.biblioteca.model;

import java.util.Objects;
import java.util.regex.Pattern;

public class Livro {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;

    private static final Pattern ISBN_PATTERN = Pattern.compile("^\\d{13}$");
    private static long contadorId = 1;

    public Livro() {
        this.id = contadorId++;
    }

    public Livro(String titulo, String autor, String isbn) {
        this();
        setTitulo(titulo);
        setAutor(autor);
        setIsbn(isbn);
    }

    // Getters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }

    // Setters com validação
    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID não pode ser nulo ou menor que 1");
        }
        this.id = id;
    }

    public void setTitulo(String titulo) {
        validarCampoNaoNulo(titulo, "título");
        this.titulo = titulo.trim();
    }

    public void setAutor(String autor) {
        validarCampoNaoNulo(autor, "autor");
        this.autor = autor.trim();
    }

    public void setIsbn(String isbn) {
        validarCampoNaoNulo(isbn, "ISBN");
        String isbnLimpo = isbn.trim();

        if (!ISBN_PATTERN.matcher(isbnLimpo).matches()) {
            throw new IllegalArgumentException("ISBN deve conter exatamente 13 dígitos numéricos");
        }

        this.isbn = isbnLimpo;
    }

    private void validarCampoNaoNulo(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nomeCampo + " não pode ser nulo ou vazio");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return Objects.equals(isbn, livro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Livro{id=%d, titulo='%s', autor='%s', isbn='%s'}",
                id, titulo, autor, isbn);
    }
}
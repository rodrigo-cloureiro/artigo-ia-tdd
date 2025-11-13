package com.biblioteca.model;

import com.biblioteca.security.SecurityUtils;
import com.biblioteca.validation.ValidationResult;
import com.biblioteca.validation.Validator;
import java.util.Objects;

public class Livro {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;

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

    // Setters com validação e sanitização
    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID não pode ser nulo ou menor que 1");
        }
        this.id = id;
    }

    public void setTitulo(String titulo) {
        validarCampoNaoNulo(titulo, "título");
        String tituloSanitizado = SecurityUtils.sanitizeInput(titulo);

        if (!SecurityUtils.isValidTitle(tituloSanitizado)) {
            throw new IllegalArgumentException("Título inválido. Use apenas letras, números e espaços (máx. 255 caracteres)");
        }

        this.titulo = tituloSanitizado;
    }

    public void setAutor(String autor) {
        validarCampoNaoNulo(autor, "autor");
        String autorSanitizado = SecurityUtils.sanitizeInput(autor);

        if (!SecurityUtils.isValidAuthor(autorSanitizado)) {
            throw new IllegalArgumentException("Autor inválido. Use apenas letras e espaços (máx. 100 caracteres)");
        }

        this.autor = autorSanitizado;
    }

    public void setIsbn(String isbn) {
        validarCampoNaoNulo(isbn, "ISBN");
        String isbnSanitizado = SecurityUtils.sanitizeInput(isbn);

        if (!SecurityUtils.isValidISBN(isbnSanitizado)) {
            throw new IllegalArgumentException("ISBN inválido. Deve ser um código ISBN-13 válido com 13 dígitos");
        }

        this.isbn = isbnSanitizado;
    }

    private void validarCampoNaoNulo(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nomeCampo + " não pode ser nulo ou vazio");
        }
    }

    // Validação completa do objeto
    public ValidationResult validate() {
        return Validator.validateLivro(this);
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
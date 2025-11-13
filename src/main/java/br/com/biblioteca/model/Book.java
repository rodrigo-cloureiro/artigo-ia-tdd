package br.com.biblioteca.model;

/**
 * Representa um Livro no sistema.
 * Utiliza um 'record' (Java 16+) para imutabilidade e concisão.
 * * @param id     Identificador único do livro (gerado pelo DAO). Pode ser nulo se ainda não foi persistido.
 * @param title  Título do livro.
 * @param author Autor do livro.
 * @param isbn   ISBN de 13 dígitos (único).
 */
public record Book(
        Long id,
        String title,
        String author,
        String isbn
) {
    /**
     * Construtor auxiliar para criar um livro novo (sem ID).
     * O ID será definido pela camada de persistência.
     */
    public Book(String title, String author, String isbn) {
        this(null, title, author, isbn);
    }
}
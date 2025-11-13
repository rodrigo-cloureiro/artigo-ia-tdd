package br.com.biblioteca.model;

import java.time.LocalDate;

/**
 * Representa um Empréstimo de um livro.
 *
 * @param id           O ID único do empréstimo.
 * @param bookId       A chave estrangeira para o Livro (Book).
 * @param borrowerName O nome da pessoa que pegou o livro.
 * @param loanDate     A data em que o empréstimo foi feito.
 * @param dueDate      A data prevista para devolução (calculada com base no prazo inserido).
 * @param returnDate   A data em que o livro foi de fato devolvido (null se ainda emprestado).
 */
public record Loan(
        Long id,
        Long bookId,
        String borrowerName,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate
) {
    /**
     * Construtor auxiliar para criar um novo empréstimo (sem ID e sem data de devolução).
     */
    public Loan(Long bookId, String borrowerName, LocalDate loanDate, LocalDate dueDate) {
        this(null, bookId, borrowerName, loanDate, dueDate, null);
    }
}
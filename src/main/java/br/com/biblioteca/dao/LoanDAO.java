package br.com.biblioteca.dao;

import br.com.biblioteca.model.Loan;

import java.util.List;
import java.util.Optional;

/**
 * Interface de Contrato de Acesso a Dados (DAO) para a entidade Loan (Empréstimo).
 */
public interface LoanDAO {

    /**
     * Salva ou atualiza um empréstimo.
     */
    Loan save(Loan loan);

    /**
     * Busca um empréstimo pelo seu ID.
     */
    Optional<Loan> findById(Long id);

    /**
     * Busca o empréstimo *ativo* (não devolvido) para um livro específico.
     */
    Optional<Loan> findActiveLoanByBookId(Long bookId);

    /**
     * Retorna todos os empréstimos que ainda não foram devolvidos.
     */
    List<Loan> findAllActiveLoans();
}
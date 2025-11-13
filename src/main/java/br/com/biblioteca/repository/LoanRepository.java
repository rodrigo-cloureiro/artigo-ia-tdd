package br.com.biblioteca.repository;

import br.com.biblioteca.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(Long id);
    List<Loan> findAll();
    List<Loan> findActiveLoans();
    Optional<Loan> findActiveByBookId(Long bookId);
    void deleteById(Long id);
}

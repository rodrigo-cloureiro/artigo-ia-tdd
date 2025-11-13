package br.com.biblioteca.dao;

import br.com.biblioteca.model.Loan;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementação do LoanDAO usando memória (ConcurrentHashMap) para persistência simples.
 */
public class InMemoryLoanDAO implements LoanDAO {

    private final Map<Long, Loan> loans = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Loan save(Loan loan) {
        Long id = loan.id();
        if (id == null) {
            // Criar novo empréstimo
            long newId = idGenerator.getAndIncrement();
            Loan newLoan = new Loan(
                    newId,
                    loan.bookId(),
                    loan.borrowerName(),
                    loan.loanDate(),
                    loan.dueDate(),
                    loan.returnDate()
            );
            loans.put(newId, newLoan);
            return newLoan;
        } else {
            // Atualizar empréstimo existente
            if (!loans.containsKey(id)) {
                throw new IllegalArgumentException("Empréstimo inexistente com ID: " + id);
            }
            loans.put(id, loan);
            return loan;
        }
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return Optional.ofNullable(loans.get(id));
    }

    @Override
    public Optional<Loan> findActiveLoanByBookId(Long bookId) {
        return loans.values().stream()
                // Empréstimo para o livro e ainda não devolvido (returnDate é null)
                .filter(loan -> loan.bookId().equals(bookId) && loan.returnDate() == null)
                .findFirst();
    }

    @Override
    public List<Loan> findAllActiveLoans() {
        return loans.values().stream()
                .filter(loan -> loan.returnDate() == null)
                .sorted((l1, l2) -> l2.loanDate().compareTo(l1.loanDate())) // Mais recentes primeiro
                .collect(Collectors.toList());
    }
}
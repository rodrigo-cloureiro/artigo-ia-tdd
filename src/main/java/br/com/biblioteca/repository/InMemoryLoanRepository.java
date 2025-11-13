package br.com.biblioteca.repository;

import br.com.biblioteca.model.Loan;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryLoanRepository {

    private final Map<Long, Loan> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public synchronized Loan save(Loan loan) {
        if (loan.getId() == null) {
            loan.setId(sequence.getAndIncrement());
        }
        storage.put(loan.getId(), loan);
        return loan;
    }

    public Optional<Loan> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Loan> findAll() {
        return new ArrayList<>(storage.values())
                .stream()
                .sorted(Comparator.comparing(Loan::getLoanDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Loan> findActiveLoans() {
        return storage.values().stream()
                .filter(l -> l.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    public Optional<Loan> findActiveByBookId(Long bookId) {
        return storage.values().stream()
                .filter(l -> l.getBookId().equals(bookId) && l.getReturnDate() == null)
                .findFirst();
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }
}

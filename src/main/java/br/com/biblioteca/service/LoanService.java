package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.model.Loan;
import br.com.biblioteca.repository.JdbcLoanRepository;
import br.com.biblioteca.repository.LoanRepository;
import br.com.biblioteca.service.exceptions.ValidationException;
import br.com.biblioteca.util.InputSanitizer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private static final int FREE_DAYS = 10;
    private static final BigDecimal BASE_FINE = BigDecimal.valueOf(5.00);
    private static final BigDecimal DAILY_FINE = BigDecimal.valueOf(0.50);

    public LoanService(BookService bookService) {
        this(new JdbcLoanRepository(), bookService);
    }
    public LoanService(LoanRepository repo, BookService bookService) {
        this.loanRepository = repo;
        this.bookService = bookService;
    }

    public Loan createLoan(Long bookId, String borrower, int prazoDias) {
        if (bookId == null) throw new ValidationException("ID do livro é obrigatório");
        borrower = InputSanitizer.sanitize(borrower);
        if (borrower == null || borrower.isBlank()) throw new ValidationException("Nome do tomador é obrigatório");
        if (prazoDias < 1) throw new ValidationException("Prazo deve ser ao menos 1 dia");
        Book book = bookService.findById(bookId);
        Optional<Loan> active = loanRepository.findActiveByBookId(bookId);
        if (active.isPresent()) throw new ValidationException("Livro já está emprestado");
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(prazoDias);
        Loan loan = new Loan(null, bookId, borrower, loanDate, dueDate);
        return loanRepository.save(loan);
    }

    public List<Loan> listAll() { return loanRepository.findAll(); }
    public List<Loan> listActive() { return loanRepository.findActiveLoans(); }
    public Optional<Loan> findActiveByBookId(Long bookId) { return loanRepository.findActiveByBookId(bookId); }
    public Loan findById(Long id) { return loanRepository.findById(id).orElseThrow(() -> new ValidationException("Empréstimo não encontrado")); }

    public BigDecimal calculateFine(Loan loan, LocalDate returnDate) {
        if (loan == null) throw new IllegalArgumentException("Loan is null");
        LocalDate threshold = loan.getLoanDate().plusDays(FREE_DAYS);
        if (!returnDate.isAfter(threshold)) return BigDecimal.ZERO;
        long daysLate = ChronoUnit.DAYS.between(threshold, returnDate);
        return BASE_FINE.add(DAILY_FINE.multiply(BigDecimal.valueOf(daysLate)));
    }

    public BigDecimal attemptReturn(Long loanId, LocalDate returnDate) {
        Loan loan = findById(loanId);
        if (loan.isReturned()) throw new ValidationException("Empréstimo já devolvido");
        BigDecimal fine = calculateFine(loan, returnDate);
        if (fine.compareTo(BigDecimal.ZERO) > 0 && !loan.isFinePaid()) {
            return fine;
        }
        loan.setReturnDate(returnDate);
        loanRepository.save(loan);
        return BigDecimal.ZERO;
    }

    public void payFine(Long loanId) {
        Loan loan = findById(loanId);
        loan.setFinePaid(true);
        loanRepository.save(loan);
    }

    public void deleteLoan(Long id) { loanRepository.deleteById(id); }

    public boolean isBookLoaned(Long bookId) { return loanRepository.findActiveByBookId(bookId).isPresent(); }
}

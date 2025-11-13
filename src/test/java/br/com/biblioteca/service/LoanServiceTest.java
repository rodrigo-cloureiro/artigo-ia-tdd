package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.model.Loan;
import br.com.biblioteca.repository.InMemoryBookRepository;
import br.com.biblioteca.repository.InMemoryLoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class LoanServiceTest {

    private LoanService loanService;
    private BookService bookService;
    private InMemoryBookRepository bookRepo;

    @BeforeEach
    void setup() {
        bookRepo = new InMemoryBookRepository();
        bookService = new BookService(bookRepo);
        InMemoryLoanRepository loanRepo = new InMemoryLoanRepository();
        loanService = new LoanService(loanRepo, bookService);
    }

    @Test
    void fineShouldBeZeroIfReturnedWithin10Days() {
        Book b = new Book(null, "T", "A", "1234567890123");
        bookRepo.save(b);
        Loan loan = loanService.createLoan(b.getId(), "João", 5);
        LocalDate returnDate = loan.getLoanDate().plusDays(10); // exactly 10 days -> no fine
        BigDecimal fine = loanService.calculateFine(loan, returnDate);
        assertThat(fine).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void fineShouldApplyFrom11thDay() {
        Book b = new Book(null, "T2", "A2", "1234567890124");
        bookRepo.save(b);
        Loan loan = loanService.createLoan(b.getId(), "Maria", 15);
        // return at loanDate + 12 (2 days after day 10)
        LocalDate returnDate = loan.getLoanDate().plusDays(12);
        BigDecimal fine = loanService.calculateFine(loan, returnDate);
        // daysLate = 2 -> fine = 5 + 0.5*2 = 6.0
        assertThat(fine).isEqualByComparingTo(BigDecimal.valueOf(6.0));
    }
}

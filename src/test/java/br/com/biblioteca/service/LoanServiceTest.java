package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.model.Loan;
import br.com.biblioteca.repository.JdbcBookRepository;
import br.com.biblioteca.repository.JdbcLoanRepository;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class LoanServiceTest {

    private BookService bookService;
    private LoanService loanService;

    @BeforeEach
    void setup() {
        bookService = new BookService(new JdbcBookRepository());
        loanService = new LoanService(new JdbcLoanRepository(), bookService);
    }

    @Test
    void fineZeroWithin10Days() {
        Book b = bookService.create(new Book("T", "A", "1000000000001"));
        Loan loan = loanService.createLoan(b.getId(), "João", 5);
        BigDecimal fine = loanService.calculateFine(loan, loan.getLoanDate().plusDays(10));
        assertThat(fine).isZero();
    }

    @Test
    void fineAppliesFrom11thDay() {
        Book b = bookService.create(new Book("T2","A2","1000000000002"));
        Loan loan = loanService.createLoan(b.getId(), "Ana", 20);
        BigDecimal fine = loanService.calculateFine(loan, loan.getLoanDate().plusDays(12));
        assertThat(fine).isEqualByComparingTo(BigDecimal.valueOf(6.0));
    }

    @Test
    void cannotLoanAlreadyLoanedBook() {
        Book b = bookService.create(new Book("T3","A3","1000000000003"));
        loanService.createLoan(b.getId(), "X", 10);
        assertThatThrownBy(() -> loanService.createLoan(b.getId(), "Y", 5)).hasMessageContaining("já está emprestado");
    }
}

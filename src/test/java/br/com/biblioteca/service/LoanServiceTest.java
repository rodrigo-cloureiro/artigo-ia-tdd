package br.com.biblioteca.service;

import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.dao.LoanDAO;
import br.com.biblioteca.model.Book;
import br.com.biblioteca.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Lógica de Empréstimos e Multas")
public class LoanServiceTest {

    @Mock
    private LoanDAO loanDAO;

    @Mock
    private BookDAO bookDAO;

    @InjectMocks
    private LoanService loanService;

    private final Long BOOK_ID = 1L;
    private final Book TEST_BOOK = new Book(BOOK_ID, "Duna", "Frank Herbert", "12345");
    private final Long LOAN_ID = 10L;

    @BeforeEach
    void setUp() {
        // Inicializa mocks antes de cada teste
    }

    // --- Testes de Empréstimo ---

    @Test
    @DisplayName("Deve registrar um novo empréstimo com sucesso")
    void shouldLendBookSuccessfully() {
        // Configuração: Livro existe e não está emprestado
        when(bookDAO.findById(BOOK_ID)).thenReturn(Optional.of(TEST_BOOK));
        when(loanDAO.findActiveLoanByBookId(BOOK_ID)).thenReturn(Optional.empty());

        // Simula o salvamento e retorno de um Loan com ID
        Loan loanToSave = new Loan(BOOK_ID, "João Silva", LocalDate.now(), LocalDate.now().plusDays(5));
        when(loanDAO.save(any(Loan.class))).thenAnswer(invocation -> {
            // Retorna o Loan com um ID simulado
            Loan arg = invocation.getArgument(0);
            return new Loan(LOAN_ID, arg.bookId(), arg.borrowerName(), arg.loanDate(), arg.dueDate(), arg.returnDate());
        });

        assertDoesNotThrow(() -> loanService.lendBook(BOOK_ID, "João Silva", 5));
        verify(loanDAO, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Não deve emprestar livro inexistente")
    void shouldNotLendNonExistentBook() {
        when(bookDAO.findById(BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                loanService.lendBook(BOOK_ID, "João Silva", 5));
        verify(loanDAO, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Não deve emprestar livro que já está emprestado")
    void shouldNotLendBookAlreadyOnLoan() {
        when(bookDAO.findById(BOOK_ID)).thenReturn(Optional.of(TEST_BOOK));
        when(loanDAO.findActiveLoanByBookId(BOOK_ID)).thenReturn(Optional.of(
                new Loan(LOAN_ID, BOOK_ID, "Outro Leitor", LocalDate.now(), LocalDate.now().plusDays(10), null)
        ));

        assertThrows(IllegalStateException.class, () ->
                loanService.lendBook(BOOK_ID, "João Silva", 5));
        verify(loanDAO, never()).save(any(Loan.class));
    }

    // --- Testes de Multa ---

    @Test
    @DisplayName("Deve calcular multa zero se devolvido no 10º dia")
    void shouldCalculateZeroFineForOnTimeReturn() {
        LocalDate loanDate = LocalDate.now().minusDays(9); // Emprestado há 9 dias (hoje é o 10º)
        Loan loan = new Loan(LOAN_ID, BOOK_ID, "Leitor", loanDate, loanDate.plusDays(10), null);

        LoanService.FineCalculation fine = loanService.calculateFine(loan);

        assertEquals(0, fine.daysOverdue());
        assertEquals(0.0, fine.fineAmount());
    }

    @Test
    @DisplayName("Deve calcular multa de R$5,50 se devolvido no 11º dia")
    void shouldCalculateFineForOneDayDelay() {
        LocalDate loanDate = LocalDate.now().minusDays(10); // Emprestado há 10 dias (hoje é o 11º)
        Loan loan = new Loan(LOAN_ID, BOOK_ID, "Leitor", loanDate, loanDate.plusDays(10), null);

        LoanService.FineCalculation fine = loanService.calculateFine(loan);

        assertEquals(1, fine.daysOverdue()); // 11º dia - 10 dias gratuitos = 1 dia de atraso
        assertEquals(5.50, fine.fineAmount(), 0.01); // 5.00 + (1 * 0.50)
    }

    @Test
    @DisplayName("Deve calcular multa de R$6,00 se devolvido no 12º dia")
    void shouldCalculateFineForTwoDaysDelay() {
        LocalDate loanDate = LocalDate.now().minusDays(11); // Emprestado há 11 dias (hoje é o 12º)
        Loan loan = new Loan(LOAN_ID, BOOK_ID, "Leitor", loanDate, loanDate.plusDays(10), null);

        LoanService.FineCalculation fine = loanService.calculateFine(loan);

        assertEquals(2, fine.daysOverdue()); // 12º dia - 10 dias gratuitos = 2 dias de atraso
        assertEquals(6.00, fine.fineAmount(), 0.01); // 5.00 + (2 * 0.50)
    }

    // --- Testes de Devolução ---

    @Test
    @DisplayName("Deve processar a devolução com sucesso")
    void shouldReturnBookSuccessfully() {
        LocalDate loanDate = LocalDate.now().minusDays(5);
        Loan activeLoan = new Loan(LOAN_ID, BOOK_ID, "Leitor", loanDate, loanDate.plusDays(10), null);

        when(loanDAO.findById(LOAN_ID)).thenReturn(Optional.of(activeLoan));
        when(loanDAO.save(any(Loan.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> loanService.returnBook(LOAN_ID));

        verify(loanDAO, times(1)).save(argThat(loan ->
                loan.id().equals(LOAN_ID) && loan.returnDate() != null
        ));
    }
}
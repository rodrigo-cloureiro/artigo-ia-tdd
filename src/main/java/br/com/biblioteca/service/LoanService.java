package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.model.Loan;
import br.com.biblioteca.repository.InMemoryLoanRepository;
import br.com.biblioteca.service.exceptions.BookNotFoundException;
import br.com.biblioteca.service.exceptions.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class LoanService {

    private final InMemoryLoanRepository loanRepository;
    private final BookService bookService;

    // multa constants
    private static final int FREE_DAYS = 10;
    private static final BigDecimal BASE_FINE = BigDecimal.valueOf(5.00);
    private static final BigDecimal DAILY_FINE = BigDecimal.valueOf(0.50);

    public LoanService(InMemoryLoanRepository loanRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    public Loan createLoan(Long bookId, String borrower, int prazoDias) {
        if (bookId == null) throw new ValidationException("ID do livro é obrigatório");
        if (borrower == null || borrower.isBlank()) throw new ValidationException("Nome do tomador é obrigatório");
        if (prazoDias < 1) throw new ValidationException("Prazo deve ser ao menos 1 dia");

        // check book exists
        Book book = bookService.findById(bookId);

        // check if book already emprestado
        Optional<Loan> active = loanRepository.findActiveByBookId(bookId);
        if (active.isPresent()) {
            throw new ValidationException("Livro já está emprestado");
        }

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(prazoDias);

        Loan loan = new Loan(null, bookId, borrower, loanDate, dueDate);
        return loanRepository.save(loan);
    }

    public List<Loan> listAll() {
        return loanRepository.findAll();
    }

    public List<Loan> listActive() {
        return loanRepository.findActiveLoans();
    }

    public Optional<Loan> findActiveByBookId(Long bookId) {
        return loanRepository.findActiveByBookId(bookId);
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new ValidationException("Empréstimo não encontrado"));
    }

    /**
     * Calcula a multa (se houver) no momento da tentativa de devolução.
     * Regra: gratuito até 10 dias contados a partir da data do empréstimo (loanDate).
     * Caso devolvido após dia 10 (i.e. a partir do 11º dia), multa = R$5,00 + R$0,50 por dia extra (a partir do 11º).
     * O número de dias atrasados = dias entre (loanDate.plusDays(FREE_DAYS)) e returnDate (inclusive/exclusive? usamos difference em dias naturais).
     */
    public BigDecimal calculateFine(Loan loan, LocalDate returnDate) {
        if (loan == null) throw new IllegalArgumentException("Loan is null");
        LocalDate threshold = loan.getLoanDate().plusDays(FREE_DAYS);
        if (!returnDate.isAfter(threshold)) {
            return BigDecimal.ZERO;
        }
        long daysLate = ChronoUnit.DAYS.between(threshold, returnDate);
        // daysLate >= 1
        BigDecimal fine = BASE_FINE.add(DAILY_FINE.multiply(BigDecimal.valueOf(daysLate)));
        return fine;
    }

    /**
     * Tenta devolver o livro. Se houver multa, devolução não é efetuada e o valor é retornado (não-pago).
     * Se não houver multa, marca devolvido.
     */
    public BigDecimal attemptReturn(Long loanId, LocalDate returnDate) {
        Loan loan = findById(loanId);
        if (loan.isReturned()) throw new ValidationException("Empréstimo já devolvido");
        BigDecimal fine = calculateFine(loan, returnDate);
        if (fine.compareTo(BigDecimal.ZERO) > 0 && !loan.isFinePaid()) {
            // multa pendente — devolução não processada até pagamento
            return fine;
        }
        // no fine or fine already paid
        loan.setReturnDate(returnDate);
        loanRepository.save(loan);
        return BigDecimal.ZERO;
    }

    /**
     * Marca multa como paga e se o livro ainda não foi devolvido, também pode registrar a devolução opcionalmente.
     */
    public void payFine(Long loanId) {
        Loan loan = findById(loanId);
        loan.setFinePaid(true);
        loanRepository.save(loan);
    }

    /**
     * Remove empréstimo (apenas para admin/histórico). Não é usado para devolver — prefer use attemptReturn.
     */
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    /**
     * Verifica se o livro está emprestado (ativo).
     */
    public boolean isBookLoaned(Long bookId) {
        return loanRepository.findActiveByBookId(bookId).isPresent();
    }

}


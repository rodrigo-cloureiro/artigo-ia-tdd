package br.com.biblioteca.service;

import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.dao.LoanDAO;
import br.com.biblioteca.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Camada de Serviço para a lógica de Empréstimos.
 * Implementa as regras de empréstimo, devolução e cálculo de multa.
 */
public class LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);

    private final LoanDAO loanDAO;
    private final BookDAO bookDAO;

    // Constantes das regras de negócio
    private static final int FREE_LOAN_DAYS = 10;
    private static final double FINE_BASE_AMOUNT = 5.00; // R$5,00 fixo
    private static final double FINE_PER_DAY_AMOUNT = 0.50; // R$0,50 por dia

    public LoanService(LoanDAO loanDAO, BookDAO bookDAO) {
        this.loanDAO = loanDAO;
        this.bookDAO = bookDAO;
    }

    /**
     * Verifica se um livro está atualmente emprestado.
     */
    public boolean isBookOnLoan(Long bookId) {
        return loanDAO.findActiveLoanByBookId(bookId).isPresent();
    }

    /**
     * Registra um novo empréstimo.
     * @throws IllegalStateException Se o livro não existir ou já estiver emprestado.
     */
    public Loan lendBook(Long bookId, String borrowerName, int daysToLend) {
        // 1. Validar e verificar existência
        if (bookId == null || borrowerName == null || borrowerName.isBlank() || daysToLend <= 0) {
            throw new IllegalArgumentException("Dados do empréstimo inválidos. Verifique o nome e o prazo.");
        }
        bookDAO.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Livro com ID " + bookId + " não encontrado."));

        // 2. Verificar regra de negócio: Livro já emprestado
        if (isBookOnLoan(bookId)) {
            throw new IllegalStateException("Livro com ID " + bookId + " já está emprestado e não pode ser emprestado novamente.");
        }

        // 3. Criar e salvar
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(daysToLend);
        Loan newLoan = new Loan(bookId, borrowerName, loanDate, dueDate);

        logger.info("Livro ID {} emprestado para {}.", bookId, borrowerName);
        return loanDAO.save(newLoan);
    }

    /**
     * Confirma a devolução de um livro (implica pagamento da multa se houver).
     */
    public void returnBook(Long loanId) {
        Loan loan = loanDAO.findById(loanId)
                .orElseThrow(() -> new IllegalStateException("Empréstimo com ID " + loanId + " não encontrado."));

        if (loan.returnDate() != null) {
            throw new IllegalStateException("Este empréstimo já foi devolvido em " + loan.returnDate());
        }

        // Cria um novo registro record com a data de devolução
        Loan returnedLoan = new Loan(
                loan.id(),
                loan.bookId(),
                loan.borrowerName(),
                loan.loanDate(),
                loan.dueDate(),
                LocalDate.now() // Define a data de devolução
        );

        loanDAO.save(returnedLoan);
        logger.info("Empréstimo ID {} devolvido com sucesso.", loanId);
    }

    /**
     * Retorna todos os empréstimos ativos (não devolvidos).
     */
    public List<Loan> getActiveLoans() {
        return loanDAO.findAllActiveLoans();
    }

    /**
     * Busca um empréstimo pelo ID.
     */
    public Loan getLoanById(Long loanId) {
        return loanDAO.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo com ID " + loanId + " não encontrado."));
    }

    /**
     * Calcula a multa para um empréstimo no momento da devolução (hoje).
     * Regra: R$5,00 fixo + R$0,50 por dia, a partir do 11º dia (inclusive).
     *
     * @param loan O empréstimo ativo a ser analisado.
     * @return DTO contendo os dias de atraso e o valor da multa.
     */
    public FineCalculation calculateFine(Loan loan) {
        Objects.requireNonNull(loan);
        if (loan.returnDate() != null) {
            return new FineCalculation(0, 0.0); // Já devolvido
        }

        LocalDate today = LocalDate.now();
        // Dias totais que o livro foi mantido, incluindo o dia de hoje
        // O ChronoUnit.DAYS.between calcula a diferença. Adicionar 1 para incluir o dia inicial.
        long totalDaysLoaned = ChronoUnit.DAYS.between(loan.loanDate(), today) + 1;

        if (totalDaysLoaned <= FREE_LOAN_DAYS) {
            // Dentro do prazo gratuito (até o 10º dia)
            return new FineCalculation(0, 0.0);
        }

        // Se totalDaysLoaned for 11, daysOverdue será 1 (11 - 10)
        long daysOverdue = totalDaysLoaned - FREE_LOAN_DAYS;

        // Multa: Base + (Dias de Atraso * Valor por Dia)
        double fine = FINE_BASE_AMOUNT + (daysOverdue * FINE_PER_DAY_AMOUNT);

        return new FineCalculation(daysOverdue, fine);
    }

    /**
     * DTO (Data Transfer Object) para retornar o cálculo da multa.
     */
    public record FineCalculation(long daysOverdue, double fineAmount) {
    }
}
package br.com.biblioteca.controller;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.model.Loan;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.LoanService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para as rotas de Empréstimo (Lending e Returning).
 */
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);
    private final LoanService loanService;
    private final BookService bookService;

    public LoanController(LoanService loanService, BookService bookService) {
        this.loanService = loanService;
        this.bookService = bookService;
    }

    public void registerRoutes(Javalin app) {
        app.get("/loans", this::handleListActiveLoans);
        app.get("/lend/{bookId}", this::handleShowLendForm);
        app.post("/lend", this::handleProcessLending);
        app.get("/return/{loanId}", this::handleShowReturnForm);
        app.post("/return/{loanId}", this::handleProcessReturn);
    }

    /**
     * GET /loans: Exibe a lista de empréstimos ativos.
     */
    private void handleListActiveLoans(Context ctx) {
        List<Loan> activeLoans = loanService.getActiveLoans();

        // Mapeia IDs de livros para seus detalhes (Título/Autor)
        Map<Long, Book> bookDetails = activeLoans.stream()
                .map(Loan::bookId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        bookService::getBookById
                ));

        Map<String, Object> model = new HashMap<>();
        model.put("loans", activeLoans);
        model.put("books", bookDetails);
        model.put("pageTitle", "Empréstimos Ativos");
        ctx.render("loans-list.html.html", model);
    }

    /**
     * GET /lend/{bookId}: Exibe o formulário para emprestar.
     */
    private void handleShowLendForm(Context ctx) {
        try {
            Long bookId = Long.parseLong(ctx.pathParam("bookId"));
            Book book = bookService.getBookById(bookId);

            if (loanService.isBookOnLoan(bookId)) {
                String errorMsg = URLEncoder.encode("Este livro já está emprestado.", StandardCharsets.UTF_8);
                ctx.redirect("/?error=" + errorMsg);
                return;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("book", book);
            model.put("borrowerName", "");
            model.put("daysToLend", 10); // Sugestão padrão
            model.put("pageTitle", "Emprestar Livro");
            ctx.render("loan-form.html", model);

        } catch (Exception e) {
            ctx.status(404).result("Erro ao carregar formulário: " + e.getMessage());
        }
    }

    /**
     * POST /lend: Processa o empréstimo.
     */
    private void handleProcessLending(Context ctx) {
        Long bookId = null;
        String borrowerName = ctx.formParam("borrowerName");
        int daysToLend = 10;

        try {
            bookId = Long.parseLong(ctx.formParam("bookId"));
            daysToLend = Integer.parseInt(ctx.formParam("daysToLend"));

            loanService.lendBook(bookId, borrowerName, daysToLend);
            ctx.redirect("/loans");

        } catch (Exception e) {
            // Em caso de erro, re-renderiza o formulário
            logger.warn("Erro ao processar empréstimo: {}", e.getMessage());
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("borrowerName", borrowerName);
            model.put("daysToLend", daysToLend);

            try {
                if (bookId != null) {
                    model.put("book", bookService.getBookById(bookId));
                }
            } catch (Exception ex) {
                // Se o bookId for inválido, ignora
            }
            model.put("pageTitle", "Emprestar Livro (Erro)");
            ctx.render("loan-form.html", model);
        }
    }

    /**
     * GET /return/{loanId}: Exibe a multa e confirmação de devolução.
     */
    private void handleShowReturnForm(Context ctx) {
        try {
            Long loanId = Long.parseLong(ctx.pathParam("loanId"));
            Loan loan = loanService.getLoanById(loanId);
            Book book = bookService.getBookById(loan.bookId());

            LoanService.FineCalculation fine = loanService.calculateFine(loan);

            Map<String, Object> model = new HashMap<>();
            model.put("loan", loan);
            model.put("book", book);
            model.put("fine", fine);
            model.put("pageTitle", "Devolver Livro");
            ctx.render("return-form.html", model);

        } catch (Exception e) {
            String errorMsg = URLEncoder.encode("Erro ao processar devolução: " + e.getMessage(), StandardCharsets.UTF_8);
            ctx.redirect("/loans?error=" + errorMsg);
        }
    }

    /**
     * POST /return/{loanId}: Confirma a devolução.
     */
    private void handleProcessReturn(Context ctx) {
        try {
            Long loanId = Long.parseLong(ctx.pathParam("loanId"));

            // O botão 'Confirmar' implica que o pagamento da multa foi efetuado.
            loanService.returnBook(loanId);
            String successMsg = URLEncoder.encode("Livro devolvido com sucesso!", StandardCharsets.UTF_8);
            ctx.redirect("/loans?success=" + successMsg);

        } catch (Exception e) {
            String errorMsg = URLEncoder.encode("Falha na devolução: " + e.getMessage(), StandardCharsets.UTF_8);
            ctx.redirect("/loans?error=" + errorMsg);
        }
    }
}
package br.com.biblioteca.controller;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.model.Loan;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.LoanService;
import br.com.biblioteca.service.exceptions.ValidationException;
import io.javalin.http.Handler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    public LoanController(LoanService loanService, BookService bookService) {
        this.loanService = loanService;
        this.bookService = bookService;
    }

    public Handler listView = ctx -> {
        List<Loan> loans = loanService.listAll();
        Map<String, Object> model = new HashMap<>();
        model.put("loans", loans);
        model.put("activeLoans", loanService.listActive());
        ctx.render("loans/list", model);
    };

    public Handler showCreateForm = ctx -> {
        Map<String, Object> model = new HashMap<>();
        model.put("loan", new Loan());
        model.put("books", bookService.findAll());
        model.put("errors", List.of());
        ctx.render("loans/form", model);
    };

    public Handler create = ctx -> {
        try {
            Long bookId = ctx.formParamAsClass("bookId", Long.class).getOrDefault(null);
            String borrower = ctx.formParam("borrower");
            int prazo = ctx.formParamAsClass("prazo", Integer.class).getOrDefault(10);
            loanService.createLoan(bookId, borrower, prazo);
            ctx.redirect("/loans");
        } catch (ValidationException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("loan", new Loan());
            model.put("books", bookService.findAll());
            model.put("errors", List.of(e.getMessage()));
            ctx.render("loans/form", model);
        }
    };

    public Handler showReturn = ctx -> {
        Long id = Long.valueOf(ctx.pathParam("id"));
        Loan loan = loanService.findById(id);
        Map<String, Object> model = new HashMap<>();
        model.put("loan", loan);
        model.put("today", LocalDate.now());
        ctx.render("loans/return", model);
    };

    public Handler attemptReturn = ctx -> {
        Long id = Long.valueOf(ctx.pathParam("id"));
        LocalDate returnDate = LocalDate.now();
        Loan loan = loanService.findById(id);
        BigDecimal fine = loanService.calculateFine(loan, returnDate);
        if (fine.compareTo(BigDecimal.ZERO) > 0 && !loan.isFinePaid()) {
            // show payment page
            Map<String, Object> model = new HashMap<>();
            model.put("loan", loan);
            model.put("fine", fine);
            ctx.render("loans/pay", model);
            return;
        }
        // no fine or already paid => process return
        loanService.attemptReturn(id, returnDate);
        ctx.redirect("/loans");
    };

    public Handler payFine = ctx -> {
        Long id = Long.valueOf(ctx.pathParam("id"));
        // Simula pagamento — em aplicação real integrar gateway
        loanService.payFine(id);
        // depois de pagar, processa devolução imediatamente com data atual
        loanService.attemptReturn(id, LocalDate.now());
        ctx.redirect("/loans");
    };
}
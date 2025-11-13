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
        this.loanService = loanService; this.bookService = bookService;
    }

    public Handler listView = ctx -> {
        List<Loan> loans = loanService.listAll();
        List<Loan> active = loanService.listActive();
        Map<String,Object> m = new HashMap<>();
        m.put("loans", loans); m.put("activeLoans", active); m.put("books", bookService.findAll());
        ctx.render("loans/list", m);
    };

    public Handler showCreateForm = ctx -> {
        Map<String,Object> m = new HashMap<>(); m.put("loan", new Loan()); m.put("books", bookService.findAll()); m.put("errors", List.of());
        ctx.render("loans/form", m);
    };

    public Handler create = ctx -> {
        try {
            Long bookId = ctx.formParamAsClass("bookId", Long.class).getOrDefault(null);
            String borrower = ctx.formParam("borrower");
            int prazo = ctx.formParamAsClass("prazo", Integer.class).getOrDefault(10);
            loanService.createLoan(bookId, borrower, prazo);
            ctx.redirect("/loans");
        } catch (ValidationException e) {
            Map<String,Object> m = new HashMap<>(); m.put("errors", List.of(e.getMessage())); m.put("books", bookService.findAll()); ctx.render("loans/form", m);
        }
    };

    public Handler showReturn = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Loan loan = loanService.findById(id);
        Map<String,Object> m = new HashMap<>(); m.put("loan", loan); m.put("today", LocalDate.now()); ctx.render("loans/return", m);
    };

    public Handler attemptReturn = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Loan loan = loanService.findById(id);
        BigDecimal fine = loanService.calculateFine(loan, LocalDate.now());
        if (fine.compareTo(BigDecimal.ZERO) > 0 && !loan.isFinePaid()) {
            Map<String,Object> m = new HashMap<>(); m.put("loan", loan); m.put("fine", fine); ctx.render("loans/pay", m);
            return;
        }
        loanService.attemptReturn(id, LocalDate.now());
        ctx.redirect("/loans");
    };

    public Handler payFine = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        loanService.payFine(id);
        loanService.attemptReturn(id, LocalDate.now());
        ctx.redirect("/loans");
    };
}

package br.com.biblioteca;

import br.com.biblioteca.controller.BookController;
import br.com.biblioteca.controller.LoanController;
import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.InMemoryBookRepository;
import br.com.biblioteca.repository.InMemoryLoanRepository;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.LoanService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class App {
    public static void main(String[] args) {
        InMemoryBookRepository repo = new InMemoryBookRepository();
        BookService bookService = new BookService(repo);

        InMemoryLoanRepository loanRepo = new InMemoryLoanRepository();
        LoanService loanService = new LoanService(loanRepo, bookService);

        // controllers
        BookController bookController = new BookController(bookService, loanService);
        LoanController loanController = new LoanController(loanService, bookService);

        // Seed sample data
        try {
            repo.save(new Book(null, "Clean Code", "Robert C. Martin", "9780132350884"));
            repo.save(new Book(null, "Effective Java", "Joshua Bloch", "9780134685991"));
        } catch (Exception e) {
            // ignore if seeded incorrectly
        }

        Javalin app = Javalin.create(cfg -> {
            cfg.staticFiles.add("/static", Location.CLASSPATH);
            cfg.contextPath = "/";
        }).start(7000);

        // Book routes
        app.get("/", bookController.listView);
        app.get("/books/new", bookController.showCreateForm);
        app.post("/books", bookController.create);
        app.get("/books/:id", bookController.view);
        app.get("/books/:id/edit", bookController.showEditForm);
        app.post("/books/:id", bookController.update);
        app.post("/books/:id/delete", bookController.delete);

        // Loan routes
        app.get("/loans", loanController.listView);
        app.get("/loans/new", loanController.showCreateForm);
        app.post("/loans", loanController.create);
        app.get("/loans/:id/return", loanController.showReturn);
        app.post("/loans/:id/return", loanController.attemptReturn);
        app.post("/loans/:id/pay", loanController.payFine);

        System.out.println("Aplicação iniciada em http://localhost:7000");
    }
}

package br.com.biblioteca.controller;

import br.com.biblioteca.model.Book;
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
 * Controlador para as rotas de Livro.
 * Inclui a lógica para exibir o status de empréstimo na lista principal.
 */
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;
    private final LoanService loanService;

    public BookController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    /**
     * Registra todas as rotas de Livro na aplicação Javalin.
     */
    public void registerRoutes(Javalin app) {
        app.get("/", this::handleListAndSearchBooks);
        app.get("/search", this::handleListAndSearchBooks);
        app.get("/new", this::handleShowNewForm);
        app.post("/books", this::handleCreateBook);
        app.get("/edit/{id}", this::handleShowEditForm);
        app.post("/update/{id}", this::handleUpdateBook);
        app.post("/delete/{id}", this::handleDeleteBook);
    }

    /**
     * Manipulador para GET / e GET /search
     * **(Lógica atualizada para incluir status de empréstimo)**
     */
    private void handleListAndSearchBooks(Context ctx) {
        String query = ctx.queryParam("query");
        String error = ctx.queryParam("error");

        Map<String, Object> model = new HashMap<>();
        List<Book> books;

        if (query != null && !query.isBlank()) {
            books = bookService.searchBooks(query);
            model.put("query", query);
        } else {
            books = bookService.getAllBooks();
        }

        // **NOVA LÓGICA: Verificar status de empréstimo de cada livro**
        Map<Long, Boolean> loanStatus = books.stream()
                .collect(Collectors.toMap(
                        Book::id,
                        book -> loanService.isBookOnLoan(book.id())
                ));

        model.put("books", books);
        model.put("loanStatus", loanStatus);
        model.put("pageTitle", "Acervo de Livros");

        if (error != null && !error.isBlank()) {
            model.put("error", error);
        }

        ctx.render("index.html", model);
    }

    /**
     * Manipulador para POST /delete/{id}
     * **(Lógica atualizada para tratar a regra de livro emprestado)**
     */
    private void handleDeleteBook(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            bookService.deleteBook(id);
            ctx.redirect("/");
        } catch (Exception e) {
            logger.warn("Falha ao excluir livro: {}", e.getMessage());
            // Captura o erro (incluindo IllegalStateException de livro emprestado) e passa via query param
            String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            ctx.redirect("/?error=" + errorMessage);
        }
    }

    private void handleShowNewForm(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("book", new Book(null, "", "", ""));
        model.put("isNew", true);
        model.put("pageTitle", "Novo Livro");
        ctx.render("book-form.html", model);
    }

    private void handleCreateBook(Context ctx) {
        String title = ctx.formParam("title");
        String author = ctx.formParam("author");
        String isbn = ctx.formParam("isbn");

        try {
            bookService.addBook(title, author, isbn);
            ctx.redirect("/");
        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("book", new Book(title, author, isbn));
            model.put("isNew", true);
            model.put("pageTitle", "Novo Livro (Erro)");
            ctx.render("book-form.html", model);
        }
    }

    private void handleShowEditForm(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Book book = bookService.getBookById(id);

            Map<String, Object> model = new HashMap<>();
            model.put("book", book);
            model.put("isNew", false);
            model.put("pageTitle", "Editar Livro");
            ctx.render("book-form.html", model);
        } catch (Exception e) {
            ctx.status(404).result("Erro: " + e.getMessage());
        }
    }

    private void handleUpdateBook(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        String title = ctx.formParam("title");
        String author = ctx.formParam("author");
        String isbn = ctx.formParam("isbn");

        try {
            bookService.updateBook(id, title, author, isbn);
            ctx.redirect("/");
        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("book", new Book(id, title, author, isbn));
            model.put("isNew", false);
            model.put("pageTitle", "Editar Livro (Erro)");
            ctx.render("book-form.html", model);
        }
    }
}
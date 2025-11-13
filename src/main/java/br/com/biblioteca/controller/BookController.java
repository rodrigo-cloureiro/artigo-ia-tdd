package br.com.biblioteca.controller;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.service.BookService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Camada de Controle (Controller)
 * Mapeia as rotas HTTP (endpoints) para os métodos do BookService.
 * Prepara o modelo (Map) para o Thymeleaf renderizar a View.
 */
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Registra todas as rotas de Livro na aplicação Javalin.
     * @param app A instância do Javalin.
     */
    public void registerRoutes(Javalin app) {
        app.get("/", this::handleListAndSearchBooks);
        app.get("/search", this::handleListAndSearchBooks);
        app.get("/new", this::handleShowNewForm);
        app.post("/books", this::handleCreateBook);
        app.get("/edit/{id}", this::handleShowEditForm);
        app.post("/update/{id}", this::handleUpdateBook);
        app.post("/delete/{id}", this::handleDeleteBook); // Usando POST para exclusão por simplicidade com formulários HTML
    }

    /**
     * Manipulador para GET / e GET /search
     * Lista todos os livros ou filtra com base em um parâmetro de consulta.
     */
    private void handleListAndSearchBooks(Context ctx) {
        String query = ctx.queryParam("query");
        Map<String, Object> model = new HashMap<>();

        if (query != null && !query.isBlank()) {
            model.put("books", bookService.searchBooks(query));
            model.put("query", query);
        } else {
            model.put("books", bookService.getAllBooks());
        }

        ctx.render("index.html", model);
    }

    /**
     * Manipulador para GET /new
     * Exibe o formulário de criação de um novo livro.
     */
    private void handleShowNewForm(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        // Envia um "livro" vazio para preencher o th:object e evitar erros
        model.put("book", new Book(null, "", "", ""));
        model.put("isNew", true);
        ctx.render("book-form.html", model);
    }

    /**
     * Manipulador para POST /books
     * Processa o formulário de criação de novo livro.
     */
    private void handleCreateBook(Context ctx) {
        String title = ctx.formParam("title");
        String author = ctx.formParam("author");
        String isbn = ctx.formParam("isbn");

        try {
            bookService.addBook(title, author, isbn);
            ctx.redirect("/"); // Redireciona para a lista em caso de sucesso
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao criar livro: {}", e.getMessage());
            // Em caso de erro, renderiza o formulário novamente com a mensagem de erro
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("book", new Book(title, author, isbn)); // Devolve os dados preenchidos
            model.put("isNew", true);
            ctx.render("book-form.html", model);
        }
    }

    /**
     * Manipulador para GET /edit/{id}
     * Exibe o formulário de edição para um livro existente.
     */
    private void handleShowEditForm(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Book book = bookService.getBookById(id);

            Map<String, Object> model = new HashMap<>();
            model.put("book", book);
            model.put("isNew", false);
            ctx.render("book-form.html", model);
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID inválido.");
        } catch (IllegalArgumentException e) {
            ctx.status(404).result(e.getMessage());
        }
    }

    /**
     * Manipulador para POST /update/{id}
     * Processa o formulário de atualização de um livro.
     */
    private void handleUpdateBook(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        String title = ctx.formParam("title");
        String author = ctx.formParam("author");
        String isbn = ctx.formParam("isbn");

        try {
            bookService.updateBook(id, title, author, isbn);
            ctx.redirect("/"); // Sucesso
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao atualizar livro {}: {}", id, e.getMessage());
            // Erro de validação ou de negócio
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("book", new Book(id, title, author, isbn)); // Devolve os dados
            model.put("isNew", false);
            ctx.render("book-form.html", model);
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar livro {}: {}", id, e.getMessage());
            ctx.status(500).result("Erro interno no servidor.");
        }
    }

    /**
     * Manipulador para POST /delete/{id}
     * Exclui um livro.
     */
    private void handleDeleteBook(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            bookService.deleteBook(id);
        } catch (NumberFormatException e) {
            logger.warn("Tentativa de exclusão com ID inválido: {}", ctx.pathParam("id"));
            // Ignora silenciosamente ou envia um erro
        } catch (IllegalArgumentException e) {
            logger.warn("Tentativa de exclusão de livro não existente: {}", e.getMessage());
            // Ignora silenciosamente
        }
        ctx.redirect("/"); // Sempre redireciona para a home
    }
}
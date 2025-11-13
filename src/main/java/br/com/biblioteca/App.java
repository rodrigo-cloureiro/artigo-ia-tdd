package br.com.biblioteca;

import br.com.biblioteca.controller.BookController;
import br.com.biblioteca.controller.LoanController;
import br.com.biblioteca.db.Database;
import br.com.biblioteca.repository.JdbcBookRepository;
import br.com.biblioteca.repository.JdbcLoanRepository;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.LoanService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // init DB and fixtures
        Database db = Database.getInstance();
        db.loadFixturesIfPresent();

        var bookRepo = new JdbcBookRepository();
        var loanRepo = new JdbcLoanRepository();
        var bookService = new BookService(bookRepo);
        var loanService = new LoanService(loanRepo, bookService);

        var bookController = new BookController(bookService, loanService);
        var loanController = new LoanController(loanService, bookService);

        TemplateEngine engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        engine.setTemplateResolver(resolver);

        Javalin app = Javalin.create(cfg -> {
            cfg.staticFiles.add("/static", Location.CLASSPATH);
            cfg.fileRenderer(new JavalinThymeleaf(engine));
        }).start(7000);

        // global exception mapping
        app.exception(Exception.class, (e, ctx) -> {
            log.error("Erro não tratado", e);
            if (ctx.header("Accept") != null && ctx.header("Accept").contains("application/json")) {
                ctx.status(500).json(new ErrorResponse("Erro interno"));
            } else {
                ctx.status(500).result("Erro interno: " + e.getMessage());
            }
        });

        // routes
        app.get("/", bookController.listView);
        app.get("/books/new", bookController.showCreateForm);
        app.post("/books", bookController.create);
        app.get("/books/{id}", bookController.view);
        app.get("/books/{id}/edit", bookController.showEditForm);
        app.post("/books/{id}", bookController.update);
        app.post("/books/{id}/delete", bookController.delete);

        app.get("/loans", loanController.listView);
        app.get("/loans/new", loanController.showCreateForm);
        app.post("/loans", loanController.create);
        app.get("/loans/{id}/return", loanController.showReturn);
        app.post("/loans/{id}/return", loanController.attemptReturn);
        app.post("/loans/{id}/pay", loanController.payFine);

        System.out.println("Aplicação iniciada em http://localhost:7000");
    }

    static class ErrorResponse {
        public final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}

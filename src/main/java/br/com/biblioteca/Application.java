package br.com.biblioteca;

import br.com.biblioteca.config.ThymeleafConfig;
import br.com.biblioteca.controller.BookController;
import br.com.biblioteca.controller.LoanController;
import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.dao.InMemoryBookDAO;
import br.com.biblioteca.dao.LoanDAO;
import br.com.biblioteca.dao.InMemoryLoanDAO;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.LoanService;
import br.com.biblioteca.service.ValidationService;
import br.com.biblioteca.util.DataLoader;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 7070;

    public static void main(String[] args) {
        // 1. Injeção de Dependências
        BookDAO bookDAO = new InMemoryBookDAO();
        LoanDAO loanDAO = new InMemoryLoanDAO();

        ValidationService validationService = new ValidationService();
        BookService bookService = new BookService(bookDAO, validationService);
        LoanService loanService = new LoanService(loanDAO, bookDAO);

        bookService.setLoanService(loanService);

        BookController bookController = new BookController(bookService, loanService);
        LoanController loanController = new LoanController(loanService, bookService);

        // 2. Configuração do Javalin com Thymeleaf
        JavalinThymeleaf thymeleafRenderer = ThymeleafConfig.configure();
        Javalin app = Javalin.create(config -> {
            config.fileRenderer(thymeleafRenderer);
            config.bundledPlugins.enableDevLogging();
        });

        // 3. Configuração das exceções
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            logger.warn("Erro de validação ou argumento: {}", e.getMessage());
            ctx.status(400).result("Erro de requisição: " + e.getMessage());
        });

        app.exception(IllegalStateException.class, (e, ctx) -> {
            logger.warn("Erro de estado do sistema: {}", e.getMessage());
            String encodedError = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            ctx.redirect("/?error=" + encodedError);
        });

        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Erro interno inesperado: {}", e.getMessage(), e);
            ctx.status(500).result("Ocorreu um erro interno no servidor.");
        });

        // 4. Registro das Rotas
        bookController.registerRoutes(app);
        loanController.registerRoutes(app);

        // 5. Iniciar o servidor
        app.start(PORT);
        logger.info("Servidor da Biblioteca iniciado em http://localhost:{}", PORT);
        populateInitialData(bookService);
    }

    private static void populateInitialData(BookService bookService) {
        try {
            bookService.addBook("O Senhor dos Anéis", "J.R.R. Tolkien", "9788535902796");
            bookService.addBook("O Guia do Mochileiro das Galáxias", "Douglas Adams", "9788575422465");
            bookService.addBook("1984", "George Orwell", "9788535914843");
        } catch (Exception e) {
            // Ignora se já existirem
        }

        DataLoader.loadInitialData(bookService.getBookDAO());
    }
}

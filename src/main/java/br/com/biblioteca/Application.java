package br.com.biblioteca;

import br.com.biblioteca.controller.BookController;
import br.com.biblioteca.controller.LoanController;
import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.dao.InMemoryBookDAO;
import br.com.biblioteca.dao.LoanDAO;
import br.com.biblioteca.dao.InMemoryLoanDAO;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.LoanService;
import br.com.biblioteca.service.ValidationService;
import io.javalin.Javalin;
import io.javalin.community.plugins.thymeleaf.ThymeleafPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;

/**
 * Classe principal da aplicação.
 * Configura e inicia o servidor Javalin e registra os controladores.
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 7070;

    public static void main(String[] args) {
        // 1. Injeção de Dependências

        // Camada DAO
        BookDAO bookDAO = new InMemoryBookDAO();
        LoanDAO loanDAO = new InMemoryLoanDAO();

        // Camada de Serviço
        ValidationService validationService = new ValidationService();
        BookService bookService = new BookService(bookDAO, validationService);
        LoanService loanService = new LoanService(loanDAO, bookDAO);

        // Injeção da dependência para a validação de exclusão
        bookService.setLoanService(loanService);

        // Camada de Controle
        BookController bookController = new BookController(bookService, loanService);
        LoanController loanController = new LoanController(loanService, bookService);

        // 2. Configuração do Javalin
        Javalin app = Javalin.create(config -> {
            config.plugins.register(ThymeleafPlugin.create(engine -> {
                var resolver = new ClassLoaderResourceResolver();
                resolver.setPrefix("/templates/");
                resolver.setSuffix(".html");
                engine.setTemplateResolver(resolver);
            }));
            config.staticFiles.add("/public");
        });

        // 3. Registro das Rotas
        bookController.registerRoutes(app);
        loanController.registerRoutes(app);

        // 4. Iniciar o servidor
        app.start(PORT);
        logger.info("Servidor da Biblioteca iniciado em http://localhost:{}", PORT);

        populateInitialData(bookService);
    }

    private static void populateInitialData(BookService bookService) {
        try {
            bookService.addBook("O Senhor dos Anéis", "J.R.R. Tolkien", "9788535902796");
            bookService.addBook("O Guia do Mochileiro das Galáxias", "Douglas Adams", "9788575422465");
            bookService.addBook("1984", "George Orwell", "9788535914843");
        } catch (IllegalArgumentException e) {
            // Ignora
        }
    }
}
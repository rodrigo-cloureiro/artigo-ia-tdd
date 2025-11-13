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
import br.com.biblioteca.util.DataLoader;
import io.javalin.Javalin;
import io.javalin.community.plugins.thymeleaf.ThymeleafPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

            // Tratamento de Erros (Handles)
            config.router.apiBuilder(() -> {
                // Manipulador para entradas inválidas (e erros de validação da Service Layer)
                app.exception(IllegalArgumentException.class, (e, ctx) -> {
                    logger.warn("Erro de validação ou argumento: {}", e.getMessage());
                    ctx.status(400).result("Erro de requisição: " + e.getMessage());
                });

                // Manipulador para erros de estado (Livro Emprestado, etc.)
                app.exception(IllegalStateException.class, (e, ctx) -> {
                    logger.warn("Erro de estado do sistema: {}", e.getMessage());
                    // Redireciona para a home com a mensagem, comum em POSTs
                    String encodedError = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                    ctx.redirect("/?error=" + encodedError);
                });

                // Manipulador para not found (Livro ou Rota não existe)
                app.exception(Exception.class, (e, ctx) -> {
                    logger.error("Erro interno inesperado: {}", e.getMessage(), e);
                    ctx.status(500).result("Ocorreu um erro interno no servidor.");
                });
            });
        });

        // 3. Registro das Rotas
        bookController.registerRoutes(app);
        loanController.registerRoutes(app);

        // 4. Iniciar o servidor
        app.start(PORT);
        logger.info("Servidor da Biblioteca iniciado em http://localhost:{}", PORT);
        populateInitialData(bookService);
    }

    // Altera a função de população para usar o DataLoader
    private static void populateInitialData(BookService bookService) {
        // Adiciona dados fixos para garantir base
        try {
            bookService.addBook("O Senhor dos Anéis", "J.R.R. Tolkien", "9788535902796");
            bookService.addBook("O Guia do Mochileiro das Galáxias", "Douglas Adams", "9788575422465");
            bookService.addBook("1984", "George Orwell", "9788535914843");
        } catch (Exception e) {
            // Ignora se já existirem
        }

        // Carrega dados do CSV
        DataLoader.loadInitialData(bookService.getBookDAO()); // Necessário metodo getBookDAO na BookService
    }
}
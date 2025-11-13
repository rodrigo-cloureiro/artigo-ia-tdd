package br.com.biblioteca;

import br.com.biblioteca.controller.BookController;
import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.dao.InMemoryBookDAO;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.ValidationService;
import io.javalin.Javalin;
import io.javalin.community.plugins.thymeleaf.ThymeleafPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;

/**
 * Classe principal da aplicação.
 * Configura e inicia o servidor Javalin, registra o template engine Thymeleaf
 * e injeta as dependências (DAO, Service, Controller).
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 7070;

    public static void main(String[] args) {
        // 1. Injeção de Dependências (manual, estilo "Pure DI")
        BookDAO bookDAO = new InMemoryBookDAO();
        ValidationService validationService = new ValidationService();
        BookService bookService = new BookService(bookDAO, validationService);
        BookController bookController = new BookController(bookService);

        // 2. Configuração do Javalin
        Javalin app = Javalin.create(config -> {
            logger.info("Configurando o Javalin...");

            // Configuração do plugin do Thymeleaf
            config.plugins.register(ThymeleafPlugin.create(engine -> {
                var resolver = new ClassLoaderResourceResolver();
                resolver.setPrefix("/templates/");
                resolver.setSuffix(".html");
                engine.setTemplateResolver(resolver);
                logger.info("Thymeleaf plugin registrado com prefixo /templates/ e sufixo .html");
            }));

            // Habilitar logging de requisições
            config.requestLogger.http((ctx, ms) -> {
                logger.debug("{} {} ({}ms)", ctx.method(), ctx.path(), ms);
            });

            // Adicionar suporte para arquivos estáticos (CSS, JS) se necessário
            config.staticFiles.add("/public");
        });

        // 3. Registro das Rotas (definidas no Controller)
        logger.info("Registrando rotas da aplicação...");
        bookController.registerRoutes(app);

        // 4. Iniciar o servidor
        app.start(PORT);
        logger.info("Servidor da Biblioteca iniciado em http://localhost:{}", PORT);

        // Adicionar alguns dados de exemplo
        populateInitialData(bookService);
    }

    private static void populateInitialData(BookService bookService) {
        try {
            logger.debug("Populando dados iniciais...");
            bookService.addBook("O Senhor dos Anéis", "J.R.R. Tolkien", "9788535902796");
            bookService.addBook("O Guia do Mochileiro das Galáxias", "Douglas Adams", "9788575422465");
            bookService.addBook("1984", "George Orwell", "9788535914843");
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao popular dados iniciais (talvez já existam): {}", e.getMessage());
        }
    }
}
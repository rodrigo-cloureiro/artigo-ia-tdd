package com.biblioteca;

import com.biblioteca.config.ThymeleafConfig;
import com.biblioteca.controller.EmprestimoController;
import com.biblioteca.controller.LivroController;
import com.biblioteca.exception.GlobalExceptionHandler;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.EmprestimoService;
import com.biblioteca.service.LivroService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class BibliotecaApp {
    public static void main(String[] args) {
        // Configuração de dependências
        LivroRepository livroRepository = new LivroRepository();
        EmprestimoRepository emprestimoRepository = new EmprestimoRepository();

        LivroService livroService = new LivroService(livroRepository, null);
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository, livroService);

        // Atualizar dependência circular
        livroService = new LivroService(livroRepository, emprestimoService);

        LivroController livroController = new LivroController(livroService);
        EmprestimoController emprestimoController = new EmprestimoController(emprestimoService, livroService);

        // Configurar Thymeleaf
        ThymeleafConfig.configure();

        // Criar aplicação Javalin com configurações de segurança
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });

            config.bundledPlugins.enableDevLogging();
        });

        // Configurar tratamento global de exceções
        app.exception(Exception.class, GlobalExceptionHandler.GENERAL_EXCEPTION_HANDLER);
        app.exception(IllegalArgumentException.class, GlobalExceptionHandler.VALIDATION_EXCEPTION_HANDLER);
        app.exception(SecurityException.class, GlobalExceptionHandler.SECURITY_EXCEPTION_HANDLER);

        // Configurar rotas de Livros
        app.get("/", ctx -> ctx.redirect("/livros"));
        app.get("/livros", livroController::listarLivros);
        app.get("/livros/buscar", livroController::buscarLivros);
        app.get("/livros/novo", livroController::mostrarFormularioCadastro);
        app.post("/livros", livroController::cadastrarLivro);
        app.get("/livros/editar/{id}", livroController::mostrarFormularioEdicao);
        app.post("/livros/editar/{id}", livroController::atualizarLivro);
        app.get("/livros/deletar/{id}", livroController::deletarLivro);

        // Configurar rotas de Empréstimos
        app.get("/emprestimos", emprestimoController::listarEmprestimos);
        app.get("/emprestimos/ativos", emprestimoController::listarEmprestimosAtivos);
        app.get("/emprestimos/atrasados", emprestimoController::listarEmprestimosAtrasados);
        app.get("/emprestimos/buscar", emprestimoController::buscarEmprestimosPorUsuario);
        app.get("/emprestimos/novo", emprestimoController::mostrarFormularioEmprestimo);
        app.post("/emprestimos/novo", emprestimoController::realizarEmprestimo);
        app.get("/emprestimos/{id}", emprestimoController::mostrarDetalhesEmprestimo);
        app.get("/emprestimos/{id}/devolver", emprestimoController::registrarDevolucao);
        app.get("/emprestimos/{id}/pagar-multa", emprestimoController::pagarMulta);

        // Inicializar aplicação
        app.start(7070);

        System.out.println("=== Sistema Biblioteca CRUD ===");
        System.out.println("Aplicação rodando em: http://localhost:7070");
        System.out.println("Livros: http://localhost:7070/livros");
        System.out.println("Empréstimos: http://localhost:7070/emprestimos");
        System.out.println("=================================");
    }
}
package com.biblioteca;

import com.biblioteca.config.ThymeleafConfig;
import com.biblioteca.controller.LivroController;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.LivroService;
import io.javalin.Javalin;

public class BibliotecaApp {
    public static void main(String[] args) {
        // Configuração de dependências
        LivroRepository repository = new LivroRepository();
        LivroService service = new LivroService(repository);
        LivroController controller = new LivroController(service);

        // Configurar Thymeleaf
        ThymeleafConfig.configure();

        // Criar aplicação Javalin
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        });

        // Configurar rotas
        app.get("/", ctx -> ctx.redirect("/livros"));

        app.get("/livros", controller::listarLivros);
        app.get("/livros/buscar", controller::buscarLivros);
        app.get("/livros/novo", controller::mostrarFormularioCadastro);
        app.post("/livros", controller::cadastrarLivro);
        app.get("/livros/editar/{id}", controller::mostrarFormularioEdicao);
        app.post("/livros/editar/{id}", controller::atualizarLivro);
        app.get("/livros/deletar/{id}", controller::deletarLivro);

        // Inicializar aplicação
        app.start(7070);

        System.out.println("Aplicação da Biblioteca rodando em: http://localhost:7070");
    }
}